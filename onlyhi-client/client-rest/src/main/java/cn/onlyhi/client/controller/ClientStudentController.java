package cn.onlyhi.client.controller;


import cn.onlyhi.client.dto.CourseDto;
import cn.onlyhi.client.dto.CourseInfoDto;
import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.po.AppInfo;
import cn.onlyhi.client.po.ClassRoom;
import cn.onlyhi.client.po.Leads;
import cn.onlyhi.client.po.LeadsExt;
import cn.onlyhi.client.po.PatriarchLeads;
import cn.onlyhi.client.po.PushMessage;
import cn.onlyhi.client.request.AppInfoRequest;
import cn.onlyhi.client.request.BingRequest;
import cn.onlyhi.client.request.CourseRecordRequest;
import cn.onlyhi.client.request.PhoneRequest;
import cn.onlyhi.client.request.SaveAvatarRequest;
import cn.onlyhi.client.request.UpdateStudentRequest;
import cn.onlyhi.client.service.AppInfoService;
import cn.onlyhi.client.service.ClassRoomService;
import cn.onlyhi.client.service.CourseLeadsService;
import cn.onlyhi.client.service.PatriarchLeadsService;
import cn.onlyhi.client.service.PushMessageService;
import cn.onlyhi.client.service.RoleService;
import cn.onlyhi.client.vo.*;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.enums.CodeEnum;
import cn.onlyhi.common.request.BaseRequest;
import cn.onlyhi.common.request.PageRequest;
import cn.onlyhi.common.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static cn.onlyhi.common.enums.ClientEnum.UserType.CC;
import static cn.onlyhi.common.enums.ClientEnum.UserType.CR;
import static cn.onlyhi.common.enums.ClientEnum.UserType.PATRIARCH;
import static cn.onlyhi.common.enums.ClientEnum.UserType.TEACHER;
import static cn.onlyhi.common.enums.CodeEnum.INVALID_IDENTITY;
import static cn.onlyhi.common.util.ClientUtil.getAgoraUid;
import static cn.onlyhi.common.util.ClientUtil.getFourRandom;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/3/17.
 */
