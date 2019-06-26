package cn.onlyhi.client.controller;


import cn.onlyhi.client.dto.CoursewareDto;
import cn.onlyhi.client.po.Courseware;
import cn.onlyhi.client.po.CoursewareImage;
import cn.onlyhi.client.request.CoursewareUuidRequest;
import cn.onlyhi.client.service.CoursewareImageService;
import cn.onlyhi.client.service.CoursewareService;
import cn.onlyhi.client.service.RedisService;
import cn.onlyhi.client.vo.CoursewareImageVo;
import cn.onlyhi.client.vo.CoursewareVo;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.request.BaseRequest;
import cn.onlyhi.common.request.PageRequest;
import cn.onlyhi.common.util.*;
import com.aliyun.oss.OSSClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.onlyhi.common.enums.ClientEnum.CoursewareType.TEACHER;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/3/23.
 */
@RestController
@RequestMapping("/client/courseware")
public class ClientCoursewareController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCoursewareController.class);

    @Autowired
    private CoursewareService coursewareService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private CoursewareImageService coursewareImageService;

    /**
     * 获取系统课件
     *
     * @param request
     * @return
     */
    @GetMapping("/getSysCoursewareList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001133, methodName = "getSysCoursewareList", description = "获取系统课件")
    public ResponseEntity<Response> getSysCoursewareList(PageRequest request) {
        Integer pageNo = request.getPageNo();
        Integer pageSize = request.getPageSize();
        Map paramMap = new HashMap<>();
        long count = coursewareService.countSysCourseware(paramMap);
        List<CoursewareDto> coursewareDtoList = coursewareService.findSysCourseware(paramMap, pageNo, pageSize);
        List<CoursewareVo> coursewareVoList = TransferUtil.transfer(coursewareDtoList, CoursewareVo.class);
        Page<CoursewareVo> page = new Page<>(count, coursewareVoList);
        return ResponseEntity.ok(Response.success(page));
    }

    @PostMapping("/deleteCourseware")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001137, methodName = "deleteCourseware", description = "删除课件")
    public ResponseEntity<Response> deleteCourseware(CoursewareUuidRequest request) {
        return deleteCoursewareCom(request);
    }

    @PostMapping("/deleteCoursewareByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 1000017, methodName = "deleteCoursewareByJson", description = "删除课件")
    public ResponseEntity<Response> deleteCoursewareByJson(@RequestBody CoursewareUuidRequest request) {
        return deleteCoursewareCom(request);
    }

    /**
     * 删除课件
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> deleteCoursewareCom(CoursewareUuidRequest request) {
        coursewareService.deleteCourseware(request.getCoursewareUuid());
        return ResponseEntity.ok(Response.success());
    }

    @PostMapping("/iosGetCoursewareImageList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 1000018, methodName = "iosGetCoursewareImageList", description = "获取课件图片列表")
    public ResponseEntity<Response> iosGetCoursewareImageList(@RequestBody CoursewareUuidRequest request) throws IOException {
        return getCoursewareImageCom(request);
    }

    @PostMapping("/getCoursewareImageList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 1000019, methodName = "getCoursewareImageList", description = "获取课件图片列表")
    public ResponseEntity<Response> getCoursewareImageList(CoursewareUuidRequest request) throws IOException {
        return getCoursewareImageCom(request);
    }

    private ResponseEntity<Response> getCoursewareImageCom(CoursewareUuidRequest request) throws IOException {
        String coursewareUuid = request.getCoursewareUuid();
        Courseware courseware = coursewareService.findByUuid(coursewareUuid);
        if (courseware != null) {
            if (Objects.equals(courseware.getStatus(), 0)) {
                return ResponseEntity.ok(Response.errorCustom("课件已被删除！"));
            }
            if (!TEACHER.name().equals(courseware.getCoursewareType())) {
                return ResponseEntity.ok(Response.errorCustom("不是教师课件！"));
            }
        }
        List<CoursewareImage> coursewareImageList = coursewareService.findImageByCoursewareId(coursewareUuid);
     /* 注释原code，下面新修改此段
     for (CoursewareImage coursewareImage : coursewareImageList) {
            String imageUrl = coursewareImage.getImageUrl();
            String imageMd5 = coursewareImage.getImageMd5();
            if (StringUtils.isBlank(imageMd5) && imageUrl.contains("http")) {
                try {
                    imageMd5 = SecurityUtil.hashMD5Hex(new URL(imageUrl).openStream());
                    coursewareImage.setImageMd5(imageMd5);
                    coursewareImageService.update(coursewareImage);
                } catch (IOException e) {
                    LOGGER.info("课件图片加密失败:{}", e);
                    LOGGER.error(e.getMessage());
                }
            }
        }*/
        OSSClient ossClient = OssUtils.generateOssClient();
        for (CoursewareImage coursewareImage : coursewareImageList) {
            String imageUrl = coursewareImage.getImageUrl();
            String imageMd5 = coursewareImage.getImageMd5();
            InputStream inputStream = null;
            if (StringUtils.isBlank(imageMd5) && imageUrl.contains("http")) {
                //从Java7开始，Java新添了一个特性，在try后面加上一个圆括号，将需要关闭的资源放到里面定义，
                //程序执行完毕会自动帮我们关闭圆括号里面的资源
                try {
                    inputStream = new URL(imageUrl).openStream();
                    imageMd5 = SecurityUtil.hashMD5Hex(inputStream);
                    coursewareImage.setImageMd5(imageMd5);
                    coursewareImageService.update(coursewareImage);
                } catch (IOException e) {
                    LOGGER.info("课件图片加密失败:{}", e);
                    LOGGER.error(e.getMessage());
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            } else {
                if (!Objects.equals(coursewareImage.getImageUrl(), null) && !coursewareImage.getImageUrl().contains("http://")) {
                    coursewareImage.setImageUrl(OssTeacherUtils.generateFileUrl(ossClient, coursewareImage.getImageUrl()));
                }
            }
        }
        ossClient.shutdown();

        List<CoursewareImageVo> coursewareImageVoList = TransferUtil.transfer(coursewareImageList, CoursewareImageVo.class);

        return ResponseEntity.ok(Response.success(coursewareImageVoList));
    }

    @GetMapping("/getCoursewareList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001135, methodName = "getCoursewareList", description = "获取课件列表")
    public ResponseEntity<Response> getCoursewareList(BaseRequest request) {
        return getCoursewareListCom(request);
    }

    @PostMapping("/getCoursewareListByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000110, methodName = "getCoursewareListByJson", description = "获取课件列表")
    public ResponseEntity<Response> getCoursewareListByJson(@RequestBody BaseRequest request) {
        return getCoursewareListCom(request);
    }

    /**
     * 获取课件列表
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> getCoursewareListCom(BaseRequest request) {
        List<CoursewareVo> coursewareVoList = null;
        try {
            String teacherUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
            List<Courseware> coursewareList = coursewareService.findByTeacherUuid(teacherUuid);
            OSSClient ossClient = OssUtils.generateOssClient();
            for (Courseware courseware : coursewareList) {
                if (!Objects.equals(courseware.getCoursewarePreviewUrl(), null) && !courseware.getCoursewarePreviewUrl().contains("http://")) {
                    courseware.setCoursewarePreviewUrl(OssTeacherUtils.generateFileUrl(ossClient, courseware.getCoursewarePreviewUrl()));
                }
                if (!Objects.equals(courseware.getCoursewareUrl(), null) && !courseware.getCoursewareUrl().contains("http://")) {
                    courseware.setCoursewareUrl(OssTeacherUtils.generateFileUrl(ossClient, courseware.getCoursewareUrl()));
                }
            }
            ossClient.shutdown();
            coursewareVoList = TransferUtil.transfer(coursewareList, CoursewareVo.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(Response.success(coursewareVoList));
    }

    /**
     * 课件图片加密
     *
     * @param request
     * @return
     */
    @GetMapping("/coursewareImageMd5")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001182, methodName = "coursewareImageMd5", description = "课件图片加密")
    public ResponseEntity<Response> coursewareImageMd5(BaseRequest request) {
        //查询所有未加密图片
        List<CoursewareImage> coursewareImageList = coursewareImageService.findNoMd5();
        for (CoursewareImage coursewareImage : coursewareImageList) {
            String imageUrl = coursewareImage.getImageUrl();
            if (!imageUrl.contains("http")) {   //系统课件图片
                continue;
            }
            try {
                String imageMd5 = ClientUtil.md5(new URL(imageUrl));
                coursewareImage.setImageMd5(imageMd5);
                coursewareImageService.update(coursewareImage);
            } catch (MalformedURLException e) {
                LOGGER.info(imageUrl + "加密失败:{}", e);
                LOGGER.error(e.getMessage());
            }
        }
        return ResponseEntity.ok(Response.success());
    }
}
