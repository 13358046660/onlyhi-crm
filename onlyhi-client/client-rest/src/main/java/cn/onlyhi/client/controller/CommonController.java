package cn.onlyhi.client.controller;

import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.dto.UserDto;
import cn.onlyhi.client.po.AgoraCallLog;
import cn.onlyhi.client.po.AppInfo;
import cn.onlyhi.client.po.CpCourse;
import cn.onlyhi.client.po.Leads;
import cn.onlyhi.client.po.PatriarchLeads;
import cn.onlyhi.client.po.SysArea;
import cn.onlyhi.client.po.SysEnum;
import cn.onlyhi.client.po.TcTeacher;
import cn.onlyhi.client.po.User;
import cn.onlyhi.client.request.AppInfoRequest2;
import cn.onlyhi.client.request.CityCodeRequest;
import cn.onlyhi.client.request.CourseUuidRequest;
import cn.onlyhi.client.request.ProvinceCodeRequest;
import cn.onlyhi.client.request.SaveAgoraCallLogRequest;
import cn.onlyhi.client.service.AgoraCallLogService;
import cn.onlyhi.client.service.AppInfoService;
import cn.onlyhi.client.service.CpCourseService;
import cn.onlyhi.client.service.LeadsService;
import cn.onlyhi.client.service.PatriarchLeadsService;
import cn.onlyhi.client.service.RedisService;
import cn.onlyhi.client.service.SysAreaService;
import cn.onlyhi.client.service.SysEnumService;
import cn.onlyhi.client.service.TcTeacherService;
import cn.onlyhi.client.service.UserService;
import cn.onlyhi.client.vo.*;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.enums.CodeEnum;
import cn.onlyhi.common.request.BaseRequest;
import cn.onlyhi.common.util.*;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static cn.onlyhi.common.constants.Constants.SYS_ENUM_TYPE_SUBJECT;
import static cn.onlyhi.common.enums.ClientEnum.UserType.*;
import static cn.onlyhi.common.enums.CodeEnum.COURSE_NO_STUDENT;
import static cn.onlyhi.common.enums.CodeEnum.NO_COURSE;
import static cn.onlyhi.common.util.ClientUtil.getAgoraUid;
import static cn.onlyhi.common.util.ClientUtil.getUrlParams;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2018/1/29.
 */
