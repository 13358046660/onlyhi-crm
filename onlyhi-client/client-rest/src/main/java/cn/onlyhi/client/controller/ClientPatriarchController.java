package cn.onlyhi.client.controller;


import cn.onlyhi.client.dto.CourseDto;
import cn.onlyhi.client.dto.CourseInfoDto;
import cn.onlyhi.client.dto.LeadsExtendEntity;
import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.po.ClassRoom;
import cn.onlyhi.client.po.Leads;
import cn.onlyhi.client.po.Patriarch;
import cn.onlyhi.client.po.PatriarchLeads;
import cn.onlyhi.client.po.PatriarchLeadsRecord;
import cn.onlyhi.client.request.BingChildrenRequest;
import cn.onlyhi.client.request.CodeBingChildrenRequest;
import cn.onlyhi.client.request.CourseRecordRequest;
import cn.onlyhi.client.request.PatriarchRegisterRequest;
import cn.onlyhi.client.service.ClassRoomService;
import cn.onlyhi.client.service.CpCourseService;
import cn.onlyhi.client.service.PatriarchLeadsRecordService;
import cn.onlyhi.client.service.PatriarchLeadsService;
import cn.onlyhi.client.service.PatriarchService;
import cn.onlyhi.client.vo.BingInfoVo;
import cn.onlyhi.client.vo.ClassTimeVo;
import cn.onlyhi.client.vo.CourseListVo;
import cn.onlyhi.client.vo.PatriarchNoEndV1CourseListVo;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.request.BaseRequest;
import cn.onlyhi.common.request.PageRequest;
import cn.onlyhi.common.util.Page;
import cn.onlyhi.common.util.Response;
import cn.onlyhi.common.util.TransferUtil;
import cn.onlyhi.common.util.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static cn.onlyhi.common.enums.ClientEnum.UserType.PATRIARCH;
import static cn.onlyhi.common.enums.ClientEnum.UserType.STUDENT;
import static cn.onlyhi.common.enums.ClientEnum.UserType.TEACHER;
import static cn.onlyhi.common.enums.CodeEnum.ALREADY_BING;
import static cn.onlyhi.common.enums.CodeEnum.AUTHCODE_ERROR;
import static cn.onlyhi.common.enums.CodeEnum.AUTHCODE_TIMEOUT;
import static cn.onlyhi.common.enums.CodeEnum.INVALID_MOBILE;
import static cn.onlyhi.common.enums.CodeEnum.MOBILE_HAS_REGISTERED;
import static cn.onlyhi.common.enums.CodeEnum.NO_BING;
import static cn.onlyhi.common.enums.CodeEnum.P_ALREADY_BING;
import static cn.onlyhi.common.util.ClientUtil.getAgoraUid;

/**
 * 家长
 *
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/5.
 */
@RestController
@RequestMapping("/client/patriarch")
public class ClientPatriarchController extends BaseController {

    @Autowired
    private PatriarchService patriarchService;
    @Autowired
    private CpCourseService courseService;
    @Autowired
    private PatriarchLeadsService patriarchLeadsService;
    @Autowired
    private PatriarchLeadsRecordService patriarchLeadsRecordService;
    @Autowired
    private ClassRoomService classRoomService;

    @PostMapping("checkBingStatusByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000150, methodName = "checkBingStatusByJson", description = "判断是否绑定小孩账号")
    public ResponseEntity<Response> checkBingStatusByJson(@RequestBody BaseRequest request) {
        return checkBingStatusCom(request);
    }

    @PostMapping("checkBingStatus")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000151, methodName = "checkBingStatus", description = "判断是否绑定小孩账号")
    public ResponseEntity<Response> checkBingStatus(BaseRequest request) {
        return checkBingStatusCom(request);
    }

    /**
     * 判断是否绑定小孩账号
     */
    private ResponseEntity<Response> checkBingStatusCom(BaseRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String phone = loginUserCache.getPhone();
        PatriarchLeadsRecord patriarchLeadsRecord = patriarchLeadsRecordService.findByPhone(phone);
        BingInfoVo bingInfoVo = new BingInfoVo();
        if (patriarchLeadsRecord != null) {    //绑定过
            PatriarchLeads patriarchLeads = patriarchLeadsService.findByLeadsUuid(patriarchLeadsRecord.getLeadsUuid());
            if (!checkBing(patriarchLeads)) {
                bingInfoVo.setBingStatus(1);
            } else {
                String patriarchPhone = patriarchLeads.getPatriarchPhone();
                if (phone.equals(patriarchPhone)) {    //最后绑定学生的是自己
                    bingInfoVo.setBingStatus(2);
                } else {  //被他人解绑了
                    bingInfoVo.setBingStatus(3);
                    bingInfoVo.setPhone(patriarchPhone);
                }
                Leads leads = leadsService.findLeadsByPhone(patriarchLeadsRecord.getLeadsPhone());
                bingInfoVo.setChildrenName(leads.getName());
            }
        } else {
            bingInfoVo.setBingStatus(1);
        }
        return ResponseEntity.ok(Response.success(bingInfoVo));
    }

