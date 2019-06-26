package cn.onlyhi.client.controller;

import cn.onlyhi.client.config.OssConfig;
import cn.onlyhi.client.config.YmlMyConfig;
import cn.onlyhi.client.request.UploadCoursewareRequest;
import cn.onlyhi.client.util.FileConverUtil;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.util.FileUtils;
import cn.onlyhi.common.util.OSSUtil;
import cn.onlyhi.common.util.OSUtil;
import cn.onlyhi.common.util.Response;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static cn.onlyhi.common.constants.Constants.COURSEWARE;
import static cn.onlyhi.common.constants.Constants.FILESEPARATOR;
import static cn.onlyhi.common.enums.CodeEnum.INVALID_FILE;
import static cn.onlyhi.common.enums.CodeEnum.NULL_FILE;
import static cn.onlyhi.common.util.ClientUtil.sendWebsocketMessageToUser;
import static cn.onlyhi.common.util.FileUtils.matchFileType;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/12/22.
 */
@RestController
@RequestMapping("/conver")
public class ConverController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConverController.class);
    private static final List<String> suffixList = Arrays.asList("ppt", "pptx", "doc", "docx", "pdf", "png", "jpeg", "jpg");
    private static String FILESAVETEMPPATH = "/www/clientfile/fileSaveTempPath";

    static {
        if (OSUtil.isWindows()) {
            FILESAVETEMPPATH = "D:/www/clientfile/fileSaveTempPath";
        }
    }

    @Autowired
    private YmlMyConfig ymlMyConfig;
    @Autowired
    private OssConfig ossConfig;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 课件转换
     *
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/uploadFile")
    @LogRecordAnnotation(moduleCode = 100005, moduleName = "课件转换", methodCode = 1000051, methodName = "uploadFile", description = "课件转换", checkToken = false)
    public ResponseEntity<Response> uploadFile(UploadCoursewareRequest request) throws Exception {
        MultipartFile originalFile = request.getOriginalFile();
        if (originalFile == null || originalFile.getSize() == 0) {
            return ResponseEntity.ok(Response.error(NULL_FILE));
        }
        String originalFilename = originalFile.getOriginalFilename();
        LOGGER.info("课件上传,原文件:{}", originalFilename);
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (!matchFileType(originalFile.getInputStream(), suffix) || !suffixList.contains(suffix.toLowerCase())) { //文件后缀是否符合
            return ResponseEntity.ok(Response.error(INVALID_FILE));
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

        String timeName = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS");
        String saveDir = FILESAVETEMPPATH + FILESEPARATOR + timeName;
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

        LOGGER.info("文件转换中!");
        String message = "1";
        String type = "1";  //表示发送课件转换状态消息
        String websocketUrl = ymlMyConfig.getWebsocketUrl();
        String websocketToken = request.getWebsocketToken();
        if (StringUtils.isNotBlank(websocketToken)) {
            sendWebsocketMessageToUser(websocketToken, type, message, websocketUrl);
        }

        String teacherUuid = request.getTeacherUuid();
        String backUrl = request.getBackUrl();
        File file = saveFile;
        int fileType;
        if ("png".equalsIgnoreCase(suffix) || "jpg".equalsIgnoreCase(suffix) || "jpeg".equalsIgnoreCase(suffix)) {
            fileType = 1;
        } else if ("pdf".equalsIgnoreCase(suffix)) {
            fileType = 2;
        } else {    //office
            fileType = 3;
            if (pdfSaveFile.exists()) {  //上传了pdf文件
                file = pdfSaveFile;
                fileType = 2;
            }
        }
        FileConverUtil.ConverResult converResult = FileConverUtil.conver(file, fileType);
        if (StringUtils.isNotBlank(websocketToken)) {
            sendWebsocketMessageToUser(websocketToken, type, String.valueOf(converResult.getConverStatus()), websocketUrl);
        }
        converResult.setOriginalUrl(saveFile.getPath());
        //将文件上传到OSS上并删除本地文件
        uploadAndDeleteFile(converResult, teacherUuid);
        //结果推送
        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(mediaType);
        String requestJson = JSON.toJSONString(converResult);
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        Response result = restTemplate.postForObject(backUrl, entity, Response.class);
        LOGGER.info("调用backUrl={}的结果:{}", backUrl, result);
        if (StringUtils.isNotBlank(websocketToken)) {
            if (result.isHasError()) {
                sendWebsocketMessageToUser(websocketToken, type, "3", websocketUrl);
            } else {
                sendWebsocketMessageToUser(websocketToken, type, "2", websocketUrl);
            }
        }
        return ResponseEntity.ok(Response.success());
    }

    private void uploadAndDeleteFile(FileConverUtil.ConverResult converResult, String teacherUuid) {
        String baseKey = COURSEWARE;
        String file_separator = "/";
        if (StringUtils.isBlank(teacherUuid)) {
            baseKey = COURSEWARE + file_separator + "SYSTEM";
        } else {
            baseKey = COURSEWARE + file_separator + teacherUuid;
        }
        OSSUtil ossUtil = new OSSUtil(ossConfig.getDomain(), ossConfig.getBucketName());
        String originalPath = converResult.getOriginalUrl();
        File originalFile = new File(originalPath);
        String originalKey = baseKey + file_separator + originalFile.getName();
        ossUtil.upload(originalFile, originalKey);
        converResult.setOriginalUrl(originalKey);

        String pdfPath = converResult.getPdfUrl();
        File pdfFile = new File(pdfPath);
        String pdfKey = baseKey + file_separator + pdfFile.getName();
        ossUtil.upload(pdfFile, pdfKey);
        converResult.setPdfUrl(pdfKey);

        List<FileConverUtil.ConverResult.Image> imageList = converResult.getImageList();
        String baseImageKey = pdfKey.substring(0, pdfKey.indexOf("."));
        File imageFile = null;
        for (FileConverUtil.ConverResult.Image image : imageList) {
            String path = image.getUrl();
            imageFile = new File(path);
            String imageKey = baseImageKey + file_separator + imageFile.getName();
            ossUtil.upload(imageFile, imageKey);
            image.setUrl(imageKey);
        }

        //删除本地文件(失败，图片文件被java进程占用，待查)
        boolean deleteFlag = FileUtils.deleteDirectory(originalFile.getParentFile());
        LOGGER.info("删除本地文件:{}", deleteFlag);
    }
}