@RestController
@RequestMapping("/common")
public class CommonController {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CommonController.class);
    @Autowired
    private SysAreaService sysAreaService;
    @Autowired
    private SysEnumService sysEnumService;
    @Autowired
    private AgoraCallLogService agoraCallLogService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private CpCourseService cpCourseService;
    @Autowired
    private UserService userService;
    @Autowired
    private LeadsService leadsService;
    @Autowired
    private TcTeacherService tcTeacherService;
    @Autowired
    private PatriarchLeadsService patriarchLeadsService;
    @Autowired
    private AppInfoService appInfoService;
    @Value("${phpStaging.url}")
    private String url;
    /**
     * 获取所有省份信息列表
     *
     * @param request
     * @return
     */
    @GetMapping("/getAllProvince")
    @LogRecordAnnotation(moduleCode = 100007, moduleName = "公共接口", methodCode = 1000072, methodName = "getAllProvince", description = "获取所有省份信息")
    public ResponseEntity<Response> getAllProvince(BaseRequest request) {
        List<SysArea> sysAreaList = sysAreaService.findByAreaLevel(1);
        List<SysAreaVo> voList = TransferUtil.transfer(sysAreaList, SysAreaVo.class);
        return ResponseEntity.ok(Response.success(voList));
    }

    /**
     * 根据省份编码获取其下的市信息列表
     *
     * @param request
     * @return
     */
    @GetMapping("/getCitysByProvinceCode")
    @LogRecordAnnotation(moduleCode = 100007, moduleName = "公共接口", methodCode = 1000073, methodName = "getCitysByProvinceCode", description = "根据省份编码获取其下的市信息列表")
    public ResponseEntity<Response> getCitysByProvinceCode(ProvinceCodeRequest request) {
        String provinceCode = request.getProvinceCode();
        List<SysArea> sysAreaList = sysAreaService.findByAreaLevelAndParentCode(2, provinceCode);
        List<SysAreaVo> voList = TransferUtil.transfer(sysAreaList, SysAreaVo.class);
        return ResponseEntity.ok(Response.success(voList));
    }

    /**
     * 根据市编码获取其下的区信息列表
     *
     * @param request
     * @return
     */
    @GetMapping("/getDistrictsByCityCode")
    @LogRecordAnnotation(moduleCode = 100007, moduleName = "公共接口", methodCode = 1000074, methodName = "getDistrictsByCityCode", description = "根据市编码获取其下的区信息列表")
    public ResponseEntity<Response> getDistrictsByCityCode(CityCodeRequest request) {
        String cityCode = request.getCityCode();
        List<SysArea> sysAreaList = sysAreaService.findByAreaLevelAndParentCode(3, cityCode);
        List<SysAreaVo> voList = TransferUtil.transfer(sysAreaList, SysAreaVo.class);
        return ResponseEntity.ok(Response.success(voList));
    }

    /**
     * 获取科目列表
     *
     * @param request
     * @return
     */
    @GetMapping("/getAllSubject")
    @LogRecordAnnotation(moduleCode = 100007, moduleName = "公共接口", methodCode = 1000075, methodName = "getAllSubject", description = "获取科目列表")
    public ResponseEntity<Response> getAllSubject(BaseRequest request) {
        List<SysEnum> sysEnumList = sysEnumService.findByEnumType(SYS_ENUM_TYPE_SUBJECT);
        List<SysEnumVo> voList = new ArrayList<>();
        SysEnumVo vo = new SysEnumVo();
        for (SysEnum sysEnum : sysEnumList) {
            vo = new SysEnumVo();
            vo.setName(sysEnum.getEnumName());
            vo.setValue(sysEnum.getUuid());
            voList.add(vo);
        }
        return ResponseEntity.ok(Response.success(voList));
    }

    @PostMapping("/saveAgoraCallLog")
    @LogRecordAnnotation(moduleCode = 100007, moduleName = "公共接口", methodCode = 1000076, methodName = "saveAgoraCallLog", description = "获取科目列表")
    public ResponseEntity<Response> saveAgoraCallLog(SaveAgoraCallLogRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        AgoraCallLog agoraCallLog = new AgoraCallLog();
        agoraCallLog.setAgoraCallLogUuid(UUIDUtil.randomUUID2());
        agoraCallLog.setCourseUuid(request.getCourseUuid());
        agoraCallLog.setAgoraUuid(request.getAgoraUuid());
        agoraCallLog.setAgoraType(request.getAgoraType());
        agoraCallLog.setAgoraCode(request.getAgoraCode());
        agoraCallLog.setDeviceType(loginUserCache.getDeviceType());
        agoraCallLog.setUserType(loginUserCache.getUserType());
        agoraCallLogService.save(agoraCallLog);
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 服务器时间
     *
     * @return
     */
    @GetMapping("/serverTime")
    public ResponseEntity<Response> serverTime() {
        ServerTimeVo vo = new ServerTimeVo();
        vo.setServerTime(System.currentTimeMillis());
        return ResponseEntity.ok(Response.success(vo));
    }
    /**
     * 服务器时间
     * 调用php
     * @return
     */
    //@GetMapping("/serverTime")
    public ResponseEntity<Response> serverTimePhp() {
        String phpUrl = url.concat("common/serverTime");
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> paramMap = new HashMap<>();
        headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        //php 成功返回
        String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
        Response phpResponse = JSON.parseObject(phpResult, Response.class);
        LOGGER.info("serverTimePhp phpResponse={}", phpResponse.getData());
        return ResponseEntity.ok(Response.success(phpResponse.getData()));
    }

    /**
     * 获取上课人员信息列表（声网uid和姓名）
     *
     * @param request
     * @return
     */
    //@GetMapping("/getCourseUserList")
    @LogRecordAnnotation(moduleCode = 100007, moduleName = "公共接口", methodCode = 1000078, methodName = "getCourseUserList", description = "获取上课人员信息列表（声网uid和姓名）")
    public ResponseEntity<Response> getCourseUserList(CourseUuidRequest request) {
        LoginUserCache cache=redisService.getLoginUserCache(request.getToken());
        String courseUuid = request.getCourseUuid();
        String curUuid = cache.getUserUuid();
        LOGGER.info("当前用户curUuid："+curUuid);
        CpCourse cpCourse = cpCourseService.findByUuid(courseUuid);
        if (cpCourse == null) {
            return ResponseEntity.ok(Response.error(NO_COURSE));
        }
        CourseUserListVo listVo = new CourseUserListVo();

        CourseUserVo stuUser = new CourseUserVo();
        String leadsUuid = cpCourse.getLeadsUuid();
        Leads leads = leadsService.findByUuid(leadsUuid);
        if (leads == null) {
            return ResponseEntity.ok(Response.error(COURSE_NO_STUDENT));
        }
        stuUser.setAgoraUid(getAgoraUid(leadsUuid, STUDENT.name()));
        stuUser.setName(leads.getName());
        listVo.setStuUser(stuUser);

        CourseUserVo teaUser = new CourseUserVo();
        String teacherUuid = cpCourse.getTeacherUuid();
        if (StringUtils.isNotBlank(teacherUuid)) {
            teaUser.setAgoraUid(getAgoraUid(teacherUuid, TEACHER.name()));
            TcTeacher tcTeacher = tcTeacherService.findByUuid(teacherUuid);
            if (tcTeacher != null) {
                teaUser.setName(tcTeacher.getTcName());
            }
        }
        listVo.setTeaUser(teaUser);

        CourseUserVo patUser = new CourseUserVo();
        PatriarchLeads patriarchLeads = patriarchLeadsService.findByLeadsUuid(leadsUuid);
        if (patriarchLeads != null) {
            patUser.setAgoraUid(getAgoraUid(patriarchLeads.getPatriarchUuid(), PATRIARCH.name()));
        }
        listVo.setPatUser(patUser);

        CourseUserVo ccUser = new CourseUserVo();
        String ccUuid = leads.getCcUuid();
        if (StringUtils.isNotBlank(ccUuid)) {
            ccUser.setAgoraUid(getAgoraUid(ccUuid, CC.name()));
            User user = userService.findByUuid(ccUuid);
            if (user != null) {
                ccUser.setName(user.getName());
            }
        }
        listVo.setCcUser(ccUser);

        CourseUserVo crUser = new CourseUserVo();
        String crUuid = leads.getCrUuid();
        if (StringUtils.isNotBlank(crUuid)) {
            crUser.setAgoraUid(getAgoraUid(crUuid, CR.name()));
            User user = userService.findByUuid(crUuid);
            if (user != null) {
                crUser.setName(user.getName());
            }
        }
        listVo.setCrUser(crUser);

        List<CourseUserVo> tsUserList = new ArrayList<>();
        CourseUserVo tsUser = new CourseUserVo();
        List<UserDto> tsUserDtoList = userService.findAllMonitor();   //拥有教学监课角色的人员列表
        for (UserDto userDto : tsUserDtoList) {
            tsUser = new CourseUserVo();
            String userUuid = userDto.getUserUuid();
            String userName = userDto.getUserName();
            tsUser.setAgoraUid(getAgoraUid(userUuid, MONITOR.name()));
            tsUser.setName(userName);
            tsUserList.add(tsUser);
        }
        listVo.setTsUserList(tsUserList);

        List<CourseUserVo> qcUserList = new ArrayList<>();
        CourseUserVo qcUser = new CourseUserVo();
        List<UserDto> qcUserDtoList = userService.findQCMonitor();   //拥有QC监课角色的人员列表
        for (UserDto userDto : qcUserDtoList) {
            qcUser = new CourseUserVo();
            String userUuid = userDto.getUserUuid();
            String userName = userDto.getUserName();
            qcUser.setAgoraUid(getAgoraUid(userUuid, QC.name()));
            qcUser.setName(userName);
            qcUserList.add(qcUser);
        }
        listVo.setQcUserList(qcUserList);
        /**start wqz返回技术支持角色*/
        CourseUserVo assistance = new CourseUserVo();
        if(StringUtils.isNotEmpty(curUuid)){
            UserDto dto=userService.findUserByUuid(curUuid);
            if(dto!=null){
                assistance.setName(dto.getUserName());
            }
            assistance.setAgoraUid(getAgoraUid(curUuid,TA.name()));
            listVo.setTechnicalAssistance(assistance);
        }
        /**end*/
        return ResponseEntity.ok(Response.success(listVo));
    }

    /**
     * 获取上课人员信息列表（声网uid和姓名）
     * 调用php
     * @param request
     * @return
     */
    @GetMapping("/getCourseUserList")
    @LogRecordAnnotation(moduleCode = 100007, moduleName = "公共接口", methodCode = 1000078, methodName = "getCourseUserListPhp", description = "获取上课人员信息列表（声网uid和姓名）")
    public ResponseEntity<Response> getCourseUserListPhp(CourseUuidRequest request) {
        Response phpResponse =null;
        try {
            LoginUserCache loginUserCache=redisService.getLoginUserCache(request.getToken());
            String phpUrl =url.concat("client/common/getCourseUserList?courseUuid=".concat(request.getCourseUuid()));
            CloseableHttpClient client =HttpClients.createDefault();
            HttpGet post=new HttpGet(phpUrl);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            post.setHeader("Authorization", "Bearer "+loginUserCache.getToken());
            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity=response.getEntity();
            String result=EntityUtils.toString(entity,"UTF-8");
            if(result==null){
                LOGGER.info("获取上课人员信息列表（声网uid和姓名）返回空");
            }
            phpResponse = JSON.parseObject(result, Response.class);

        }catch (Exception e){
            LOGGER.info("Exception={}", e.getMessage());
            e.printStackTrace();
        }
        return ResponseEntity.ok(phpResponse);
    }
    /**
     * 获取app版本信息
     *
     * @param request
     * @return
     */
    @GetMapping("/getAppInfo")
    @LogRecordAnnotation(moduleCode = 100007, moduleName = "公共接口", methodCode = 1000079, methodName = "getAppInfo", description = "获取app版本信息", checkToken = false)
    public ResponseEntity<Response> getAppInfo(AppInfoRequest2 request) {
        String deviceType = request.getDeviceType().toUpperCase();
        String userType = request.getUserType().toUpperCase();
        AppInfo appInfo = appInfoService.findByDeviceTypeAndUserType(deviceType, userType);
        if (appInfo == null) {
            return ResponseEntity.ok(Response.errorCustom("无此版本信息！"));
        }
        return ResponseEntity.ok(Response.success(TransferUtil.transfer(appInfo, AppInfoVo.class)));
    }

}
