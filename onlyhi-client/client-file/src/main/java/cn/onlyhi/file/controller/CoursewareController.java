package cn.onlyhi.file.controller;

import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.po.Courseware;
import cn.onlyhi.client.po.CoursewareImage;
import cn.onlyhi.client.service.RedisService;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.enums.ClientEnum;
import cn.onlyhi.common.util.*;
import cn.onlyhi.file.request.ConverRequest;
import cn.onlyhi.file.request.UploadCoursewareRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static cn.onlyhi.common.constants.Constants.COURSEWARE_ROOT;
import static cn.onlyhi.common.constants.Constants.FILESEPARATOR;
import static cn.onlyhi.common.constants.Constants.UPLOAD_ROOT;
import static cn.onlyhi.common.enums.ClientEnum.UserType.TEACHER;
import static cn.onlyhi.common.enums.CodeEnum.*;
import static cn.onlyhi.common.util.ClientUtil.sendWebsocketMessageToUser;
import static cn.onlyhi.common.util.FileUtils.matchFileType;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2018/3/19.
 */
@RestController
@RequestMapping("/courseware")
public class CoursewareController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoursewareController.class);
    private static final List<String> suffixList = Arrays.asList("ppt", "pptx", "doc", "docx", "pdf", "png", "jpeg", "jpg");
    @Autowired
    private RedisService redisService;
    @Autowired
    private HttpServletRequest req;

    @PostMapping("/uploadFile")
    @LogRecordAnnotation(moduleCode = 100009, moduleName = "课件管理", methodCode = 1000095, methodName = "uploadFile", description = "教师课件上传", userTypes = TEACHER)
    public ResponseEntity<Response> uploadFile(UploadCoursewareRequest request) throws Exception {
        MultipartFile originalFile = request.getOriginalFile();
        String token = request.getToken();
        LoginUserCache loginUserCache = redisService.getLoginUserCache(token);
        String teacherUuid = loginUserCache.getUserUuid();

        if (originalFile == null || originalFile.getSize() == 0) {
            return ResponseEntity.ok(Response.error(NULL_FILE));
        }

        String originalFilename = originalFile.getOriginalFilename();
        LOGGER.info("课件上传,原文件:{}", originalFilename);
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (!matchFileType(originalFile.getInputStream(), suffix) || !suffixList.contains(suffix.toLowerCase())) { //文件后缀是否符合
            return ResponseEntity.ok(Response.error(INVALID_FILE));
        }

        String streamMD5 = SecurityUtil.hashMD5Hex(originalFile.getInputStream());
        int count = checkFileRepeat(teacherUuid, streamMD5);
        if (count > 0) {
            return ResponseEntity.ok(Response.error(REPEAT_FILE));
        }

        MultipartFile pdfFile = request.getPdfFile();
        String pdfFileOriginalFilename = null;
        if (pdfFile != null && pdfFile.getSize() > 0) {
            pdfFileOriginalFilename = pdfFile.getOriginalFilename();
            String pdfSuffix = pdfFileOriginalFilename.substring(pdfFileOriginalFilename.lastIndexOf(".") + 1);
            if (!matchFileType(pdfFile.getInputStream(), "pdf") || !"pdf".equalsIgnoreCase(pdfSuffix)) { //pdf文件后缀是否符合
                return ResponseEntity.ok(Response.error(INVALID_FILE));
            }
        }
        LOGGER.info("课件上传,pdf文件:{}", pdfFileOriginalFilename);
        String saveDir =null;
        if(teacherUuid!=null){
            saveDir = COURSEWARE_ROOT.concat(FILESEPARATOR).concat(UUIDUtil.noSplit(teacherUuid));
        }else {
            LOGGER.error("老师登录后php未返回userUuid");
            return ResponseEntity.ok(Response.error(INVALID_IDENTITY));
        }

        String timeName = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS");
        File saveFile = new File(saveDir, timeName + "." + suffix);
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }
        //上传文件
        String saveFilePath = saveFile.getPath();
        //LOGGER.info("dir of originalSaveFile is {}", saveFilePath);
        Files.copy(originalFile.getInputStream(), Paths.get(saveDir, saveFile.getName()));
        File pdfSaveFile = new File(saveDir, timeName + ".pdf");
        if (pdfFile != null && pdfFile.getSize() > 0) {
            String pdfSaveFilePath = pdfSaveFile.getPath();
            //LOGGER.info("dir of pdfSaveFilePath is {}", pdfSaveFilePath);
            Files.copy(pdfFile.getInputStream(), Paths.get(saveDir, pdfSaveFile.getName()));
        }

        //课件重命名
        String coursewareName = getCoursewareName(originalFilename, teacherUuid);
        LOGGER.info("课件重命名:{}", coursewareName);
        //保存数据
        String coursewareUuid = UUIDUtil.randomUUID2();
        Courseware courseware = new Courseware();
        courseware.setCoursewareUuid(coursewareUuid);
        courseware.setTeacherUuid(teacherUuid);
        courseware.setOriginalName(originalFilename);
        courseware.setCoursewareName(coursewareName);
        //LOGGER.info("courseware.setConverStatus(1)");
        courseware.setConverStatus(1);
        courseware.setStreamMd5(streamMD5);
        int i = coursewareService.save(courseware);
        if (i < 1) {
            return ResponseEntity.ok(Response.error(FILE_UPLOAD_FAIL));
        }

        LOGGER.info("文件转换中!");
        String message = "1";   //课件正在转换中的状态
        String type = "1";  //表示发送课件转换状态消息
        String websocketUrl = ymlMyConfig.getWebsocketUrl();
        sendWebsocketMessageToUser(token, type, message, websocketUrl);
        if ("png".equalsIgnoreCase(suffix) || "jpg".equalsIgnoreCase(suffix) || "jpeg".equalsIgnoreCase(suffix)) {
            //LOGGER.info("图片不需要转，直接保存");
            new Thread(() -> {
                String coursewareUrl = ymlMyConfig.getUploadAddress() + saveFilePath.substring(saveFilePath.indexOf(UPLOAD_ROOT));
                String coursewareType = ClientEnum.CoursewareType.TEACHER.name();
                courseware.setCoursewareUrl(coursewareUrl);
                courseware.setCoursewareType(coursewareType);
                courseware.setCoursewarePreviewUrl(coursewareUrl);
                courseware.setPageNum(1);
                courseware.setCoursewarePath(saveFilePath);
                courseware.setConverStatus(2);
                int j = coursewareService.update(courseware);
                if (j > 0) { //更新成功
                    //将图片保存到课件图片表
                    //获取图片的宽高
                    //LOGGER.info("更新成功,将图片保存到课件图片表、获取图片的宽高");
                    File imageFile = new File(saveFilePath);
                    BufferedImage bufferedImage = null;
                    try {
                        bufferedImage = ImageIO.read(imageFile);
                        int imageWidth = bufferedImage.getWidth();
                        int imageHeight = bufferedImage.getHeight();
                        CoursewareImage coursewareImage = new CoursewareImage();
                        coursewareImage.setCoursewareImageUuid(UUIDUtil.randomUUID2());
                        coursewareImage.setCoursewareUuid(coursewareUuid);
                        coursewareImage.setImageUrl(coursewareUrl);
                        coursewareImage.setImageName(imageFile.getName());
                        coursewareImage.setWidth(imageWidth);
                        coursewareImage.setHeight(imageHeight);
                        coursewareImage.setImagePath(saveFilePath);
                        String imageMd5 = ClientUtil.md5(imageFile);
                        coursewareImage.setImageMd5(imageMd5);
                        coursewareImageService.save(coursewareImage);
                        sendWebsocketMessageToUser(token, type, "2", websocketUrl);
                    } catch (IOException e) {
                        LOGGER.info("读取图片异常:{}", e);
                        sendWebsocketMessageToUser(token, type, "3", websocketUrl);
                        LOGGER.error(e.getMessage());
                    }
                }
            }).start();
        } else if ("pdf".equalsIgnoreCase(suffix)) {
            //LOGGER.info("pdf直接转成图片");
            new Thread(() -> {
                int converStatus = converAndSaveCourseware(saveFile, coursewareUuid, true);
                sendWebsocketMessageToUser(token, type, String.valueOf(converStatus), websocketUrl);
            }).start();
        } else {
            //LOGGER.info("先转为pdf再转成图片");
            new Thread(() -> {
                int converStatus = 3;
                if (pdfFile != null && pdfFile.getSize() > 0) {  //上传了pdf文件
                    converStatus = converAndSaveCourseware(pdfSaveFile, coursewareUuid, true);
                    //更新原文件路径
                    Courseware courseware1 = new Courseware();
                    courseware1.setCoursewareUuid(coursewareUuid);
                    String coursewareUrl = ymlMyConfig.getUploadAddress() + saveFilePath.substring(saveFilePath.indexOf(UPLOAD_ROOT));
                    courseware1.setCoursewareUrl(coursewareUrl);
                    coursewareService.update(courseware1);
                } else {
                    converStatus = converAndSaveCourseware(saveFile, coursewareUuid, false);
                }
                sendWebsocketMessageToUser(token, type, String.valueOf(converStatus), websocketUrl);
            }).start();
        }
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 课件转换结果回调
     *
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/converResultByJson")
    @LogRecordAnnotation(moduleCode = 100009, moduleName = "课件管理", methodCode = 1000097, methodName = "converResultByJson", description = "课件转换结果回调", checkToken = false)
    public ResponseEntity<Response> converResultByJson(@RequestBody ConverRequest request) {
        int converStatus = request.getConverStatus();
        LOGGER.info("converStatus={}", converStatus);
        if (converStatus == 2) {
            // TODO: 2018/4/27 将数据保存到数据库中
        }
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 文件上传与转换分开
     * （上传，转换流程不变）新增uploadFileNew
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/uploadFileNew")
    @LogRecordAnnotation(moduleCode = 100009, moduleName = "课件管理", methodCode = 1000095, methodName = "uploadFile", description = "教师课件上传（新）", userTypes = TEACHER)
    public ResponseEntity<Response> uploadFileNew(UploadCoursewareRequest request) {
        LOGGER.info("uploadFileNew.");
        try {
            MultipartFile pdfFile = request.getPdfFile();
            String token = request.getToken();
            String websocketUrl = ymlMyConfig.getWebsocketUrl();
            String originalName = request.getOriginalName();
            if (Objects.equals(request.getIsOriginalFile(), 0)) {
                sendWebsocketMessageToUser(token, "5", "5", websocketUrl);
            }
            {
                MultipartFile originalFile = request.getOriginalFile();
                if (originalFile == null || originalFile.getSize() == 0) {
                    return ResponseEntity.ok(Response.error(NULL_FILE));
                }

                String originalFilename = originalFile.getOriginalFilename();
                LOGGER.info("课件上传,原文件（也可能是客户端转换后pdf文件）:{}", originalFilename);

                String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                //文件后缀是否符合
                //!matchFileType(originalFile.getInputStream(), suffix) ||
                if (!suffixList.contains(suffix.toLowerCase())) {
                    return ResponseEntity.ok(Response.error(INVALID_FILE));
                }
                /***********重复提交相同文件***********/
                String streamMD5 = SecurityUtil.hashMD5Hex(originalFile.getInputStream());
                LOGGER.info("uploadFileNew streamMD5" + streamMD5);
                int count = checkFileRepeat(originalFile.getOriginalFilename(), streamMD5);
                if (count > 0) {
                    //返回success,返回error客户端会不停请求
                    return ResponseEntity.ok(Response.error(REPEAT_FILE));
                }
                /**********************/

                String pdfFileOriginalFilename = null;
                if (pdfFile != null && pdfFile.getSize() > 0) {
                    pdfFileOriginalFilename = pdfFile.getOriginalFilename();
                    String pdfSuffix = pdfFileOriginalFilename.substring(pdfFileOriginalFilename.lastIndexOf(".") + 1);
                    if (!matchFileType(pdfFile.getInputStream(), "pdf") || !"pdf".equalsIgnoreCase(pdfSuffix)) { //pdf文件后缀是否符合
                        return ResponseEntity.ok(Response.error(INVALID_FILE));
                    }
                }
                LOGGER.info("课件上传,pdf文件:{}", pdfFileOriginalFilename);

                LoginUserCache loginUserCache = redisService.getLoginUserCache(token);
                String teacherUuid = loginUserCache.getUserUuid();
                String saveDir = COURSEWARE_ROOT + FILESEPARATOR + UUIDUtil.noSplit(teacherUuid);
                String timeName = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS");
                File saveFile = new File(saveDir, timeName + "." + suffix);
                if (!saveFile.getParentFile().exists()) {
                    saveFile.getParentFile().mkdirs();
                }
                //上传文件
                String saveFilePath = saveFile.getPath();
                LOGGER.info("dir of originalSaveFile is {}", saveFilePath);
                Files.copy(originalFile.getInputStream(), Paths.get(saveDir, saveFile.getName()));
                File pdfSaveFile = new File(saveDir, timeName + ".pdf");
                if (pdfFile != null && pdfFile.getSize() > 0) {
                    String pdfSaveFilePath = pdfSaveFile.getPath();
                    LOGGER.info("dir of pdfSaveFilePath is {}", pdfSaveFilePath);
                    Files.copy(pdfFile.getInputStream(), Paths.get(saveDir, pdfSaveFile.getName()));
                }
                //讲师中心课件重命名
                String coursewareName = getCoursewareName(originalFilename, teacherUuid);
                LOGGER.info("讲师中心课件重命名:{}", coursewareName);

                //客户端课件重命名
                String coursewareNameNew = getCoursewareName(originalName, teacherUuid);
                LOGGER.info("客户端课件重命名:{}", coursewareNameNew);

                //保存数据
                String coursewareUuid = UUIDUtil.randomUUID2();
                Courseware courseware = new Courseware();
                courseware.setCoursewareUuid(coursewareUuid);
                courseware.setTeacherUuid(teacherUuid);

                //客户端上传时需传入源文件名，用于课件列表展示文件名称（必须是源文件，因为要下载的是源文件）
                if (StringUtils.isNotBlank(originalName) || StringUtils.isNotEmpty(originalName)) {
                    //可能是客户端本地转换后的pdf名称
                    courseware.setOriginalName(request.getOriginalName());
                    //一定是源文件名称
                    courseware.setCoursewareName(coursewareNameNew);
                }
                //讲师中心
                if (Objects.equals(request.getIsPageRequest(), 1)) {
                    courseware.setOriginalName(originalFilename);
                    courseware.setCoursewareName(coursewareName);
                }
                courseware.setConverStatus(1);
                courseware.setStreamMd5(streamMD5);
                int i = coursewareService.save(courseware);
                if (i < 1) {
                    return ResponseEntity.ok(Response.error(FILE_UPLOAD_FAIL));
                }
                LOGGER.info("课件上传完成.");

                String message = "1";   //课件正在转换中的状态
                String type = "1";  //表示发送课件转换状态消息
                sendWebsocketMessageToUser(token, type, message, websocketUrl);

                if ("png".equalsIgnoreCase(suffix) || "jpg".equalsIgnoreCase(suffix) || "jpeg".equalsIgnoreCase(suffix)) {
                    LOGGER.info("图片不需要转，直接保存");
                    new Thread(() -> {
                        String coursewareUrl = ymlMyConfig.getUploadAddress() + saveFilePath.substring(saveFilePath.indexOf(UPLOAD_ROOT));
                        String coursewareType = ClientEnum.CoursewareType.TEACHER.name();
                        courseware.setCoursewareUrl(coursewareUrl);
                        courseware.setCoursewareType(coursewareType);
                        courseware.setCoursewarePreviewUrl(coursewareUrl);
                        courseware.setPageNum(1);
                        courseware.setCoursewarePath(saveFilePath);
                        courseware.setConverStatus(2);
                        int j = coursewareService.update(courseware);
                        if (j > 0) { //更新成功
                            //将图片保存到课件图片表
                            //获取图片的宽高
                            LOGGER.info("更新成功,将图片保存到课件图片表、获取图片的宽高");
                            File imageFile = new File(saveFilePath);
                            BufferedImage bufferedImage = null;
                            try {
                                bufferedImage = ImageIO.read(imageFile);
                                int imageWidth = bufferedImage.getWidth();
                                int imageHeight = bufferedImage.getHeight();
                                CoursewareImage coursewareImage = new CoursewareImage();
                                coursewareImage.setCoursewareImageUuid(UUIDUtil.randomUUID2());
                                coursewareImage.setCoursewareUuid(coursewareUuid);
                                coursewareImage.setImageUrl(coursewareUrl);
                                coursewareImage.setImageName(imageFile.getName());
                                coursewareImage.setWidth(imageWidth);
                                coursewareImage.setHeight(imageHeight);
                                coursewareImage.setImagePath(saveFilePath);
                                String imageMd5 = ClientUtil.md5(imageFile);
                                coursewareImage.setImageMd5(imageMd5);
                                coursewareImageService.save(coursewareImage);
                                sendWebsocketMessageToUser(token, type, "2", websocketUrl);
                            } catch (IOException e) {
                                LOGGER.info("读取图片异常:{}", e);
                                sendWebsocketMessageToUser(token, type, "3", websocketUrl);
                                LOGGER.error(e.getMessage());
                            }
                        }
                    }).start();
                } else if ("pdf".equalsIgnoreCase(suffix)) {
                    LOGGER.info("pdf直接转成图片");
                    new Thread(() -> {
                        int converStatus = converAndSaveCoursewareNew(saveFile, coursewareUuid, true, request.getIsPageRequest(), request.getIsOriginalFile());
                        sendWebsocketMessageToUser(token, type, String.valueOf(converStatus), websocketUrl);
                    }).start();
                } else {
                    LOGGER.info("先转为pdf再转成图片");
                    new Thread(() -> {
                        int converStatus = 3;
                        if (pdfFile != null && pdfFile.getSize() > 0) {  //上传了pdf文件
                            converStatus = converAndSaveCoursewareNew(pdfSaveFile, coursewareUuid, true, request.getIsPageRequest(), request.getIsOriginalFile());
                            //更新原文件路径
                            Courseware courseware1 = new Courseware();
                            courseware1.setCoursewareUuid(coursewareUuid);
                            String coursewareUrl = ymlMyConfig.getUploadAddress() + saveFilePath.substring(saveFilePath.indexOf(UPLOAD_ROOT));
                            courseware1.setCoursewareUrl(coursewareUrl);
                            coursewareService.update(courseware1);
                        } else {
                            converStatus = converAndSaveCoursewareNew(saveFile, coursewareUuid, false, request.getIsPageRequest(), request.getIsOriginalFile());
                        }
                        sendWebsocketMessageToUser(token, type, String.valueOf(converStatus), websocketUrl);
                    }).start();
                }
                Map<String, String> map = new HashMap<>();
                map.put("coursewareUuid", coursewareUuid);
                JSONObject itemJSONObj = JSONObject.parseObject(JSON.toJSONString(map));
                LOGGER.info("执行uploadFileNew结束");
                return ResponseEntity.ok(Response.success(itemJSONObj));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    /**
     * 只传pptx ppt doc docx源文件
     * （转换流程不变）新增converFile
     *
     * @param
     * @return
     */
    @PostMapping("/uploadOriginalFile")
    @LogRecordAnnotation(moduleCode = 10000008, moduleName = "课件转换", methodCode = 100000081, methodName = "converFile", description = "客户端空闲时间上传源文件", userTypes = TEACHER)
    public ResponseEntity<Response> uploadOriginalFile(UploadCoursewareRequest request) {
        LOGGER.info("客户端空闲时间上传源文件，courwareUuid:" + request.getCoursewareUuid());
        Courseware courseware = coursewareService.findByUuid(request.getCoursewareUuid());
        if (courseware != null && StringUtils.isNotBlank(courseware.getCoursewareUuid())) {
            try {
                MultipartFile originalFile = request.getOriginalFile();
               /* if (originalFile == null || originalFile.getSize() == 0) {
                    return ResponseEntity.ok(Response.error(NULL_FILE));
                }*/
                String originalFilename = originalFile.getOriginalFilename();
                LOGGER.info("课件上传,原文件:{}", originalFilename);
                String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                //!matchFileType(originalFile.getInputStream(), suffix) ||
                if (!suffixList.contains(suffix.toLowerCase())) { //文件后缀是否符合
                    return ResponseEntity.ok(Response.error(INVALID_FILE));
                }
                /***********重复提交相同文件***********/
                String streamMD5 = SecurityUtil.hashMD5Hex(originalFile.getInputStream());
                int num = checkFileRepeat(originalFile.getOriginalFilename(), streamMD5);
                if (num > 0) {
                    //返回success,返回error客户端会不停请求
                    return ResponseEntity.ok(Response.error(REPEAT_FILE));
                }
                /**********************/
                String token = request.getToken();
                LoginUserCache loginUserCache = redisService.getLoginUserCache(token);
                String teacherUuid = loginUserCache.getUserUuid();
                String saveDir = COURSEWARE_ROOT + FILESEPARATOR + UUIDUtil.noSplit(teacherUuid);
                String timeName = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS");
                File saveFile = new File(saveDir, timeName + "." + suffix);
                if (!saveFile.getParentFile().exists()) {
                    saveFile.getParentFile().mkdirs();
                }
                //上传文件
                String saveFilePath = saveFile.getPath();
                LOGGER.info("dir of originalSaveFile is {}", saveFilePath);
                Files.copy(originalFile.getInputStream(), Paths.get(saveDir, saveFile.getName()));
                //上传完成根据当前课件id更新课件路径为源文件路径，我的课件点击下载的一定是源文件
                String coursewareUrl = ymlMyConfig.getUploadAddress() + saveFilePath.substring(saveFilePath.indexOf(UPLOAD_ROOT));
                Courseware ware = new Courseware();
                //课件重命名
                String coursewareName = getCoursewareName(originalFilename, teacherUuid);
                LOGGER.info("上传源文件url." + coursewareUrl);
                ware.setCoursewareUuid(request.getCoursewareUuid());
                ware.setCoursewareUrl(coursewareUrl);
                //上传源文件两个name都更新
                //ware.setCoursewareName(coursewareName);
                //ware.setOriginalName(originalFilename);
                int count = coursewareService.update(ware);
                if (count > 0) {
                    LOGGER.info("上传源文件更新url成功.");
                    String websocketUrl = ymlMyConfig.getWebsocketUrl();
                    sendWebsocketMessageToUser(token, "5", "5", websocketUrl);
                } else {
                    LOGGER.info("上传源文件更新url失败.");
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
        return ResponseEntity.ok(Response.success());
    }

    /**
     * MD5加密防止重复提交
     */
    public int checkFileRepeat(String teacherUuid, String streamMD5) {
        //表中是否存在已加密的文件流
        //旧数据同一个原始文件名上传了多次
        int count = 0;
        try {
            count = coursewareService.selectByMD5(teacherUuid, streamMD5);
            LOGGER.info("count" + count);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return count;
    }
}
