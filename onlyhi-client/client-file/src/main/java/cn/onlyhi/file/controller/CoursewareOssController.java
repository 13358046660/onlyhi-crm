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
import static cn.onlyhi.common.enums.CodeEnum.*;
import static cn.onlyhi.common.util.ClientUtil.sendWebsocketMessageToUser;
import static cn.onlyhi.common.util.FileUtils.matchFileType;

/**
 * @Author wangqianzhi
 * 重构CoursewareController 测试验证后替换CoursewareController
 * 1、客户端上传课件与讲师中心上传课件保持一致都要上传至oss
 * 2、课件转码插件libertyoffic很耗CPU需替换成阿里转码工具
 * <p>
 * Created by wangqianzhi on 2019/3/12
 */
@RestController
//@RequestMapping("/courseware")
public class CoursewareOssController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoursewareOssController.class);
    private static final List<String> suffixList = Arrays.asList("ppt", "pptx", "doc", "docx", "pdf");

    @Autowired
    private RedisService redisService;


    @PostMapping("/uploadFile")
    @LogRecordAnnotation(moduleCode = 10000007, moduleName = "文件上传", methodCode = 100000071, methodName = "uploadFile", description = "教师课件上传+转pdf和图片", userTypes = TEACHER)
    public ResponseEntity<Response> uploadFile(UploadFileRequest request) throws Exception {

        return null;
    }
}