    @PostMapping("coverBingByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000152, methodName = "coverBingByJson", description = "覆盖绑定小孩账号的家长手机号", checkToken = false)
    public ResponseEntity<Response> coverBingByJson(@RequestBody BingChildrenRequest request) throws Exception {
        return coverBingCom(request);
    }

    @PostMapping("coverBing")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000153, methodName = "coverBing", description = "覆盖绑定小孩账号的家长手机号", checkToken = false)
    public ResponseEntity<Response> coverBing(BingChildrenRequest request) throws Exception {
        return coverBingCom(request);
    }

    /**
     * 覆盖绑定小孩账号的家长手机号
     *
     * @param request
     * @return
     * @throws Exception
     */
    private ResponseEntity<Response> coverBingCom(BingChildrenRequest request) throws Exception {
        String phone = request.getPhone();
        String password = request.getPassword();
        String patriarchPhone = request.getPatriarchPhone();

        Leads leads = leadsService.findLeadsByPhone(phone);
        if (leads == null) {
            return ResponseEntity.ok(Response.error(INVALID_MOBILE.getCode(), "无此学生"));
        }
        if (!password.equals(leads.getPassword())) {
            return ResponseEntity.ok(Response.error(INVALID_MOBILE.getCode(), "学生密码不正确"));
        }
        PatriarchLeads patriarchLeads = patriarchLeadsService.findByLeadsUuid(leads.getUuid());
        if (!checkBing(patriarchLeads)) { //该学生未被绑定
            return ResponseEntity.ok(Response.error(NO_BING));
        } else {
            Patriarch patriarch = patriarchService.findByphone(patriarchPhone);
            patriarchLeads.setPatriarchUuid(patriarch.getPatriarchUuid());
            patriarchLeads.setPatriarchPhone(patriarchPhone);
            patriarchLeads.setStatus(1);
            patriarchLeadsService.update(patriarchLeads);
        }
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 检查学生是否被绑定
     *
     * @param patriarchLeads
     * @return true:被绑定；false未被绑定
     */
    private boolean checkBing(PatriarchLeads patriarchLeads) {
        if (patriarchLeads == null || patriarchLeads.getStatus() == 0) {
            return false;
        }
        return true;
    }

    @PostMapping("codeBingChildrenByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001150, methodName = "codeBingChildrenByJson", description = "通过验证码绑定小孩账号", checkToken = false)
    public ResponseEntity<Response> codeBingChildrenByJson(@RequestBody CodeBingChildrenRequest request) throws Exception {
        return codeBingChildrenCom(request);
    }

    @PostMapping("codeBingChildren")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001151, methodName = "codeBingChildren", description = "通过验证码绑定小孩账号", checkToken = false)
    public ResponseEntity<Response> codeBingChildren(CodeBingChildrenRequest request) throws Exception {
        return codeBingChildrenCom(request);
    }

    /**
     * 通过验证码绑定小孩账号
     *
     * @param request
     * @return
     * @throws Exception
     */
    private ResponseEntity<Response> codeBingChildrenCom(CodeBingChildrenRequest request) throws Exception {
        String phone = request.getPhone();
        String authCode = request.getAuthCode();
        String patriarchPhone = request.getPatriarchPhone();

        Leads leads = leadsService.findLeadsByPhone(phone);
        if (leads == null) {
            return ResponseEntity.ok(Response.error(INVALID_MOBILE.getCode(), "无此学生"));
        }
        String authCodecache = getAuthCode(phone);
        if (StringUtils.isBlank(authCodecache)) {
            return ResponseEntity.ok(Response.error(AUTHCODE_TIMEOUT));
        }
        if (!authCode.equals(authCodecache)) {
            return ResponseEntity.ok(Response.error(AUTHCODE_ERROR));
        }
        return codeBingChildrenCom(leads, patriarchPhone);
    }

    private ResponseEntity<Response> codeBingChildrenCom(Leads leads, String patriarchPhone) {
        if (patriarchLeadsService.findByPhone(patriarchPhone) != null) {    //家长已绑定学生
            return ResponseEntity.ok(Response.error(P_ALREADY_BING));
        }
        PatriarchLeads patriarchLeads = patriarchLeadsService.findByLeadsUuid(leads.getUuid());
        Patriarch patriarch = patriarchService.findByphone(patriarchPhone);
        if (patriarchLeads == null) { //该学生未被绑定
            patriarchLeads = new PatriarchLeads();
            patriarchLeads.setLeadsUuid(leads.getUuid());
            patriarchLeads.setLeadsPhone(leads.getPhone());
            patriarchLeads.setPatriarchUuid(patriarch.getPatriarchUuid());
            patriarchLeads.setPatriarchPhone(patriarchPhone);
            patriarchLeads.setPatriarchLeadsUuid(UUIDUtil.randomUUID2());
            patriarchLeadsService.save(patriarchLeads);
        } else {
            if (patriarchLeads.getStatus() == 0) {   //绑定过现在已解绑,未被绑定
                patriarchLeads.setPatriarchUuid(patriarch.getPatriarchUuid());
                patriarchLeads.setPatriarchPhone(patriarchPhone);
                patriarchLeads.setStatus(1);
                patriarchLeadsService.update(patriarchLeads);
            } else {
                BingInfoVo bingInfoVo = new BingInfoVo();
                bingInfoVo.setPhone(patriarchLeads.getPatriarchPhone());
                bingInfoVo.setChildrenName(leads.getName());
                return ResponseEntity.ok(Response.error(ALREADY_BING, bingInfoVo));
            }
        }
        return ResponseEntity.ok(Response.success());
    }

    @PostMapping("bingChildrenByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000154, methodName = "bingChildrenByJson", description = "通过密码绑定小孩账号", checkToken = false)
    public ResponseEntity<Response> bingChildrenByJson(@RequestBody BingChildrenRequest request) throws Exception {
        return bingChildrenCom(request);
    }

    @PostMapping("bingChildren")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000155, methodName = "bingChildren", description = "通过密码绑定小孩账号", checkToken = false)
    public ResponseEntity<Response> bingChildren(BingChildrenRequest request) throws Exception {
        return bingChildrenCom(request);
    }

    /**
     * 通过密码绑定小孩账号
     *
     * @param request
     * @return
     * @throws Exception
     */
    private ResponseEntity<Response> bingChildrenCom(BingChildrenRequest request) throws Exception {
        String phone = request.getPhone();
        String password = request.getPassword();
        String patriarchPhone = request.getPatriarchPhone();

        Leads leads = leadsService.findLeadsByPhone(phone);
        if (leads == null) {
            return ResponseEntity.ok(Response.error(INVALID_MOBILE.getCode(), "无此学生"));
        }
        if (!password.equals(leads.getPassword())) {
            return ResponseEntity.ok(Response.error(INVALID_MOBILE.getCode(), "学生密码不正确"));
        }
        return codeBingChildrenCom(leads, patriarchPhone);
    }

    @PostMapping("getClassTimeInfoByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000156, methodName = "getClassTimeInfoByJson", description = "学生课时消耗统计")
    public ResponseEntity<Response> getClassTimeInfoByJson(@RequestBody BaseRequest request) throws Exception {
        return getClassTimeInfoCom(request);
    }

    @PostMapping("getClassTimeInfo")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000157, methodName = "getClassTimeInfo", description = "学生课时消耗统计")
    public ResponseEntity<Response> getClassTimeInfo(BaseRequest request) throws Exception {
        return getClassTimeInfoCom(request);
    }

    /**
     * 学生课时消耗统计
     */
    private ResponseEntity<Response> getClassTimeInfoCom(BaseRequest request) throws Exception {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        PatriarchLeads patriarch = patriarchLeadsService.findByPhone(loginUserCache.getPhone());
        if (patriarch == null) {
            PatriarchLeadsRecord patriarchLeadsRecord = patriarchLeadsRecordService.findByPhone(loginUserCache.getPhone());
            BingInfoVo bingInfoVo = new BingInfoVo();
            if (patriarchLeadsRecord != null) {
                String leadsUuid = patriarchLeadsRecord.getLeadsUuid();
                PatriarchLeads patriarchLeads = patriarchLeadsService.findByLeadsUuid(leadsUuid);
                Leads leads = leadsService.findLeadsByPhone(patriarchLeads.getLeadsPhone());
                bingInfoVo.setPhone(patriarchLeads.getPatriarchPhone());
                bingInfoVo.setChildrenName(leads.getName());
            }
            return ResponseEntity.ok(Response.error(NO_BING, bingInfoVo));
        }

        List<CourseInfoDto> courseInfoDtoList = courseService.findCourseInfoByLeadsUuid(patriarch.getLeadsUuid());
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


    @PostMapping("/iosGetStudentInfo")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000158, methodName = "iosGetStudentInfo", description = "获取学生信息")
    public ResponseEntity<Response> iosGetStudentInfo(@RequestBody BaseRequest request) {
        return getStudentInfoCom(request);
    }

    @PostMapping("/getStudentInfo")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000159, methodName = "getStudentInfo", description = "获取学生信息")
    public ResponseEntity<Response> getStudentInfo(BaseRequest request) {
        return getStudentInfoCom(request);
    }

