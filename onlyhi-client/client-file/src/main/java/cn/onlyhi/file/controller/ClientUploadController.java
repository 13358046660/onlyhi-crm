package cn.onlyhi.file.controller;

import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.po.ClientLogfile;
import cn.onlyhi.client.service.ClientLogfileService;
import cn.onlyhi.client.service.RedisService;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.util.Response;
import cn.onlyhi.common.util.UUIDUtil;
import cn.onlyhi.file.config.YmlMyConfig;
import cn.onlyhi.file.request.UploadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static cn.onlyhi.common.constants.Constants.FILESEPARATOR;
import static cn.onlyhi.common.constants.Constants.FILEUPLOAD_DIR;
import static cn.onlyhi.common.constants.Constants.FILEUPLOAD_ROOT;
import static cn.onlyhi.common.constants.Constants.UPLOAD_ROOT;
import static cn.onlyhi.common.enums.CodeEnum.NULL_FILE;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/4.
 */
@RestController
@RequestMapping("/client/upload")
public class ClientUploadController {

    @Autowired
    private YmlMyConfig ymlMyConfig;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ClientLogfileService clientLogfileService;

    /**
     * 上传图片
     *
     * @param request
     * @return
     */
    @PostMapping("/upload")
    @LogRecordAnnotation(moduleCode = 10000007, moduleName = "文件上传", methodCode = 100000078, methodName = "upload", description = "上传图片")
    public ResponseEntity<Response> upload(UploadRequest request) throws IOException {
        MultipartFile multipartFile = request.getFile();
        if (multipartFile == null || multipartFile.getSize() == 0) {
            return ResponseEntity.ok(Response.error(NULL_FILE));
        }
        String phone = redisService.getLoginUserCache(request.getToken()).getPhone();
        String saveDir = FILEUPLOAD_ROOT + FILESEPARATOR + phone;

        //获取图片的原始名字
        String originalfileName = multipartFile.getOriginalFilename();
        //后缀名
        String suffix = originalfileName.substring(originalfileName.lastIndexOf(".") + 1);

        //生成保存的图片名，上传名+时间戳
        String saveFileName = System.currentTimeMillis() + "." + suffix;
        File uploadFile = new File(saveDir, saveFileName);
        if (!uploadFile.getParentFile().exists()) {
            uploadFile.getParentFile().mkdirs();
        }
        Files.copy(multipartFile.getInputStream(), Paths.get(saveDir, saveFileName));

        //上传成功，返回图片链接
        String uploadFileLink = ymlMyConfig.getUploadAddress() + UPLOAD_ROOT + FILESEPARATOR + FILEUPLOAD_DIR + FILESEPARATOR + phone + FILESEPARATOR + saveFileName;
        Map<String, String> map = new HashMap<>();
        map.put("imagePath", uploadFileLink);
        return ResponseEntity.ok(Response.success(map));
    }

    /**
     * 客户端日志上传
     *
     * @param request
     * @return
     */
    @PostMapping("/uploadLog")
    @LogRecordAnnotation(moduleCode = 10000007, moduleName = "文件上传", methodCode = 100000079, methodName = "uploadLog", description = "客户端日志上传")
    public ResponseEntity<Response> uploadLog(UploadRequest request) throws IOException {
        MultipartFile multipartFile = request.getFile();
        String originalfileName = multipartFile.getOriginalFilename();
        String dateDir = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String logFileDirPath = "/var/log/clientlog/" + dateDir;
        File logFile = new File(logFileDirPath, originalfileName);
        if (!logFile.getParentFile().exists()) {
            logFile.getParentFile().mkdirs();
        }
        Files.copy(multipartFile.getInputStream(), Paths.get(logFileDirPath, originalfileName));

        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userUuid = loginUserCache.getUserUuid();
        String userName = loginUserCache.getUserName();
        String userType = loginUserCache.getUserType();
        ClientLogfile clientLogfile = new ClientLogfile();
        clientLogfile.setClientLogfileUuid(UUIDUtil.randomUUID2());
        clientLogfile.setUserUuid(userUuid);
        clientLogfile.setUsername(userName);
        clientLogfile.setUsertype(userType);
        clientLogfile.setLogfilePath(logFile.getPath());
        clientLogfile.setCreateUid(userUuid);
        clientLogfileService.save(clientLogfile);
        return ResponseEntity.ok(Response.success());
    }
}
