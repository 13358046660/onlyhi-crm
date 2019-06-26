package cn.onlyhi.client.controller;

import cn.onlyhi.client.config.OssConfig;
import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.po.Courseware;
import cn.onlyhi.client.po.CoursewareDir;
import cn.onlyhi.client.request.CoursewareDirUuidRequest;
import cn.onlyhi.client.request.CoursewareNameRequest;
import cn.onlyhi.client.request.CoursewareUuidRequest;
import cn.onlyhi.client.request.MoveCoursewareRequest;
import cn.onlyhi.client.request.SaveCoursewareDirRequest;
import cn.onlyhi.client.request.UpdateCoursewareDirRequest;
import cn.onlyhi.client.service.CoursewareDirService;
import cn.onlyhi.client.service.CoursewareService;
import cn.onlyhi.client.service.RedisService;
import cn.onlyhi.client.vo.CoursewareDirVo;
import cn.onlyhi.client.vo.CoursewareListVo;
import cn.onlyhi.client.vo.CoursewareVo;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.request.BaseRequest;
import cn.onlyhi.common.util.*;
import com.aliyun.oss.OSSClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * 课件管理
 *
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/3/15.
 */
@RestController
@RequestMapping("/courseware")
public class CoursewareController {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CoursewareController.class);
    @Autowired
    private RedisService redisService;
    @Autowired
    private CoursewareDirService coursewareDirService;
    @Autowired
    private CoursewareService coursewareService;
    @Autowired
    private OssConfig ossConfig;

    /**
     * 课件列表
     *
     * @param request
     * @return
     */
    @GetMapping("/list")
    @LogRecordAnnotation(moduleCode = 100009, moduleName = "课件管理", methodCode = 1000091, methodName = "list", description = "课件列表")
    public ResponseEntity<Response> list(BaseRequest request) {
        LOGGER.info("进入课件列表");
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userUuid = loginUserCache.getUserUuid();
        List<CoursewareDir> coursewareDirList = coursewareDirService.findByTeacherUuid(userUuid);
        CoursewareListVo listVo = new CoursewareListVo();
        List<CoursewareDirVo> coursewareDirVoList = new ArrayList<>();
        List<CoursewareVo> coursewareVoList = new ArrayList<>();//输出参数
        CoursewareVo coursewareVo=new CoursewareVo();
        OSSClient ossClient = OssUtils.generateOssClient();
        if (coursewareDirList.size() == 0) { //没有目录
            List<Courseware> coursewareList = coursewareService.findByTeacherUuid(userUuid, "create_time", "desc");
            /** 如果存入的coursewareUrl不含http*/
            for(Courseware courseware:coursewareList){
                if(courseware!=null && StringUtils.isNotBlank(courseware.getCoursewareUrl()) ){
                    if(!Objects.equals(courseware.getCoursewarePreviewUrl(),null) && !courseware.getCoursewarePreviewUrl().contains("http://")){
                        courseware.setCoursewarePreviewUrl(OssTeacherUtils.generateFileUrl(ossClient, courseware.getCoursewarePreviewUrl()));
                    }
                    if(!Objects.equals(courseware.getCoursewareUrl(),null) && !courseware.getCoursewareUrl().contains("http://")){
                        courseware.setCoursewareUrl(OssTeacherUtils.generateFileUrl(ossClient, courseware.getCoursewareUrl()));
                    }
                }
            }
            coursewareVoList = TransferUtil.transfer(coursewareList, CoursewareVo.class);
        } else {
            for (CoursewareDir coursewareDir : coursewareDirList) {
                String coursewareDirUuid = coursewareDir.getCoursewareDirUuid();
                List<Courseware> coursewareList = coursewareService.findByCoursewareDirUuid(coursewareDirUuid, "create_time", "desc");
                coursewareVoList = TransferUtil.transfer(coursewareList, CoursewareVo.class);
                CoursewareDirVo coursewareDirVo = TransferUtil.transfer(coursewareDir, CoursewareDirVo.class);
                coursewareDirVo.setCoursewareList(coursewareVoList);
                coursewareDirVoList.add(coursewareDirVo);
            }
            //根目录文件（非完整url）
            List<Courseware> coursewareList = coursewareService.findRootByTeacherUuid(userUuid, "create_time", "desc");
            /** 如果存入的coursewareUrl不含http*/
            for(Courseware courseware:coursewareList){
                if(courseware!=null && StringUtils.isNotBlank(courseware.getCoursewareUrl()) ){
                    if(!Objects.equals(courseware.getCoursewarePreviewUrl(),null) && !courseware.getCoursewarePreviewUrl().contains("http://")){
                        courseware.setCoursewarePreviewUrl(OssTeacherUtils.generateFileUrl(ossClient, courseware.getCoursewarePreviewUrl()));
                    }
                    if(!Objects.equals(courseware.getCoursewareUrl(),null) && !courseware.getCoursewareUrl().contains("http://")){
                        courseware.setCoursewareUrl(OssTeacherUtils.generateFileUrl(ossClient, courseware.getCoursewareUrl()));
                    }
                }
            }
            coursewareVoList = TransferUtil.transfer(coursewareList, CoursewareVo.class);
        }
        ossClient.shutdown();
        listVo.setCoursewareDirList(coursewareDirVoList); //目录
        listVo.setCoursewareList(coursewareVoList);
        return ResponseEntity.ok(Response.success(listVo));
    }

    /**
     * 保存课件目录
     *
     * @param request
     * @return
     */
    @PostMapping("/saveDir")
    @LogRecordAnnotation(moduleCode = 100009, moduleName = "课件管理", methodCode = 1000092, methodName = "saveDir", description = "保存课件目录")
    public ResponseEntity<Response> saveDir(SaveCoursewareDirRequest request) {
        String teacherUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        String coursewareDirName = request.getCoursewareDirName().trim();
        String newCoursewareDirName = getCoursewareDirName(coursewareDirName, teacherUuid);
        CoursewareDir coursewareDir = new CoursewareDir();
        coursewareDir.setCoursewareDirUuid(UUIDUtil.randomUUID2());
        coursewareDir.setTeacherUuid(teacherUuid);
        coursewareDir.setCoursewareDirName(newCoursewareDirName);
        coursewareDirService.save(coursewareDir);
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 课件目录名判断并重命名
     *
     * @param coursewareDirName
     * @param teacherUuid
     * @return
     */
    private String getCoursewareDirName(String coursewareDirName, String teacherUuid) {
        CoursewareDir coursewareDir = coursewareDirService.findByCoursewareDirName(teacherUuid, coursewareDirName);
        if (coursewareDir == null) {
            return coursewareDirName;
        }
        //查询时将coursewareDirName中特殊符号进行转义
        String searchDirName = coursewareDirName.replace("'", "\\'").replace("(", "\\\\(").replace("[", "\\\\[");
        List<String> coursewareDirNameList = coursewareDirService.findMatchCoursewareDirNameByTeacherUuid(searchDirName, teacherUuid);
        if (coursewareDirNameList == null || coursewareDirNameList.size() == 0) {
            return coursewareDirName + "(1)";
        }
        int size = coursewareDirNameList.size();
        for (int i = 0; i <= size; i++) {
            String newCoursewareDirName = coursewareDirName + "(" + (i + 1) + ")";
            if (!coursewareDirNameList.contains(newCoursewareDirName)) {
                return newCoursewareDirName;
            }
        }
        return null;

    }

    /**
     * 移动课件
     *
     * @param request
     * @return
     */
    @PostMapping("/moveCourseware")
    @LogRecordAnnotation(moduleCode = 100009, moduleName = "课件管理", methodCode = 1000093, methodName = "moveCourseware", description = "移动课件")
    public ResponseEntity<Response> moveCourseware(MoveCoursewareRequest request) {
        String coursewareUuid = request.getCoursewareUuid();
        String coursewareDirUuid = request.getCoursewareDirUuid();
        if (StringUtils.isBlank(coursewareDirUuid)) {
            coursewareDirUuid = null;
        }
        coursewareService.updateCoursewareDir(coursewareUuid, coursewareDirUuid);
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 删除目录及其课件
     *
     * @param request
     * @return
     */
    @PostMapping("/deleteDirAndCourseware")
    @LogRecordAnnotation(moduleCode = 100009, moduleName = "课件管理", methodCode = 1000094, methodName = "deleteDirAndCourseware", description = "删除目录及其课件")
    public ResponseEntity<Response> deleteDirAndCourseware(CoursewareDirUuidRequest request) {
        String coursewareDirUuid = request.getCoursewareDirUuid();
        coursewareService.deleteDirAndCourseware(coursewareDirUuid);
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 目录重命名
     *
     * @param request
     * @return
     */
    @PostMapping("/updateDir")
    @LogRecordAnnotation(moduleCode = 100009, moduleName = "课件管理", methodCode = 1000095, methodName = "updateDir", description = "目录重命名")
    public ResponseEntity<Response> updateDir(UpdateCoursewareDirRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String teacherUuid = loginUserCache.getUserUuid();
        String coursewareDirUuid = request.getCoursewareDirUuid();
        String coursewareDirName = request.getCoursewareDirName();
        if (!coursewareDirName.equals(coursewareDirService.findByUuid(coursewareDirUuid).getCoursewareDirName())) {
            coursewareDirName = getCoursewareDirName(coursewareDirName, teacherUuid);
            CoursewareDir coursewareDir = new CoursewareDir();
            coursewareDir.setCoursewareDirUuid(coursewareDirUuid);
            coursewareDir.setCoursewareDirName(coursewareDirName);
            coursewareDirService.update(coursewareDir);
        }
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 根据课件名称模糊查找课件
     *
     * @param request
     * @return
     */
    @GetMapping("/findByName")
    @LogRecordAnnotation(moduleCode = 100009, moduleName = "课件管理", methodCode = 1000096, methodName = "findByName", description = "根据课件名称模糊查找课件")
    public ResponseEntity<Response> findByName(CoursewareNameRequest request) {
        String teacherUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        String coursewareName = request.getCoursewareName();
        List<Courseware> coursewareList = coursewareService.findLikeByCoursewareName(teacherUuid, coursewareName);
        List<CoursewareVo> voList = TransferUtil.transfer(coursewareList, CoursewareVo.class);
        return ResponseEntity.ok(Response.success(voList));
    }

    /**
     * 根据课件uuid获取课件信息
     *
     * @param request
     * @return
     */
    @GetMapping("/info")
    @LogRecordAnnotation(moduleCode = 100009, moduleName = "课件管理", methodCode = 1000097, methodName = "info", description = "根据课件uuid获取课件信息")
    public ResponseEntity<Response> info(CoursewareUuidRequest request) {
        String coursewareUuid = request.getCoursewareUuid();
        Courseware courseware = coursewareService.findByUuid(coursewareUuid);
        if (courseware == null) {
            return ResponseEntity.ok(Response.errorCustom("课件不存在！"));
        }
        CoursewareVo vo = TransferUtil.transfer(courseware, CoursewareVo.class);
        return ResponseEntity.ok(Response.success(vo));
    }


}