@RestController
@RequestMapping("/client/student")
public class ClientStudentController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientStudentController.class);

    @Autowired
    private AppInfoService appInfoService;
    @Autowired
    private PushMessageService pushMessageService;
    @Autowired
    private ClassRoomService classRoomService;
    @Autowired
    private CourseLeadsService courseLeadsService;
    @Autowired
    private PatriarchLeadsService patriarchLeadsService;
    @Autowired
    private RoleService roleService;
    @Value("${phpStaging.url}")
    private String url;
    @PostMapping("/validateStudentInfoByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001120, methodName = "validateStudentInfoByJson", description = "判断学生是否有科目和年级信息")
    public ResponseEntity<Response> validateStudentInfoByJson(@RequestBody BaseRequest request) {
        return validateStudentInfoCom(request);
    }

    @PostMapping("/validateStudentInfo")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001121, methodName = "validateStudentInfo", description = "判断学生是否有科目和年级信息")
    public ResponseEntity<Response> validateStudentInfo(BaseRequest request) {
        return validateStudentInfoCom(request);
    }

    /**
     * 判断学生是否有科目和年级信息
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> validateStudentInfoCom(BaseRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        Leads leads = leadsService.findLeadsByPhone(loginUserCache.getPhone());
        if (leads == null) {
            LOGGER.info("用户不存在了！！！");
            return ResponseEntity.ok(Response.errorCustom("服务器错误，请联系管理员"));
        }
        if (StringUtils.isBlank(leads.getGrade()) || StringUtils.isBlank(leads.getSubject())) {
            return ResponseEntity.ok(Response.errorCustom("请完善个人年级和科目信息！"));
        }
        return ResponseEntity.ok(Response.success());
    }

    @PostMapping("/updateSubjectByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001118, methodName = "updateSubjectByJson", description = "更新学生科目")
    public ResponseEntity<Response> updateSubjectByJson(@RequestBody UpdateStudentRequest request) {
        return updateSubjectCom(request);
    }

    @PostMapping("/updateSubject")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001119, methodName = "updateSubject", description = "更新学生科目")
    public ResponseEntity<Response> updateSubject(UpdateStudentRequest request) {
        return updateSubjectCom(request);
    }

    /**
     * 更新学生科目
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> updateSubjectCom(UpdateStudentRequest request) {
        String leadsUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        leadsService.updateSubjectByUuid(leadsUuid, request.getSubject());
        return ResponseEntity.ok(Response.success());
    }

    @PostMapping("/saveAvatarByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000180, methodName = "saveAvatarByJson", description = "保存头像")
    public ResponseEntity<Response> saveAvatarByJson(@RequestBody SaveAvatarRequest request) throws Exception {
        return saveAvatarCom(request);
    }

    @PostMapping("/saveAvatar")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000181, methodName = "saveAvatar", description = "保存头像")
    public ResponseEntity<Response> saveAvatar(SaveAvatarRequest request) throws Exception {
        return saveAvatarCom(request);
    }

    /**
     * 保存头像
     */
    private ResponseEntity<Response> saveAvatarCom(SaveAvatarRequest request) throws Exception {
        String imageName = request.getImageName();
        String imagePath = request.getImagePath();
        String leadsUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        LeadsExt leadsExt = leadsExtService.findLeadsExtByLeadsUuid(leadsUuid);
        if (leadsExt == null) {
            leadsExt = new LeadsExt();
            leadsExt.setLeadsUuid(leadsUuid);
            leadsExt.setIconurl(imagePath);
            leadsExt.setIconname(imageName);
            leadsExtService.save(leadsExt);
        } else {
            leadsExt.setIconurl(imagePath);
            leadsExt.setIconname(imageName);
            leadsExtService.updateLeadsExtByLeadsUuid(leadsExt);
        }
        return ResponseEntity.ok(Response.success());
    }

    @PostMapping("/isRegisterByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000111, methodName = "isRegisterByJson", description = "判断学生账号是否注册", checkToken = false)
    public ResponseEntity<Response> isRegisterByJson(@RequestBody PhoneRequest request) throws Exception {
        return isRegisterComm(request);
    }

    @PostMapping("/isRegister")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000112, methodName = "isRegister", description = "判断学生账号是否注册", checkToken = false)
    public ResponseEntity<Response> isRegister(PhoneRequest request) throws Exception {
        return isRegisterComm(request);
    }

    /**
     * 判断学生账号是否注册
     */
    private ResponseEntity<Response> isRegisterComm(PhoneRequest request) throws Exception {
        Leads leads = leadsService.findLeadsByPhone(request.getPhone());
        UserRegisterVo userRegisterVo = new UserRegisterVo();
        if (leads != null) {
            userRegisterVo.setRegisterFlag(true);
            userRegisterVo.setUserName(leads.getName());
        } else {
            userRegisterVo.setRegisterFlag(false);
            userRegisterVo.setUserName("");
        }
        return ResponseEntity.ok(Response.success(userRegisterVo));
    }

    @PostMapping("/bingAccountByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000113, methodName = "bingAccountByJson", description = "绑定app和学生账号,用户推送消息")
    public ResponseEntity<Response> bingAccountByJson(@RequestBody BingRequest request) throws Exception {
        return bingAccountCom(request);
    }

    @PostMapping("/bingAccount")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000114, methodName = "bingAccount", description = "绑定app和学生账号,用户推送消息")
    public ResponseEntity<Response> bingAccount(BingRequest request) throws Exception {
        return bingAccountCom(request);
    }

    /**
     * 绑定app和学生账号,用户推送消息
     */
    private ResponseEntity<Response> bingAccountCom(BingRequest request) throws Exception {
        String deviceToken = request.getDeviceToken();
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String leadsUuid = loginUserCache.getUserUuid();
        PushMessage pushMessage = new PushMessage();
        BeanUtils.copyProperties(pushMessage, request);

        pushMessage.setLeadsUuid(leadsUuid);
        pushMessage.setDeviceType(loginUserCache.getDeviceType());

        PushMessage message = pushMessageService.findByLeadsUuidOrDeviceToken(leadsUuid, deviceToken);
        if (message == null) {
            pushMessage.setPushMessageUuid(UUIDUtil.randomUUID2());
            pushMessage.setCreateUid(leadsUuid);
            pushMessageService.save(pushMessage);
        } else {
            pushMessage.setUpdateUid(leadsUuid);
            pushMessage.setPushMessageUuid(message.getPushMessageUuid());
            pushMessageService.update(pushMessage);
        }
        return ResponseEntity.ok(Response.success());
    }


    @PostMapping("/getClassTimeInfoByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000115, methodName = "getClassTimeInfoByJson", description = "个人课时消耗统计")
    public ResponseEntity<Response> getClassTimeInfoByJson(@RequestBody BaseRequest request) throws ParseException {
        return getClassTimeInfoCom(request);
    }

    @PostMapping("/getClassTimeInfo")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000116, methodName = "getClassTimeInfo", description = "个人课时消耗统计")
    public ResponseEntity<Response> getClassTimeInfo(BaseRequest request) throws ParseException {
        return getClassTimeInfoCom(request);
    }

    /**
     * 个人课时消耗统计
     */
    private ResponseEntity<Response> getClassTimeInfoCom(BaseRequest request) throws ParseException {
        String leadsUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        List<CourseInfoDto> courseInfoDtoList = cpCourseService.findCourseInfoByLeadsUuid(leadsUuid);
        List<ClassTimeVo> classTimeVoList = new ArrayList<>();
        ClassTimeVo classTimeVo;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (CourseInfoDto courseInfoDto : courseInfoDtoList) {
            classTimeVo = new ClassTimeVo();
            String classPackageName = courseInfoDto.getClassPackageName();
            String totalTimeString = courseInfoDto.getTotalTime();
            String courseDateString = courseInfoDto.getCourseDate();
            String startTimeString = courseInfoDto.getStartTime();
            String endTimeString = courseInfoDto.getEndTime();
            String courseUuid = courseInfoDto.getCourseUuid();
            ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
            if (classRoom != null) {
                Long outRoomTime = classRoom.getOutRoomTime();
                Date startDate = dateFormat.parse(courseDateString + " " + startTimeString + ":00");
                Date endDate = dateFormat.parse(courseDateString + " " + endTimeString + ":00");
                long startTime = startDate.getTime();
                long endTime = endDate.getTime();
                if (outRoomTime != null) {
                    if (outRoomTime < endTime) {
                        endTime = outRoomTime;
                    }
                }
                BigDecimal totalTime = new BigDecimal(totalTimeString);
                //消耗的总课时（小时）
                BigDecimal consumeTime = new BigDecimal((endTime - startTime) / (1000 * 60 * 60.0)).setScale(2, BigDecimal.ROUND_HALF_UP);
                //剩余的课时
                BigDecimal surplusTime = totalTime.subtract(consumeTime);

                classTimeVo.setSurplusTime(surplusTime.toString());
            } else {
                classTimeVo.setSurplusTime(totalTimeString);
            }

            classTimeVo.setClassPackageName(classPackageName);
            classTimeVo.setTotalTime(totalTimeString);

            classTimeVoList.add(classTimeVo);
        }
        return ResponseEntity.ok(Response.success(classTimeVoList));
    }


    @PostMapping("/iosGetAppInfo")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000117, methodName = "iosGetAppInfo", description = "获取app版本信息", checkToken = false)
    public ResponseEntity<Response> iosGetAppInfo(@RequestBody AppInfoRequest request) {
        return getAppInfoCom(request);
    }

    @PostMapping("/getAppInfo")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000118, methodName = "getAppInfo", description = "获取app版本信息", checkToken = false)
    public ResponseEntity<Response> getAppInfo(AppInfoRequest request) {
        return getAppInfoCom(request);
    }

    /**
     * 获取app版本信息
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> getAppInfoCom(AppInfoRequest request) {
        String deviceType = request.getDeviceType().toUpperCase();
        AppInfo newestAppInfo = appInfoService.findNewestAppInfo(deviceType);
        if (newestAppInfo == null) {
            LOGGER.info("app[" + deviceType + "]版本信息未设置！！！");
        }
        return ResponseEntity.ok(Response.success(TransferUtil.transfer(newestAppInfo, AppInfoVo.class)));
    }

    /**
     * 学生课程记录（一对一）
     *
     * @param request
     * @return
     */
    @GetMapping("/getCourseRecordV1List")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000119, methodName = "getCourseRecordV1List", description = "学生课程记录（一对一）")
    public ResponseEntity<Response> getCourseRecordV1List(CourseRecordRequest request) throws ParseException {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String leadsUuid = loginUserCache.getUserUuid();
        String userType = loginUserCache.getUserType();
        String subject = request.getSubject();
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();
        Integer pageNo = request.getPageNo();
        Integer pageSize = request.getPageSize();
        List<CourseDto> courseDtoTmpList = cpCourseService.findStudentCourseRecordV1List(leadsUuid, subject, startDate, endDate, 1, Integer.MAX_VALUE);
        Object[] objects = getCourseDtoPageList(courseDtoTmpList, pageNo, pageSize, true, userType);
        int count = (int) objects[0];
        List<CourseDto> courseDtoList = (List<CourseDto>) objects[1];
        List<CourseListVo> voList = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        CourseListVo courseListVo;
        for (CourseDto courseDto : courseDtoList) {
            String courseUuid = courseDto.getCourseUuid();
            String courseDate = courseDto.getCourseDate();
            String startTime = courseDto.getStartTime();
            String endTime = courseDto.getEndTime();
            ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
            if (classRoom != null) {
                Long outRoomTime = classRoom.getOutRoomTime();
                if (outRoomTime != null) {
                    if (outRoomTime < dateFormat.parse(courseDate + " " + endTime).getTime()) {
                        endTime = dateFormat.format(outRoomTime).substring(11);
                    }
                }
            }
            courseListVo = new CourseListVo();
            courseListVo.setCourseUuid(courseUuid);
            courseListVo.setCourseDate(courseDate);
            courseListVo.setStartTime(startTime);
            courseListVo.setEndTime(endTime);
            courseListVo.setSubject(courseDto.getSubject());
            courseListVo.setTeacherName(courseDto.getTeacherName());
            courseListVo.setClassTeacherAppraiseUuid(courseDto.getClassTeacherAppraiseUuid());
            courseListVo.setClassAppraiseUuid(courseDto.getClassAppraiseUuid());
            courseListVo.setGrade(courseDto.getGrade());
            courseListVo.setCourseType(courseDto.getCourseType());
            voList.add(courseListVo);
        }
        Page<CourseListVo> page = new Page<>(count, voList);
        return ResponseEntity.ok(Response.success(page));
    }

    /**
     * 学生课程记录（一对多）
     *
     * @param request
     * @return
     */
    @GetMapping("/getCourseRecordList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000120, methodName = "getCourseRecordList", description = "学生课程记录（一对多）")
    public ResponseEntity<Response> getCourseRecordList(CourseRecordRequest request) {
        String leadsUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        String subject = request.getSubject();
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();
        Integer pageNo = request.getPageNo();
        Integer pageSize = request.getPageSize();
        long count = cpCourseService.countStudentCourseRecordList(leadsUuid, subject, startDate, endDate);
        List<CourseDto> dtoList = cpCourseService.findStudentCourseRecordList(leadsUuid, subject, startDate, endDate, pageNo, pageSize);
        List<CourseListVo> voList = TransferUtil.transfer(dtoList, CourseListVo.class);
        Page<CourseListVo> page = new Page<>(count, voList);
        return ResponseEntity.ok(Response.success(page));
    }
    /**
     * 学生待上课列表（一对一）
     * 调用php
     * @param request
     * @return
     */
    @GetMapping("/noEndCourseV1List")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000121, methodName = "noEndCourseV1ListPhp", description = "学生待上课列表（一对一）")
    public ResponseEntity<Response> noEndCourseV1ListPhp(PageRequest request) throws ParseException {
        LoginUserCache loginUserCache=redisService.getLoginUserCache(request.getToken());
        String phpUrl =url.concat("client/student/noEndCourseV1List");
        Response phpResponse =null;
        try {
            CloseableHttpClient client =HttpClients.createDefault();
            HttpPost post=new HttpPost(phpUrl);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            post.setHeader("Authorization", "Bearer ".concat(loginUserCache.getToken()));

            JSONObject param= new JSONObject();
            StringEntity stringEntity = new StringEntity(param.toString());
            post.setEntity(stringEntity);

            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity=response.getEntity();
            String result=EntityUtils.toString(entity,"UTF-8");
            if(result==null){
                LOGGER.info("学生待上课列表（一对一）返回空");
            }

            phpResponse = JSON.parseObject(result, Response.class);
            Object object=phpResponse.getData();
            if(object!=null){
                DataVO dataVO=JSON.parseObject(object.toString(),DataVO.class);
                if(Objects.equals(dataVO.getList().size(),0)){
                    LOGGER.info("学生待上课列表,返回空会导致进入房间失败");
                }
            }
        }catch (Exception e){
            LOGGER.info("Exception={}", e.getMessage());
        }
        return ResponseEntity.ok(phpResponse);
    }
    /**
     * 学生待上课列表（一对一）
     *
     * @param request
     * @return
     */
    //@GetMapping("/noEndCourseV1List")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000121, methodName = "noEndCourseV1List", description = "学生待上课列表（一对一）")
    public ResponseEntity<Response> noEndCourseV1List(PageRequest request) throws ParseException {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String leadsUuid = loginUserCache.getUserUuid();
        String userType = loginUserCache.getUserType();
        Integer pageNo = request.getPageNo();
        Integer pageSize = request.getPageSize();
        List<CourseDto> courseDtoTmpList = cpCourseService.findNoEndCourseByLeadsUuid(leadsUuid, 1, Integer.MAX_VALUE);
        Object[] objects = getCourseDtoPageList(courseDtoTmpList, pageNo, pageSize, false, userType);
        int count = (int) objects[0];
        List<CourseDto> courseDtoList = (List<CourseDto>) objects[1];
        PatriarchLeads patriarchLeads = patriarchLeadsService.findByLeadsUuid(leadsUuid);
        Integer patriarchAgoraUid = null;
        if (patriarchLeads != null) {
            patriarchAgoraUid = getAgoraUid(patriarchLeads.getPatriarchUuid(), PATRIARCH.name());
        }
        List<CourseListVo> voList = new ArrayList<>();
        CourseListVo vo;
        for (CourseDto courseDto : courseDtoList) {
            vo = TransferUtil.transfer(courseDto, CourseListVo.class);
            vo.setChannelPatriarchId(patriarchAgoraUid);
            String teacherUuid = courseDto.getTeacherUuid();
            if (StringUtils.isBlank(teacherUuid)) {
                String userUuid = courseDto.getUserUuid();
                List<String> roleAliasList = roleService.findByUserUuid(userUuid);
                if (roleAliasList == null) {
                    return ResponseEntity.ok(Response.error(INVALID_IDENTITY));
                }
                if (roleAliasList.contains("cc")) {
                    vo.setChannelTeacherId(getAgoraUid(userUuid, CC.name()));
                } else {
                    vo.setChannelTeacherId(getAgoraUid(userUuid, CR.name()));
                }
            } else {
                vo.setChannelTeacherId(getAgoraUid(teacherUuid, TEACHER.name()));
            }
            voList.add(vo);
        }
        Page<CourseListVo> page = new Page<>(count, voList);
        return ResponseEntity.ok(Response.success(page));
    }

    /**
     * 学生待上课列表（一对多）
     *
     * @param request
     * @return
     */
    @GetMapping("/noEndCourseList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001147, methodName = "noEndCourseList", description = "学生待上课列表（一对多）")
    public ResponseEntity<Response> noEndCourseList(PageRequest request) {
        String leadsUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        Integer pageNo = request.getPageNo();
        Integer pageSize = request.getPageSize();
        int count = courseLeadsService.countNoEndCourseByLeadsUuid(leadsUuid);
        List<CourseDto> courseDtoList = courseLeadsService.findNoEndCourseByLeadsUuid(leadsUuid, pageNo, pageSize);
        List<CourseListVo> voList = new ArrayList<>();
        CourseListVo vo;
        for (CourseDto courseDto : courseDtoList) {
            vo = TransferUtil.transfer(courseDto, CourseListVo.class);
            String teacherUuid = courseDto.getTeacherUuid();
            if (StringUtils.isBlank(teacherUuid)) {
                String userUuid = courseDto.getUserUuid();
                List<String> roleAliasList = roleService.findByUserUuid(userUuid);
                if (roleAliasList == null) {
                    return ResponseEntity.ok(Response.error(INVALID_IDENTITY));
                }
                if (roleAliasList.contains("cc")) {
                    vo.setChannelTeacherId(getAgoraUid(userUuid, CC.name()));
                } else {
                    vo.setChannelTeacherId(getAgoraUid(userUuid, CR.name()));
                }

            } else {
                vo.setChannelTeacherId(getAgoraUid(teacherUuid, TEACHER.name()));
            }
            voList.add(vo);
        }
        Page<CourseListVo> page = new Page<>(count, voList);
        return ResponseEntity.ok(Response.success(page));
    }

    @PostMapping("/iosUpdateSex")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000123, methodName = "iosUpdateSex", description = "更新学生性别")
    public ResponseEntity<Response> iosUpdateSex(@RequestBody UpdateStudentRequest request) {
        return updateSexCom(request);
    }

    @PostMapping("/updateSex")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000124, methodName = "updateSex", description = "更新学生性别")
    public ResponseEntity<Response> updateSex(UpdateStudentRequest request) {
        return updateSexCom(request);
    }

    private ResponseEntity<Response> updateSexCom(UpdateStudentRequest request) {
        String leadsUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        leadsService.updateSexByUuid(leadsUuid, request.getSex());
        return ResponseEntity.ok(Response.success());
    }

    @PostMapping("/iosUpdateAge")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000125, methodName = "iosUpdateAge", description = "更新学生年龄")
    public ResponseEntity<Response> iosUpdateAge(@RequestBody UpdateStudentRequest request) {
        return updateAgeCom(request);
    }

    @PostMapping("/updateAge")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000126, methodName = "updateAge", description = "更新学生年龄")
    public ResponseEntity<Response> updateAge(UpdateStudentRequest request) {
        return updateAgeCom(request);
    }

    private ResponseEntity<Response> updateAgeCom(UpdateStudentRequest request) {
        String leadsUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        leadsService.updateSexByUuid(leadsUuid, request.getAge());
        return ResponseEntity.ok(Response.success());
    }

    @PostMapping("/iosUpdateGrade")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000127, methodName = "iosUpdateGrade", description = "更新学生年级")
    public ResponseEntity<Response> iosUpdateGrade(@RequestBody UpdateStudentRequest request) {
        return updateGradeCom(request);
    }

    @PostMapping("/updateGrade")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000128, methodName = "updateGrade", description = "更新学生年级")
    public ResponseEntity<Response> updateGrade(UpdateStudentRequest request) {
        return updateGradeCom(request);
    }

    private ResponseEntity<Response> updateGradeCom(UpdateStudentRequest request) {
        String leadsUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        leadsService.updateGradeByUuid(leadsUuid, request.getGrade());
        return ResponseEntity.ok(Response.success());
    }

    @PostMapping("/iosUpdateExamArea")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000129, methodName = "iosUpdateExamArea", description = "更新学生高考地区")
    public ResponseEntity<Response> iosUpdateExamArea(@RequestBody UpdateStudentRequest request) {
        return updateExamAreaCom(request);
    }

    @PostMapping("/updateExamArea")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000130, methodName = "updateExamArea", description = "更新学生高考地区")
    public ResponseEntity<Response> updateExamArea(UpdateStudentRequest request) {
        return updateExamAreaCom(request);
    }

    public ResponseEntity<Response> updateExamAreaCom(UpdateStudentRequest request) {
        String leadsUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        leadsService.updateExamAreaByUuid(leadsUuid, request.getExamArea());
        return ResponseEntity.ok(Response.success());
    }
}
