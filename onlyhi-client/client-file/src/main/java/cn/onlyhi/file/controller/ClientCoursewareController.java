package cn.onlyhi.file.controller;


import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.po.Courseware;
import cn.onlyhi.client.service.RedisService;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.util.Response;
import cn.onlyhi.common.util.UUIDUtil;
import cn.onlyhi.file.request.UploadFileRequest;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static cn.onlyhi.common.constants.Constants.COURSEWARE_ROOT;
import static cn.onlyhi.common.constants.Constants.FILESEPARATOR;
import static cn.onlyhi.common.enums.ClientEnum.UserType.TEACHER;
import static cn.onlyhi.common.enums.CodeEnum.FILE_UPLOAD_FAIL;
import static cn.onlyhi.common.enums.CodeEnum.INVALID_FILE;
import static cn.onlyhi.common.enums.CodeEnum.NULL_FILE;
import static cn.onlyhi.common.util.ClientUtil.sendWebsocketMessageToUser;
import static cn.onlyhi.common.util.FileUtils.matchFileType;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/3/23.
 */
@RestController
@RequestMapping("/client/courseware")
public class ClientCoursewareController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCoursewareController.class);
    private static final List<String> suffixList = Arrays.asList("ppt", "pptx", "doc", "docx", "pdf");

    @Autowired
    private RedisService redisService;


    @PostMapping("/uploadFile")
    @LogRecordAnnotation(moduleCode = 10000007, moduleName = "文件上传", methodCode = 100000071, methodName = "uploadFile", description = "教师课件上传+转pdf和图片", userTypes = TEACHER)
    public ResponseEntity<Response> uploadFile(UploadFileRequest request) throws Exception {
        MultipartFile multipartFile = request.getFile();
        if (multipartFile == null || multipartFile.getSize() == 0) {
            return ResponseEntity.ok(Response.error(NULL_FILE));
        }
        String originalFilename = multipartFile.getOriginalFilename();
        LOGGER.info("课件开始上传,上传文件名:{}", originalFilename);
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (!matchFileType(multipartFile.getInputStream(), suffix) || !suffixList.contains(suffix.toLowerCase())) { //文件后缀是否符合
            return ResponseEntity.ok(Response.error(INVALID_FILE));
        }

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
        LOGGER.info("dir of saveFile is {}", saveFilePath);
        Files.copy(multipartFile.getInputStream(), Paths.get(saveDir, saveFile.getName()));

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
        courseware.setConverStatus(1);
        int i = coursewareService.save(courseware);
        if (i < 1) {
            return ResponseEntity.ok(Response.error(FILE_UPLOAD_FAIL));
        }

        LOGGER.info("文件转换中!");
        String message = "1";
        String type = "1";
        String websocketUrl = ymlMyConfig.getWebsocketUrl();
        sendWebsocketMessageToUser(token, type, message, websocketUrl);
        if ("pdf".equalsIgnoreCase(suffix)) {    //直接转成图片
            int converStatus = converAndSaveCourseware(saveFile, coursewareUuid, true);
            sendWebsocketMessageToUser(token, type, String.valueOf(converStatus), websocketUrl);
        } else {  //先转为pdf再转成图片
            int converStatus = converAndSaveCourseware(saveFile, coursewareUuid, false);
            sendWebsocketMessageToUser(token, type, String.valueOf(converStatus), websocketUrl);
        }
        return ResponseEntity.ok(Response.success());
    }
}