    /**
     * 获取学生信息
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> getStudentInfoCom(BaseRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        PatriarchLeads patriarch = patriarchLeadsService.findByPhone(loginUserCache.getPhone());
        if (patriarch == null) {
            PatriarchLeadsRecord patriarchLeadsRecord = patriarchLeadsRecordService.findByPhone(loginUserCache.getPhone());
            BingInfoVo bingInfoVo = new BingInfoVo();
            if (patriarchLeadsRecord != null) {
                String leadsUuid = patriarchLeadsRecord.getLeadsUuid();
                PatriarchLeads patriarchLeads = patriarchLeadsService.findByLeadsUuid(leadsUuid);
                Leads leads = leadsService.findLeadsByPhone(patriarchLeads.getLeadsPhone());
                bingInfoVo.setPhone(patriarchLeads.getPatriarchPhone());
                bingInfoVo.setChildrenName(leads.getName());
            }
            return ResponseEntity.ok(Response.error(NO_BING, bingInfoVo));
        }
        //Leads leads = leadsService.findLeadsByPhone(patriarch.getLeadsPhone());
        //不删除旧方法原则，新增查询方法查询头像信息
        LeadsExtendEntity leads=leadsService.selectLeadsByPhone(patriarch.getLeadsPhone());
        leads.setPassword("");
        return ResponseEntity.ok(Response.success(leads));
    }

    /**
     * 家长端查看学生待上课列表（一对一）
     *
     * @param request
     * @return
     */
    @GetMapping("/noEndCourseV1List")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000160, methodName = "noEndCourseV1List", description = "家长端查看学生待上课列表（一对一）")
    public ResponseEntity<Response> noEndCourseV1List(PageRequest request) throws ParseException {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userType = loginUserCache.getUserType();
        PatriarchLeads patriarch = patriarchLeadsService.findByPhone(loginUserCache.getPhone());
        if (patriarch == null) {
            PatriarchLeadsRecord patriarchLeadsRecord = patriarchLeadsRecordService.findByPhone(loginUserCache.getPhone());
            BingInfoVo bingInfoVo = new BingInfoVo();
            if (patriarchLeadsRecord != null) {
                String leadsUuid = patriarchLeadsRecord.getLeadsUuid();
                PatriarchLeads patriarchLeads = patriarchLeadsService.findByLeadsUuid(leadsUuid);
                Leads leads = leadsService.findLeadsByPhone(patriarchLeads.getLeadsPhone());
                bingInfoVo.setPhone(patriarchLeads.getPatriarchPhone());
                bingInfoVo.setChildrenName(leads.getName());
            }
            return ResponseEntity.ok(Response.error(NO_BING, bingInfoVo));
        }
        String leadsUuid = patriarch.getLeadsUuid();
        String patriarchUuid = loginUserCache.getUserUuid();
        int pageNo = request.getPageNo();
        int pageSize = request.getPageSize();
        List<CourseDto> courseDtoTmpList = courseService.findNoEndCourseByPatriarchMonitor(patriarchUuid, 1, Integer.MAX_VALUE);
        Object[] objects = getCourseDtoPageList(courseDtoTmpList, pageNo, pageSize, false, userType);
        int count = (int) objects[0];
        List<CourseDto> courseDtoList = (List<CourseDto>) objects[1];
        Integer studentAgoraUid = getAgoraUid(leadsUuid, STUDENT.name());
        List<PatriarchNoEndV1CourseListVo> voList = new ArrayList<>();
        PatriarchNoEndV1CourseListVo vo;
        for (CourseDto courseDto : courseDtoList) {
            vo = TransferUtil.transfer(courseDto, PatriarchNoEndV1CourseListVo.class);
            vo.setChannelStudentId(studentAgoraUid);
            vo.setChannelTeacherId(getAgoraUid(courseDto.getTeacherUuid(), TEACHER.name()));
            voList.add(vo);
        }
        Page<PatriarchNoEndV1CourseListVo> page = new Page<>(count, voList);
        return ResponseEntity.ok(Response.success(page));
    }

    /**
     * 家长查看学生课程记录（一对一）
     *
     * @param request
     * @return
     */
    @GetMapping("/getCourseRecordV1List")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000162, methodName = "getCourseRecordV1List", description = "家长查看学生课程记录（一对一）")
    public ResponseEntity<Response> getCourseRecordV1List(CourseRecordRequest request) throws ParseException {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userType = loginUserCache.getUserType();
        PatriarchLeads patriarch = patriarchLeadsService.findByPhone(loginUserCache.getPhone());
        if (patriarch == null) {
            PatriarchLeadsRecord patriarchLeadsRecord = patriarchLeadsRecordService.findByPhone(loginUserCache.getPhone());
            BingInfoVo bingInfoVo = new BingInfoVo();
            if (patriarchLeadsRecord != null) {
                String leadsUuid = patriarchLeadsRecord.getLeadsUuid();
                PatriarchLeads patriarchLeads = patriarchLeadsService.findByLeadsUuid(leadsUuid);
                Leads leads = leadsService.findLeadsByPhone(patriarchLeads.getLeadsPhone());
                bingInfoVo.setPhone(patriarchLeads.getPatriarchPhone());
                bingInfoVo.setChildrenName(leads.getName());
            }
            return ResponseEntity.ok(Response.error(NO_BING, bingInfoVo));
        }
        String leadsUuid = patriarch.getLeadsUuid();
        String subject = request.getSubject();
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();
        int pageNo = request.getPageNo();
        int pageSize = request.getPageSize();
        List<CourseDto> courseDtoTmpList = courseService.findStudentCourseRecordV1List(leadsUuid, subject, startDate, endDate, 1, Integer.MAX_VALUE);
        Object[] objects = getCourseDtoPageList(courseDtoTmpList, pageNo, pageSize, true, userType);
        int count = (int) objects[0];
        List<CourseDto> dtoList = (List<CourseDto>) objects[1];
        List<CourseListVo> voList = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        CourseListVo courseListVo;
        for (CourseDto courseDto : dtoList) {
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
            courseListVo.setClassAppraiseUuid(courseDto.getClassAppraiseUuid());
            courseListVo.setGrade(courseDto.getGrade());
            courseListVo.setCourseType(courseDto.getCourseType());
            voList.add(courseListVo);
        }
        Page<CourseListVo> page = new Page<>(count, voList);
        return ResponseEntity.ok(Response.success(page));
    }

    @PostMapping("registerByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000164, methodName = "registerByJson", description = "家长注册", checkToken = false)
    public ResponseEntity<Response> registerByJson(@RequestBody PatriarchRegisterRequest request) {
        return registerCom(request);
    }

    @PostMapping("register")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000165, methodName = "register", description = "家长注册", checkToken = false)
    public ResponseEntity<Response> register(PatriarchRegisterRequest request) {
        return registerCom(request);
    }

    /**
     * 家长注册
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> registerCom(PatriarchRegisterRequest request) {
        String phone = request.getPhone();
        String authCode = request.getAuthCode();
        String authCodecache = getAuthCode(phone);
        String name = request.getPatriarchName();
        String passWord = request.getPatriarchPassword();
        String grade = request.getChildrenGrade();

       if (StringUtils.isBlank(authCodecache)) {
            return ResponseEntity.ok(Response.error(AUTHCODE_TIMEOUT));
        }
        if (!authCode.equals(authCodecache)) {
            return ResponseEntity.ok(Response.error(AUTHCODE_ERROR));
        }
        Patriarch pat = patriarchService.findByphone(phone);
        if (pat != null) {
            return ResponseEntity.ok(Response.error(MOBILE_HAS_REGISTERED));
        }
        Patriarch patriarch = new Patriarch();
        patriarch.setPatriarchUuid(UUIDUtil.randomUUID2());
        patriarch.setPatriarchPhone(phone);
        patriarch.setPatriarchName(name);
        patriarch.setPatriarchPassword(passWord);
        patriarch.setChildrenGrade(grade);
        patriarchService.save(patriarch);
        return ResponseEntity.ok(Response.success());
    }


    /**
     * 解除绑定
     *
     * @param request
     * @return
     */
    @PostMapping("/unboundChildren")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000165, methodName = "unboundChildren", description = "解除绑定", userTypes = PATRIARCH)
    public ResponseEntity<Response> unboundChildren(BaseRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userUuid = loginUserCache.getUserUuid();
        patriarchLeadsService.deleteByPatriarchUuid(userUuid);
        return ResponseEntity.ok(Response.success());
    }
}
