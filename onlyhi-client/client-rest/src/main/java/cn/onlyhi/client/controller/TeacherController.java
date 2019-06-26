package cn.onlyhi.client.controller;

import cn.onlyhi.client.config.OssConfig;
import cn.onlyhi.client.dto.*;
import cn.onlyhi.client.po.ClassRoom;
import cn.onlyhi.client.po.ClassTeacherAppraise;
import cn.onlyhi.client.po.SysArea;
import cn.onlyhi.client.po.SysEnum;
import cn.onlyhi.client.po.TcPayGrade;
import cn.onlyhi.client.po.TcTablet;
import cn.onlyhi.client.po.TcTeacher;
import cn.onlyhi.client.po.TcTeacherDate;
import cn.onlyhi.client.po.TcTeacherFile;
import cn.onlyhi.client.po.TcTeacherSchool;
import cn.onlyhi.client.po.TeacherFreetimeRecord;
import cn.onlyhi.client.po.TeacherFreetimetemplate;
import cn.onlyhi.client.request.ApplyTabletRequest;
import cn.onlyhi.client.request.CourseDateListRequest;
import cn.onlyhi.client.request.CourseDateRequest;
import cn.onlyhi.client.request.CourseRecordRequest;
import cn.onlyhi.client.request.DateTimeListRequest;
import cn.onlyhi.client.request.DateTimeRequest;
import cn.onlyhi.client.request.SaveFreeTimeListRequest;
import cn.onlyhi.client.request.SaveFreeTimeRequest;
import cn.onlyhi.client.request.SaveTeacherAppraiseRequest;
import cn.onlyhi.client.request.SaveTeacherInfoRequest;
import cn.onlyhi.client.request.UploadRequest;
import cn.onlyhi.client.request.ViewTeacherAppraiseRequest;
import cn.onlyhi.client.service.ClassTeacherAppraiseService;
import cn.onlyhi.client.service.SysAreaService;
import cn.onlyhi.client.service.SysEnumService;
import cn.onlyhi.client.service.TcPayGradeService;
import cn.onlyhi.client.service.TcTabletService;
import cn.onlyhi.client.service.TcTeacherDateService;
import cn.onlyhi.client.service.TcTeacherFileService;
import cn.onlyhi.client.service.TcTeacherSchoolService;
import cn.onlyhi.client.service.TcTeacherService;
import cn.onlyhi.client.service.TeacherFreetimeRecordService;
import cn.onlyhi.client.service.TeacherFreetimetemplateService;
import cn.onlyhi.client.vo.*;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.enums.ClientEnum;
import cn.onlyhi.common.enums.CodeEnum;
import cn.onlyhi.common.request.BaseRequest;
import cn.onlyhi.common.util.*;
import com.alibaba.fastjson.JSON;
import com.aliyun.oss.model.ObjectMetadata;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Objects;

import static cn.onlyhi.common.constants.Constants.SYS_ENUM_TYPE_GRADE;
import static cn.onlyhi.common.constants.Constants.SYS_ENUM_TYPE_GRADE_PREFERENCE;
import static cn.onlyhi.common.enums.ClientEnum.UserType.TEACHER;
import static cn.onlyhi.common.enums.CodeEnum.COURSE_HAS_APPRAISE;
import static cn.onlyhi.common.enums.CodeEnum.NULL_FILE;
import static cn.onlyhi.common.util.ClientUtil.getTeacherUploadOssKey;
import static cn.onlyhi.common.util.ClientUtil.removeDuplicate;
import static cn.onlyhi.common.util.DateUtil.getDayOfWeekList;
import static cn.onlyhi.common.util.DateUtil.getWeek;
import static cn.onlyhi.common.util.DateUtil.getWeekOfMonth;
import static cn.onlyhi.common.util.DateUtil.validDateTime;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2018/1/11.
 */
@RestController
@RequestMapping("/teacher")
public class TeacherController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeacherController.class);

    @Autowired
    private TcTeacherService tcTeacherService;
    @Autowired
    private TcPayGradeService tcPayGradeService;
    @Autowired
    private TeacherFreetimetemplateService teacherFreetimetemplateService;
    @Autowired
    private TcTeacherDateService tcTeacherDateService;
    @Autowired
    private ClassTeacherAppraiseService classTeacherAppraiseService;
    @Autowired
    private OssConfig ossConfig;
    @Autowired
    private TcTeacherFileService tcTeacherFileService;
    @Autowired
    private TcTeacherSchoolService tcTeacherSchoolService;
    @Autowired
    private SysEnumService sysEnumService;
    @Autowired
    private TcTabletService tcTabletService;
    @Autowired
    private SysAreaService sysAreaService;
    @Autowired
    private TeacherFreetimeRecordService teacherFreetimeRecordService;
    @Value("${phpStaging.url}")
    private String url;
    /**
     * 教师基本信息
     * 调用php
     * @param request
     * @return
     */
    @GetMapping("/teacherInfo")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 1000061, methodName = "teacherInfoPhp", description = "教师基本信息", userTypes = TEACHER)
    public ResponseEntity<Response> teacherInfoPhp(BaseRequest request) {
        String phpUrl = url.concat("teacher/teacherInfo");
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        Response phpResponse =null;
        try{
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet post=new HttpGet(phpUrl);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            post.setHeader("Authorization", "Bearer "+loginUserCache.getToken());
            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity=response.getEntity();
            String result= EntityUtils.toString(entity,"UTF-8");
            if(result==null){
                LOGGER.info("teacher/teacherInfo 返回空");
            }

            phpResponse = JSON.parseObject(result, Response.class);
        }catch (Exception e){
            LOGGER.info("Exception={}", e.getMessage());
        }
        return ResponseEntity.ok(phpResponse);
    }
    /**
     * 教师基本信息
     *
     * @param request
     * @return
     */
    //@GetMapping("/teacherInfo")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 1000061, methodName = "teacherInfo", description = "教师基本信息", userTypes = TEACHER)
    public ResponseEntity<Response> teacherInfo(BaseRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String teacherUuid = loginUserCache.getUserUuid();
        TcTeacher tcTeacher = tcTeacherService.findByUuid(teacherUuid);
        String teacherName = tcTeacher.getTcName();
        Integer payGrade = tcTeacher.getPayGrade();
        String grade = null;
        if (payGrade != null) {
            TcPayGrade tcPayGrade = tcPayGradeService.findById(Long.valueOf(payGrade));
            grade = tcPayGrade.getGrade();
        }

        String monthDate = DateFormatUtils.format(new Date(), "yyyy-MM");
        //本月所上课时
        double classTime = cpCourseService.findClassTimeByTeacherUuidAndMonthDate(teacherUuid, monthDate);
        //本月所有有排课的教师总数
        int courseCount = cpCourseService.countCourseTeacherByMonthDate(monthDate);
        //本月小于已上课时的教师数
        int lessClassTimeCount = cpCourseService.countLessClassTimeTeacherByMonthDate(classTime, monthDate);
        //本月消耗课时超过其他人比率
        String surpassRatio = "0%";
        if (courseCount > 0) {
            surpassRatio = new BigDecimal(lessClassTimeCount * 100 / courseCount).setScale(0, BigDecimal.ROUND_HALF_UP).toString() + "%";
        }
        //头像
        TcTeacherFile teacherFile = tcTeacherFileService.findByTeacherIdAndPurpose(tcTeacher.getId(), 1);
        TeacherInfoVo vo = new TeacherInfoVo();
        vo.setName(teacherName);
        vo.setHour(classTime);
        vo.setSurpassRatio(surpassRatio);
        vo.setGrade(grade);
        if (teacherFile != null) {
            OSSUtil ossUtil = new OSSUtil(ossConfig.getDomain(), ossConfig.getBucketName());
            vo.setPhoto(ossUtil.generatePresignedUrl(teacherFile.getOssKey()));
        }
        return ResponseEntity.ok(Response.success(vo));
    }

    /**
     * 服务器时间
     *
     * @param request
     * @return
     */
    @GetMapping("/serverTime")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 1000062, methodName = "serverTime", description = "服务器时间", userTypes = TEACHER)
    public ResponseEntity<Response> serverTime(BaseRequest request) {
        ServerTimeVo vo = new ServerTimeVo();
        vo.setServerTime(System.currentTimeMillis());
        return ResponseEntity.ok(Response.success(vo));
    }

    /**
     * 课程日历
     *
     * @param request
     * @return
     */
    //@GetMapping("/courseCalendar")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 1000063, methodName = "courseCalendar", description = "课程日历", userTypes = TEACHER)
    public ResponseEntity<Response> courseCalendar(DateTimeRequest request) throws ParseException {
        String dateTime = request.getDateTime();
        String[] dateTimes = dateTime.split("-");
        String yearStr = dateTimes[0];
        String monthStr = dateTimes[1];
        int year = Integer.parseInt(yearStr);
        int month = Integer.parseInt(monthStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);
        int days = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int weekStart = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        List<CourseCalendarVo> voList = new ArrayList<>();
        CourseCalendarVo vo = new CourseCalendarVo();
        if (weekStart != 1) {   //第一天不是星期一
            int lastDays = 31;
            if (month != 1) {
                calendar.set(Calendar.MONTH, month - 2);
                calendar.set(Calendar.DATE, 1);
                calendar.roll(Calendar.DATE, -1);
                lastDays = calendar.get(Calendar.DATE);
            }
            if (weekStart == 0) {   //周日（国际）
                weekStart = 7;  //改为（需求）
            }
            for (int i = weekStart - 1 - 1; i >= 0; i--) {
                vo = new CourseCalendarVo();
                vo.setDateDay(lastDays - i);
                vo.setDateStatus(0);
                voList.add(vo);
            }
        }
        String teacherUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        List<String> courseDateList = cpCourseService.findCourseDateByTeacherUuidAndYearMonth(teacherUuid, dateTime);
        String todayStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        for (int i = 1; i <= days; i++) {
            vo = new CourseCalendarVo();
            vo.setDateDay(i);
            String date = "";
            if (i < 10) {
                date = dateTime + "-0" + i;
            } else {
                date = dateTime + "-" + i;
            }
            if (courseDateList.contains(date)) {
                vo.setDateStatus(2);
            } else {
                vo.setDateStatus(1);
            }
            if (todayStr.equals(date)) {
                vo.setIsToday(true);
            }
            voList.add(vo);
        }
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, days);
        int weekEnd = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekEnd != 0) { //最后一天不是星期日
            for (int i = 0; i < 6 - weekEnd + 1; i++) {
                vo = new CourseCalendarVo();
                vo.setDateDay(i + 1);
                vo.setDateStatus(0);
                voList.add(vo);
            }
        }
        String lastDateTime = dateTime + "-" + days;
        int weekOfMonth = getWeekOfMonth(lastDateTime);
        if (weekOfMonth == 5) {
            CourseCalendarVo courseCalendarVo = voList.get(voList.size() - 1);
            int dateDay = courseCalendarVo.getDateDay();
            if (weekEnd == 0) {    //第五周最后一日是周日，下周时间从1号开始
                dateDay = 0;
            }
            for (int i = 0; i < 7; i++) {
                vo = new CourseCalendarVo();
                vo.setDateDay(dateDay + i + 1);
                vo.setDateStatus(0);
                voList.add(vo);
            }

        }
        List<List<CourseCalendarVo>> voLists = new ArrayList<>();
        List<CourseCalendarVo> vos = new ArrayList<>();
        for (int i = 0; i < 6; i++) {   //一个月六周
            vos = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
                CourseCalendarVo courseCalendarVo = voList.get(7 * i + j);
                vos.add(courseCalendarVo);
            }
            voLists.add(vos);
        }
        return ResponseEntity.ok(Response.success(voLists));
    }
    /**
     * 课程日历
     * 调用php
     * @param request
     * @return
     */
    @GetMapping("/courseCalendar")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 1000063, methodName = "courseCalendarPhp", description = "课程日历", userTypes = TEACHER)
    public ResponseEntity<Response> courseCalendarPhp(DateTimeRequest request) throws ParseException {
        String dateTime = request.getDateTime();
        String[] dateTimes = dateTime.split("-");
        String yearStr = dateTimes[0];
        String monthStr = dateTimes[1];
        int year = Integer.parseInt(yearStr);
        int month = Integer.parseInt(monthStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);
        int days = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int weekStart = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        List<CourseCalendarVo> voList = new ArrayList<>();
        CourseCalendarVo vo = new CourseCalendarVo();
        if (weekStart != 1) {   //第一天不是星期一
            int lastDays = 31;
            if (month != 1) {
                calendar.set(Calendar.MONTH, month - 2);
                calendar.set(Calendar.DATE, 1);
                calendar.roll(Calendar.DATE, -1);
                lastDays = calendar.get(Calendar.DATE);
            }
            if (weekStart == 0) {   //周日（国际）
                weekStart = 7;  //改为（需求）
            }
            for (int i = weekStart - 1 - 1; i >= 0; i--) {
                vo = new CourseCalendarVo();
                vo.setDateDay(lastDays - i);
                vo.setDateStatus(0);
                voList.add(vo);
            }
        }
        List<String> courseDateList=null;
        try {
            LoginUserCache loginUserCache=redisService.getLoginUserCache(request.getToken());
            String phpUserUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
            String phpUrl = url.concat("teacher/courseCalendar");
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("dateTime", dateTime);
            paramMap.put("teacherUuid", phpUserUuid);
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer ".concat(loginUserCache.getToken()));
            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            //LOGGER.info("courseCalendarPhp phpResult {}", phpResult);
            Response phpResponse = JSON.parseObject(phpResult, Response.class);
            Object object=phpResponse.getData();
            if(object!=null){
                courseDateList=JSON.parseArray(object.toString(),String.class);
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        String todayStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        for (int i = 1; i <= days; i++) {
            vo = new CourseCalendarVo();
            vo.setDateDay(i);
            String date = "";
            if (i < 10) {
                date = dateTime + "-0" + i;
            } else {
                date = dateTime + "-" + i;
            }
            if (courseDateList.contains(date)) {
                vo.setDateStatus(2);
            } else {
                vo.setDateStatus(1);
            }
            if (todayStr.equals(date)) {
                vo.setIsToday(true);
            }
            voList.add(vo);
        }
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, days);
        int weekEnd = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekEnd != 0) { //最后一天不是星期日
            for (int i = 0; i < 6 - weekEnd + 1; i++) {
                vo = new CourseCalendarVo();
                vo.setDateDay(i + 1);
                vo.setDateStatus(0);
                voList.add(vo);
            }
        }
        String lastDateTime = dateTime + "-" + days;
        int weekOfMonth = getWeekOfMonth(lastDateTime);
        if (weekOfMonth == 5) {
            CourseCalendarVo courseCalendarVo = voList.get(voList.size() - 1);
            int dateDay = courseCalendarVo.getDateDay();
            if (weekEnd == 0) {    //第五周最后一日是周日，下周时间从1号开始
                dateDay = 0;
            }
            for (int i = 0; i < 7; i++) {
                vo = new CourseCalendarVo();
                vo.setDateDay(dateDay + i + 1);
                vo.setDateStatus(0);
                voList.add(vo);
            }

        }
        List<List<CourseCalendarVo>> voLists = new ArrayList<>();
        List<CourseCalendarVo> vos = new ArrayList<>();
        for (int i = 0; i < 6; i++) {   //一个月六周
            vos = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
                CourseCalendarVo courseCalendarVo = voList.get(7 * i + j);
                vos.add(courseCalendarVo);
            }
            voLists.add(vos);
        }
        return ResponseEntity.ok(Response.success(voLists));
    }
    /**
     * 今天待上课列表
     *
     * @param request
     * @return
     */
    //@GetMapping("/todayNoEndCourseList")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 1000064, methodName = "todayNoEndCourseList", description = "今天待上课列表", userTypes = TEACHER)
    public ResponseEntity<Response> todayNoEndCourseList(BaseRequest request) throws ParseException {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String teacherUuid = loginUserCache.getUserUuid();
        String userType = loginUserCache.getUserType();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String courseDate = dateFormat.format(new Date());
        List<CourseDto> courseDtoTmpList = cpCourseService.findStudentNoEndCourseListByTeacherUuidAndCourseDate(teacherUuid, courseDate);
        Object[] objects = getCourseDtoPageList(courseDtoTmpList, 1, Integer.MAX_VALUE, false, userType);
        List<CourseDto> courseDtoList = (List<CourseDto>) objects[1];
        List<TeacherCourseRemindVo> voList = TransferUtil.transfer(courseDtoList, TeacherCourseRemindVo.class);
        Page<TeacherCourseRemindVo> page = new Page<>(voList.size(), voList);
        return ResponseEntity.ok(Response.success(page));
    }
    /**
     * 今天待上课列表
     * 调用php
     * @param request
     * @return
     */
    @GetMapping("/todayNoEndCourseList")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 1000064, methodName = "todayNoEndCourseListPhp", description = "今天待上课列表", userTypes = TEACHER)
    public ResponseEntity<Response> todayNoEndCourseListPhp(BaseRequest request) throws ParseException {
        Page<TeacherCourseRemindVo> page =null;
        try{
            LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
            String phpUrl = url.concat("teacher/todayNoEndCourseList");
            Map<String, String> headerMap = new HashMap<>();
            Map<String, String> paramMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer ".concat(loginUserCache.getToken()));
            headerMap.put("Accept","application/json");

            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            if(phpResult==null){
                LOGGER.info("老师今天待上课列表返回空}");
            }

            Response phpResponse = JSON.parseObject(phpResult, Response.class);

            Object object=phpResponse.getData();
            List<CourseDto> courseDtoTmpList =  JSON.parseArray(object.toString(),CourseDto.class);
            Object[] objects = getCourseDtoPageList(courseDtoTmpList, 1, Integer.MAX_VALUE, false, "TEACHER");
            List<CourseDto> courseDtoList = (List<CourseDto>) objects[1];
            List<TeacherCourseRemindVo> voList = TransferUtil.transfer(courseDtoList, TeacherCourseRemindVo.class);
            page = new Page<>(voList.size(), voList);

        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(Response.success(page));
    }
    /**
     * 根据日期获取课程列表
     *
     * @param request
     * @return
     */
    //@GetMapping("/courseList")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 1000065, methodName = "courseList", description = "根据日期获取课程列表", userTypes = TEACHER)
    public ResponseEntity<Response> courseList(CourseDateRequest request) throws ParseException {
        String teacherUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        String courseDateStr = request.getCourseDate();
        List<CourseDto> courseDtoList = cpCourseService.findByByTeacherUuidAndCourseDate(teacherUuid, courseDateStr);
        List<TeacherCourseRemindVo> voList = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        TeacherCourseRemindVo vo;
        for (CourseDto dto : courseDtoList) {
            String courseUuid = dto.getCourseUuid();
            String courseDate = dto.getCourseDate();
            String startTime = dto.getStartTime();
            String endTime = dto.getEndTime();
            int courseType = dto.getCourseType();
            int classStatus = dto.getClassStatus();
            String studentName = dto.getStudentName();
            if (Objects.equals(classStatus, 1)) {
                ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
                if (classRoom != null) {
                    Long outRoomTime = classRoom.getOutRoomTime();
                    if (outRoomTime != null) {
                        if (outRoomTime < dateFormat.parse(courseDate + " " + endTime).getTime()) {
                            endTime = dateFormat.format(outRoomTime).substring(11);
                        }
                    }
                }
            }
            vo = new TeacherCourseRemindVo();
            vo.setCourseDate(courseDate);
            vo.setStartTime(startTime);
            vo.setCourseUuid(courseUuid);
            vo.setEndTime(endTime);
            vo.setSubject(dto.getSubject());
            vo.setCourseType(courseType);
            vo.setStudentName(studentName);
            vo.setGrade(dto.getGrade());
            voList.add(vo);
        }
        Page<TeacherCourseRemindVo> page = new Page<>(voList.size(), voList);
        return ResponseEntity.ok(Response.success(page));
    }
    /**
     * 根据日期获取课程列表
     * 调用php
     * @param request
     * @return
     */
    @GetMapping("/courseList")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 1000065, methodName = "courseListPhp", description = "根据日期获取课程列表", userTypes = TEACHER)
    public ResponseEntity<Response> courseListPhp(CourseDateRequest request) throws ParseException {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String phpUrl = url.concat("teacher/courseList");
        String courseDate=request.getCourseDate();
        Response phpResponse =null;
        try {
            Map<String, String> headerMap = new HashMap<>();
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("courseDate",courseDate);
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer ".concat(loginUserCache.getToken()));
            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            if(phpResult==null){
                LOGGER.info("根据日期获取课程列表 返回空");
            }
            phpResponse = JSON.parseObject(phpResult, Response.class);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }

        return ResponseEntity.ok(phpResponse);
    }
    /**
     * 指定年月空闲时间列表
     *
     * @param request
     * @return
     */
    @GetMapping("/freeTimeList")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 1000066, methodName = "freeTimeList", description = "指定年月空闲时间列表", userTypes = TEACHER)
    public ResponseEntity<Response> freeTimeList(DateTimeListRequest request) {
        //检验参数
        List<String> dateTimeList = request.getDateTimeList();
        if (!validDateTime(dateTimeList)) {
            return ResponseEntity.ok(Response.errorCustom("dateTime格式错误，正确格式：yyyy-MM"));
        }
        String teacherUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        FreeTimeMonthListVo vo = new FreeTimeMonthListVo();
        List<FreeTimeListListVo> listVoList = new ArrayList<>();
        if (dateTimeList != null) {
            FreeTimeListListVo listVo = new FreeTimeListListVo();
            for (String dateTime : dateTimeList) {
                String[] dateTimes = dateTime.split("-");
                String yearStr = dateTimes[0];
                String monthStr = dateTimes[1];
                int year = Integer.parseInt(yearStr);
                int month = Integer.parseInt(monthStr);
                List<List<FreeTimeVo>> voLists = new ArrayList<>();
                for (int i = 1; i <= 6; i++) {  //一个月六周
                    int weekOfMonth = i;
                    List<FreeTimeVo> freeTimeVoList = getFreeTimeListOfWeek(year, month, weekOfMonth, teacherUuid);
                    voLists.add(freeTimeVoList);
                }
                listVo = new FreeTimeListListVo();
                listVo.setFreeDateTime(dateTime);
                listVo.setFreeTimeListList(voLists);

                listVoList.add(listVo);
            }
            //是否提交过
            TeacherFreetimeRecord teacherFreetimeRecord = teacherFreetimeRecordService.findByTeacherUuidAndFreetimeMonth(teacherUuid, dateTimeList.get(0));
            if (teacherFreetimeRecord != null) {
                vo.setIsCommit(true);
            }
        }
        vo.setFreeTimeMonthList(listVoList);

        return ResponseEntity.ok(Response.success(vo));
    }

    /**
     * 某年某月某周教师空闲时间列表
     *
     * @param year
     * @param month
     * @param weekOfMonth
     * @param teacherUuid
     * @return
     */
    private List<FreeTimeVo> getFreeTimeListOfWeek(int year, int month, int weekOfMonth, String teacherUuid) {
        List<String> dayListOfWeek = getDayOfWeekList(year, month, weekOfMonth);
        List<FreeTimeVo> voList = new ArrayList<>();
        FreeTimeVo vo = new FreeTimeVo();
        for (String day : dayListOfWeek) {
            vo = new FreeTimeVo();
            List<String> freetimePeriodList = tcTeacherDateService.findFreetimePeriodByTeacherUuidAndFreetimeDate(teacherUuid, day);
            vo.setFreetimeDate(day);
            vo.setFreetimePeriodList(freetimePeriodList);
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 保存空闲时间
     *
     * @param request
     * @return
     */
    @PostMapping("/saveFreeTime")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 1000067, methodName = "saveFreeTime", description = "保存空闲时间", userTypes = TEACHER)
    public ResponseEntity<Response> saveFreeTime(@RequestBody SaveFreeTimeListRequest request) throws ParseException {
        List<String> freeDateTimeList = request.getFreeDateTimeList();
        if (freeDateTimeList != null) {
            if (!validDateTime(freeDateTimeList)) {
                return ResponseEntity.ok(Response.errorCustom("freeDateTimeList集合元素格式错误，正确格式：yyyy-MM"));
            }
        }
        String teacherUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        List<FreeTimeListVo> freeTimeListVoList = getFreeTimeListVoList(request);
        for (FreeTimeListVo vo : freeTimeListVoList) {
            String freeDateTime = vo.getFreeDateTime();
            List<FreeTimeVo> freeTimeList = vo.getFreeTimeList();
            List<TcTeacherDate> tcTeacherDateList = new ArrayList<>();
            int size = freeTimeList.size();
            for (int i = 0; i < size; i++) {
                FreeTimeVo freeTime = freeTimeList.get(i);
                String freetimeDate = freeTime.getFreetimeDate();
                int weekOfMonth = getWeekOfMonth(freetimeDate);
                List<String> freetimePeriodList = freeTime.getFreetimePeriodList();
                TcTeacherDate tcTeacherDate = new TcTeacherDate();
                for (String freetimePeriod : freetimePeriodList) {
                    String[] freetimes = freetimePeriod.split("-");
                    if (freetimes.length != 2) {
                        LOGGER.info("freetimes={}", Arrays.toString(freetimes));
                    }
                    String startTime = freetimes[0];
                    String endTime = freetimes[1];
                    tcTeacherDate = new TcTeacherDate();
                    tcTeacherDate.setTeacherUuid(teacherUuid);
                    tcTeacherDate.setTcDate(freetimeDate);
                    tcTeacherDate.setTcTime(startTime);
                    tcTeacherDate.setCreateUserId(teacherUuid);
                    tcTeacherDate.setFreetimeMonth(freeDateTime);
                    tcTeacherDate.setWeekOfMonth(weekOfMonth);
                    tcTeacherDate.setEndTime(endTime);
                    tcTeacherDateList.add(tcTeacherDate);
                }
            }
            if (tcTeacherDateList.size() > 0) {
                //批量保存
                tcTeacherDateService.deleteAndSave(teacherUuid, freeDateTime, tcTeacherDateList);
            }
        }
        //保存记录保存（未保存过）
        if (freeDateTimeList != null && freeDateTimeList.size() > 0) {
            for (String freetimeMonth : freeDateTimeList) {
                TeacherFreetimeRecord teacherFreetimeRecord = teacherFreetimeRecordService.findByTeacherUuidAndFreetimeMonth(teacherUuid, freetimeMonth);
                if (teacherFreetimeRecord == null) {
                    teacherFreetimeRecord = new TeacherFreetimeRecord();
                    teacherFreetimeRecord.setTeacherUuid(teacherUuid);
                    teacherFreetimeRecord.setFreetimeMonth(freetimeMonth);
                    teacherFreetimeRecordService.save(teacherFreetimeRecord);
                }
            }
        }
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 课表
     *
     * @param request
     * @return
     */
    @GetMapping("/courseSchedule")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 1000068, methodName = "courseSchedule", description = "课表", userTypes = TEACHER)
    public ResponseEntity<Response> courseSchedule(DateTimeRequest request) throws ParseException {
        String teacherUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        String dateTime = request.getDateTime();
        String[] dateTimes = dateTime.split("-");
        String yearStr = dateTimes[0];
        String monthStr = dateTimes[1];
        int year = Integer.parseInt(yearStr);
        int month = Integer.parseInt(monthStr);
        List<List<CourseScheduleVo>> voLists = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {  //一个月六周
            int weekOfMonth = i;
            List<CourseScheduleVo> courseScheduleVoList = getCourseScheduleOfWeek(year, month, weekOfMonth, teacherUuid);
            voLists.add(courseScheduleVoList);
        }
        return ResponseEntity.ok(Response.success(voLists));
    }

    private List<CourseScheduleVo> getCourseScheduleOfWeek(int year, int month, int weekOfMonth, String teacherUuid) throws ParseException {
        List<String> dayListOfWeek = getDayOfWeekList(year, month, weekOfMonth);
        List<CourseScheduleVo> voList = new ArrayList<>();
        CourseScheduleVo vo = new CourseScheduleVo();
        for (String day : dayListOfWeek) {
            vo = new CourseScheduleVo();
            List<CourseDto> courseDtoList = cpCourseService.findByByTeacherUuidAndCourseDate2(teacherUuid, day);
            List<TeacherCourseRemindVo> teacherCourseRemindVoList = TransferUtil.transfer(courseDtoList, TeacherCourseRemindVo.class);

            vo.setCourseDate(day);
            vo.setCourseScheduleList(teacherCourseRemindVoList);
            voList.add(vo);
        }
        return voList;
    }

    private List<FreeTimeListVo> getFreeTimeListVoList(SaveFreeTimeListRequest request) {
        List<SaveFreeTimeRequest> freeTimeSaveList = request.getFreeTimeSaveList();
        List<FreeTimeListVo> freeTimeListVoList = new ArrayList<>();
        FreeTimeListVo freeTimeListVo = new FreeTimeListVo();
        for (SaveFreeTimeRequest saveFreeTimeRequest : freeTimeSaveList) {
            freeTimeListVo = new FreeTimeListVo();
            String freeDateTime = saveFreeTimeRequest.getFreeDateTime();    //年月
            List<List<FreeTimeVo>> freeTimeListList = saveFreeTimeRequest.getFreeTimeListList();
            List<FreeTimeVo> voList = new ArrayList<>();
            for (List<FreeTimeVo> freeTimeVoList : freeTimeListList) {
                for (FreeTimeVo freeTimeVo : freeTimeVoList) {
                    String freetimeDate = freeTimeVo.getFreetimeDate(); //年月日
                    if (freeDateTime.equals(freetimeDate.substring(0, freetimeDate.length() - 3))) {
                        voList.add(freeTimeVo);
                    }
                }
            }
            freeTimeListVo.setFreeDateTime(freeDateTime);
            freeTimeListVo.setFreeTimeList(voList);

            freeTimeListVoList.add(freeTimeListVo);
        }
        return freeTimeListVoList;
    }

    /**
     * 保存空闲时间模板
     *
     * @param request
     * @return
     */
    @PostMapping("/saveFreeTimeTemplate")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 1000068, methodName = "saveFreeTimeTemplate", description = "保存空闲时间模板", userTypes = TEACHER)
    public ResponseEntity<Response> saveFreeTimeTemplate(@RequestBody SaveFreeTimeListRequest request) throws ParseException {
        String teacherUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        List<FreeTimeListVo> freeTimeListVoList = getFreeTimeListVoList(request);
        if (freeTimeListVoList.size() != 1) {
            return ResponseEntity.ok(Response.errorCustom("模板数据非法！"));
        }
        for (FreeTimeListVo vo : freeTimeListVoList) {
            List<FreeTimeVo> freeTimeList = vo.getFreeTimeList();
            List<TeacherFreetimetemplate> teacherFreetimetemplateList = new ArrayList<>();
            int size = freeTimeList.size();
            for (int i = 0; i < size; i++) {
                FreeTimeVo freeTime = freeTimeList.get(i);
                String freetimeDate = freeTime.getFreetimeDate();
                int weekOfMonth = getWeekOfMonth(freetimeDate);
                int week = getWeek(freetimeDate);
                List<String> freetimePeriodList = freeTime.getFreetimePeriodList();
                TeacherFreetimetemplate teacherFreetimetemplate = new TeacherFreetimetemplate();
                for (String freetimePeriod : freetimePeriodList) {
                    String[] freetimes = freetimePeriod.split("-");
                    if (freetimes.length != 2) {
                        LOGGER.info("freetimes={}", Arrays.toString(freetimes));
                    }
                    String startTime = freetimes[0];
                    String endTime = freetimes[1];
                    teacherFreetimetemplate = new TeacherFreetimetemplate();
                    teacherFreetimetemplate.setTeacherFreetimetemplateUuid(UUIDUtil.randomUUID2());
                    teacherFreetimetemplate.setTeacherUuid(teacherUuid);
                    teacherFreetimetemplate.setWeekOfMonth(weekOfMonth);
                    teacherFreetimetemplate.setWeek(week);
                    teacherFreetimetemplate.setStartTime(startTime);
                    teacherFreetimetemplate.setEndTime(endTime);
                    teacherFreetimetemplate.setCreateUid(teacherUuid);
                    teacherFreetimetemplateList.add(teacherFreetimetemplate);
                }
            }
            //删除并批量保存
            teacherFreetimetemplateService.deleteAndSave(teacherUuid, teacherFreetimetemplateList);
        }


        return ResponseEntity.ok(Response.success());
    }

    @GetMapping("/getFreeTimeTemplate")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 1000069, methodName = "getFreeTimeTemplate", description = "获取空闲时间模板", userTypes = TEACHER)
    public ResponseEntity<Response> getFreeTimeTemplate(DateTimeListRequest request) {
        //检验参数
        List<String> dateTimeList = request.getDateTimeList();
        if (!validDateTime(dateTimeList)) {
            return ResponseEntity.ok(Response.errorCustom("dateTime格式错误，正确格式：yyyy-MM"));
        }
        String teacherUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        List<FreeTimeListListVo> listVoList = new ArrayList<>();
        if (dateTimeList != null) {
            FreeTimeListListVo listVo = new FreeTimeListListVo();
            for (String dateTime : dateTimeList) {
                String[] dateTimes = dateTime.split("-");
                String yearStr = dateTimes[0];
                String monthStr = dateTimes[1];
                int year = Integer.parseInt(yearStr);
                int month = Integer.parseInt(monthStr);
                List<List<FreeTimeVo>> voLists = new ArrayList<>();
                for (int i = 1; i <= 6; i++) {  //一个月六周
                    int week = i;
                    List<FreeTimeVo> freeTimeVoList = getFreeTimeTemplateListOfWeek(year, month, week, teacherUuid);
                    voLists.add(freeTimeVoList);
                }
                listVo = new FreeTimeListListVo();
                listVo.setFreeDateTime(dateTime);
                listVo.setFreeTimeListList(voLists);

                listVoList.add(listVo);
            }
        }
        FreeTimeMonthListVo vo = new FreeTimeMonthListVo();
        vo.setFreeTimeMonthList(listVoList);
        return ResponseEntity.ok(Response.success(vo));
    }

    private List<FreeTimeVo> getFreeTimeTemplateListOfWeek(int year, int month, int weekOfMonth, String teacherUuid) {
        List<FreeTimeVo> voList = new ArrayList<>();
        FreeTimeVo vo = new FreeTimeVo();
        List<TeacherFreetimetemplate> teacherFreetimetemplateList = teacherFreetimetemplateService.findByTeacherUuidAndWeekOfMonth(teacherUuid, weekOfMonth);
        //weekMap:  key:星期几 value:空闲开始时间集合
        Map<Integer, List<String>> weekMap = new HashMap<>();
        for (TeacherFreetimetemplate teacherFreetimetemplate : teacherFreetimetemplateList) {
            Integer week = teacherFreetimetemplate.getWeek();
            String startTime = teacherFreetimetemplate.getStartTime();
            List<String> startTimeList = weekMap.get(week);
            if (startTimeList == null) {
                startTimeList = new ArrayList<>();
                startTimeList.add(startTime);
                weekMap.put(week, startTimeList);
            } else {
                startTimeList.add(startTime);
            }
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        //设置一周的第一天为星期一
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.WEEK_OF_MONTH, weekOfMonth);
        Set<Map.Entry<Integer, List<String>>> entrySet = weekMap.entrySet();
        //freeTimeDateMap key:空闲日期 values:空闲开始时间集合
        Map<String, List<String>> freeTimeDateMap = new HashMap<>();
        for (Map.Entry<Integer, List<String>> entry : entrySet) {
            Integer week = entry.getKey();
            List<String> startTimeList = entry.getValue();
            calendar.set(Calendar.DAY_OF_WEEK, week);
            String freeTimeDate = dateFormat.format(calendar.getTime());
            freeTimeDateMap.put(freeTimeDate, startTimeList);
        }
        List<String> dayOfWeekList = getDayOfWeekList(year, month, weekOfMonth);
        for (String dayOfWeek : dayOfWeekList) {
            vo = new FreeTimeVo();
            vo.setFreetimeDate(dayOfWeek);
            vo.setFreetimePeriodList(freeTimeDateMap.getOrDefault(dayOfWeek, new ArrayList<>()));
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 获取指定日期的待上课列表
     *
     * @param request
     * @return
     */
    //@PostMapping("/getNoEndCourseList")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 10000610, methodName = "getNoEndCourseList", description = "获取指定日期的待上课列表", userTypes = TEACHER)
    public ResponseEntity<Response> getNoEndCourseList(@RequestBody CourseDateListRequest request) throws ParseException {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userType = loginUserCache.getUserType();
        String teacherUuid = loginUserCache.getUserUuid();
        List<String> courseDateList = request.getCourseDateList();
        int pageNo = 1;
        int pageSize = Integer.MAX_VALUE;
        List<Page<TeacherNoEndCourseVo>> pageList = new ArrayList<>();
        for (String courseDate : courseDateList) {
            List<CourseDto> courseDtoTmpList = cpCourseService.findNoEndCourseListByTeacherUuidAndCourseDate(teacherUuid, courseDate, 1, Integer.MAX_VALUE);
            Object[] objects = getCourseDtoPageList(courseDtoTmpList, pageNo, pageSize, false, userType);
            int total = (int) objects[0];
            List<CourseDto> courseDtoList = (List<CourseDto>) objects[1];
            List<TeacherNoEndCourseVo> voList = TransferUtil.transfer(courseDtoList, TeacherNoEndCourseVo.class);
            Page<TeacherNoEndCourseVo> page = new Page<>(total, voList);
            pageList.add(page);
        }
        return ResponseEntity.ok(Response.success(pageList));
    }

    /**
     * 获取指定日期的待上课列表(只显示近期三天有课的课程)
     * 调用php
     * @param request
     * @return
     */

    @PostMapping("/getNoEndCourseList")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 10000610, methodName = "getNoEndCourseListPhp", description = "获取指定日期的待上课列表", userTypes = TEACHER)
    public ResponseEntity<Response> getNoEndCourseListPhp(@RequestBody CourseDateListRequest request) throws ParseException {
        Response phpResponse =null;
        List<Page<TeacherNoEndCourseVo>> pageList = new ArrayList<>();
        try{
            List<CourseDto> courseDtoTmpList =new ArrayList<>();
            LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
            String userType = loginUserCache.getUserType();
            String phpUrl = url.concat("teacher/getNoEndCourseList");
            List<String> courseDateList=request.getCourseDateList();

            Map<String, String> headerMap = new HashMap<>();
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("courseDateList",JSON.toJSONString(courseDateList));
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer ".concat(loginUserCache.getToken()));

            LOGGER.info("teacher/getNoEndCourseList 传给php={}",paramMap);

            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            if(phpResult==null){
                LOGGER.info("只显示近期三天有课的课程 php 返回空");
            }
            phpResponse = JSON.parseObject(phpResult, Response.class);
            Object object=phpResponse.getData();
            courseDtoTmpList=JSON.parseArray(object.toString(),CourseDto.class);

            int pageNo = 1;
            int pageSize = Integer.MAX_VALUE;

            Object[] objects = getCourseDtoPageList(courseDtoTmpList, pageNo, pageSize, false, userType);
            int total = (int) objects[0];
            if(objects.length>0){
                List<CourseDto> courseDtoList = (List<CourseDto>) objects[1];
                List<TeacherNoEndCourseVo> voList = TransferUtil.transfer(courseDtoList, TeacherNoEndCourseVo.class);
                Page<TeacherNoEndCourseVo> page = new Page<>(total, voList);
                pageList.add(page);
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(Response.success(pageList));
    }
    /**
     * 老师对学生进行评价
     *
     * @param request
     * @return
     */
    //@PostMapping("/saveTeacherAppraise")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 10000611, methodName = "saveTeacherAppraise", description = "保存老师对学生的评价", userTypes = {ClientEnum.UserType.STUDENT, ClientEnum.UserType.TEACHER})
    public ResponseEntity<Response> saveTeacherAppraise(SaveTeacherAppraiseRequest request) {
        String appraiseContent = request.getAppraiseContent();
        //获取课程uuid
        String courseUuid = request.getCourseUuid();
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userUuid = loginUserCache.getUserUuid();

        //查看是否含有这节课的评价
        ClassTeacherAppraise teacherAppraise = classTeacherAppraiseService.findByCourseUuid(courseUuid);
        if (teacherAppraise != null) {
            return ResponseEntity.ok(Response.error(COURSE_HAS_APPRAISE));
        }
        ClassTeacherAppraise classTeacherAppraise = new ClassTeacherAppraise();
        classTeacherAppraise.setCourseUuid(courseUuid);
        classTeacherAppraise.setClassTeacherAppraiseUuid(UUIDUtil.randomUUID2());
        classTeacherAppraise.setAppraiserUuid(userUuid);
        classTeacherAppraise.setAppraiseContent(appraiseContent);
        classTeacherAppraiseService.save(classTeacherAppraise);
        return ResponseEntity.ok(Response.success());
    }
    /**
     * 老师对学生进行评价
     * 调用php
     * @param request
     * @return
     */
    @PostMapping("/saveTeacherAppraise")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 10000611, methodName = "saveTeacherAppraisePhp", description = "保存老师对学生的评价", userTypes = {ClientEnum.UserType.STUDENT, ClientEnum.UserType.TEACHER})
    public ResponseEntity<Response> saveTeacherAppraisePhp(SaveTeacherAppraiseRequest request) {
        Response phpResponse =null;
        try{
            String appraiseContent = request.getAppraiseContent();
            //获取课程uuid
            String courseUuid = request.getCourseUuid();
            LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
            //登录时设置用户唯一id
            String userUuid = loginUserCache.getUserUuid();

            String phpUrl = url.concat("teacher/saveTeacherAppraise");
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("courseUuid", courseUuid);
            paramMap.put("classTeacherAppraiseUuid", UUIDUtil.randomUUID2());
            paramMap.put("appraiserUuid", userUuid);
            paramMap.put("appraiseContent", appraiseContent);
            LOGGER.info("teacher/saveTeacherAppraise传给php={}",JSON.toJSONString(paramMap));

            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer ".concat(loginUserCache.getToken()));
            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            LOGGER.info("老师对学生进行评价={}",phpResult);
            phpResponse = JSON.parseObject(phpResult, Response.class);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(phpResponse);
    }

    /**
     * 查看老师的反馈内容
     *
     * @param teacherRequest
     * @return
     */
    //@GetMapping("/viewTeacherAppraise")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 10000612, methodName = "viewTeacherAppraise", description = "查看教师评价", userTypes = {ClientEnum.UserType.STUDENT, ClientEnum.UserType.TEACHER})
    public ResponseEntity<Response> viewTeacherAppraise(ViewTeacherAppraiseRequest teacherRequest) {
        ViewTeacherAppraiseVo vo = new ViewTeacherAppraiseVo();
        //评价详情
        ClassTeacherAppraise classTeacherAppraise = classTeacherAppraiseService.findByUuid(teacherRequest.getClassTeacherAppraiseUuid());
        if (classTeacherAppraise == null) {
            return ResponseEntity.ok(Response.errorCustom("无此评价！"));
        }
        vo.setAppraiseContent(classTeacherAppraise.getAppraiseContent());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = sdf.format(classTeacherAppraise.getCreateTime());
        vo.setCreateTime(dateTime);
        return ResponseEntity.ok(Response.success(vo));
    }
    /**
     * 查看老师对学生的评价内容
     * 调用php
     * @param teacherRequest
     * @return
     */
    @GetMapping("/viewTeacherAppraise")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 10000612, methodName = "viewTeacherAppraisePhp", description = "查看教师评价", userTypes = {ClientEnum.UserType.STUDENT, ClientEnum.UserType.TEACHER})
    public ResponseEntity<Response> viewTeacherAppraisePhp(ViewTeacherAppraiseRequest teacherRequest) {
        Response phpResponse =null;
        try{
            String phpCacheToken = redisService.getLoginUserCache(teacherRequest.getToken()).getToken();
            String classTeacherAppraiseUuid =teacherRequest.getClassTeacherAppraiseUuid();
            String phpUrl = url.concat("teacher/viewTeacherAppraise");
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer ".concat(phpCacheToken));
            Map<String, String> paramMap = new HashMap<>();
            if(classTeacherAppraiseUuid!=null){
                paramMap.put("classTeacherAppraiseUuid",classTeacherAppraiseUuid);
            }
            String phpResult = HttpUtil.sendPost(phpUrl,paramMap,headerMap);
            LOGGER.info("teacher/viewTeacherAppraise ={}", phpResult);
            phpResponse = JSON.parseObject(phpResult, Response.class);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }

        return ResponseEntity.ok(phpResponse);
    }
    /**
     * 上传文件到阿里OSS
     *
     * @param request
     * @return
     */
    @PostMapping("/uploadFile")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 10000613, methodName = "uploadFile", description = "上传文件到阿里OSS", userTypes = TEACHER)
    public ResponseEntity<Response> uploadFile(UploadRequest request) throws IOException {
        MultipartFile file = request.getFile();
        long fileSize = file.getSize();
        if (file == null || fileSize == 0) {
            return ResponseEntity.ok(Response.error(NULL_FILE));
        }
        InputStream is = file.getInputStream();
        String originalFilename = file.getOriginalFilename();
        String teacherUploadOssKey = getTeacherUploadOssKey(originalFilename);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        OSSUtil ossUtil = new OSSUtil(ossConfig.getDomain(), ossConfig.getBucketName());
        String uploadUrl = ossUtil.upload(is, teacherUploadOssKey);
        ossUtil.closeOssClient();
        return ResponseEntity.ok(Response.success(uploadUrl));
    }

    /**
     * 获取教师个人信息
     *
     * @param request
     * @return
     */
    //@GetMapping("/info")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 10000614, methodName = "info", description = "获取教师个人信息", userTypes = TEACHER)
    public ResponseEntity<Response> info(BaseRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String teacherUuid = loginUserCache.getUserUuid();
        TeacherDto teacherDto = tcTeacherService.findInfoByUuid(teacherUuid);
        TeacherVo teacherVo = TransferUtil.transfer(teacherDto, TeacherVo.class);
        List<TcTeacherFile> teacherFileList = tcTeacherFileService.findByTeacherId(teacherDto.getTeacherId());
        List<TeacherFileVo> teacherFileVoList = new ArrayList<>();
        TeacherFileVo teacherFileVo = new TeacherFileVo();
        OSSUtil ossUtil = new OSSUtil(ossConfig.getDomain(), ossConfig.getBucketName());
        for (TcTeacherFile tcTeacherFile : teacherFileList) {
            teacherFileVo = new TeacherFileVo();
            teacherFileVo.setFileAddress(ossUtil.generatePresignedUrl(tcTeacherFile.getOssKey()));
            Integer purpose = tcTeacherFile.getPurpose();
            teacherFileVo.setPurpose(purpose);
            teacherFileVo.setFileOriginalName(tcTeacherFile.getFileOriginalName());
            teacherFileVoList.add(teacherFileVo);
        }
        teacherVo.setTeacherFileList(teacherFileVoList);
        ossUtil.closeOssClient();
        boolean flag = verifyInfoComplete(teacherVo);
        teacherVo.setCompleteFlag(flag);
        return ResponseEntity.ok(Response.success(teacherVo));
    }
    /**
     * 获取教师个人信息
     * 调用php 与页面直接交互
     * @param request
     * @return
     */
    @GetMapping("/info")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 10000614, methodName = "infoPhp", description = "获取教师个人信息", userTypes = TEACHER)
    public ResponseEntity<Response> infoPhp(BaseRequest request) {
     /*   Response phpResponse =null;
        try{
            String phpCacheToken = redisService.getLoginUserCache(request.getToken()).getToken();
            String phpUrl = url.concat("/teacher/teacherInfo");
            Map<String, String> headerMap = new HashMap<>();
            Map<String, String> paramMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer ".concat(phpCacheToken));
            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            LOGGER.info("infoPhp phpResponse {}", phpResponse.getData());
            phpResponse = JSON.parseObject(phpResult, Response.class);

        }catch (Exception e){

        }*/
        return ResponseEntity.ok(Response.success());
    }
    /**
     * 教师文件必须要有的
     */
    private static final List<Integer> PURPOSEBASELIST = Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 8});

    /**
     * 校验教师信息的完整性
     *
     * @param teacherVo
     * @return
     */
    private boolean verifyInfoComplete(TeacherVo teacherVo) {
        //教师基本信息
        if (StringUtils.isBlank(teacherVo.getTeacherName())
                || teacherVo.getSex() == null
                || StringUtils.isBlank(teacherVo.getPhone())
                || StringUtils.isBlank(teacherVo.getWechat())
                || StringUtils.isBlank(teacherVo.getQq())
                || StringUtils.isBlank(teacherVo.getEmail())
                || StringUtils.isBlank(teacherVo.getProvinceCode())
                || StringUtils.isBlank(teacherVo.getCityCode())
                || StringUtils.isBlank(teacherVo.getDistrictCode())
                || teacherVo.getArtsOrScience() == null
                || StringUtils.isBlank(teacherVo.getTeacherSchoolUuid())
                || teacherVo.getEducation() == null
                || teacherVo.getHighestEducation() == null
                || StringUtils.isBlank(teacherVo.getMajor())
                || StringUtils.isBlank(teacherVo.getGrade())
                || StringUtils.isBlank(teacherVo.getGradePreferenceValue())
                || StringUtils.isBlank(teacherVo.getTeachingSubjectUuid())
                || StringUtils.isBlank(teacherVo.getSecondSubjectUuid())
                || StringUtils.isBlank(teacherVo.getThirdSubjectUuid())
                || StringUtils.isBlank(teacherVo.getIdNumber())
                || StringUtils.isBlank(teacherVo.getCardNumber())
                || StringUtils.isBlank(teacherVo.getBankAddress())) {
            return false;
        }
        //教师文件
        List<TeacherFileVo> teacherFileList = teacherVo.getTeacherFileList();
        if (teacherFileList.size() == 0) {
            return false;
        }
        List<Integer> purposeList = new ArrayList<>();
        for (TeacherFileVo teacherFileVo : teacherFileList) {
            Integer purpose = teacherFileVo.getPurpose();
            purposeList.add(purpose);
        }
        if (!purposeList.containsAll(PURPOSEBASELIST)) {
            return false;
        }
        return true;
    }

    /**
     * 保存教师个人信息
     *
     * @param request
     * @return
     */
    @PostMapping("/infoSaveByJson")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 10000615, methodName = "infoSaveByJson", description = "保存教师个人信息", userTypes = TEACHER)
    public ResponseEntity<Response> infoSaveByJson(@RequestBody SaveTeacherInfoRequest request) {
        String teacherUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        SaveTeacherInfoDto saveTeacherInfoDto = TransferUtil.transfer(request, SaveTeacherInfoDto.class);
        saveTeacherInfoDto.setTeacherUuid(teacherUuid);
        saveTeacherInfoDto.setTeacherFileList(TransferUtil.transfer(request.getTeacherFileList(), TcTeacherFile.class));
        tcTeacherService.infoSave(saveTeacherInfoDto);
        return ResponseEntity.ok(Response.success());
    }


    /**
     * 获取教师所在学校所有列表
     *
     * @param request
     * @return
     */
    @GetMapping("/getAllSchool")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 10000616, methodName = "getAllSchool", description = "获取教师所在学校所有列表", userTypes = TEACHER)
    public ResponseEntity<Response> getAllSchool(BaseRequest request) {
        List<TcTeacherSchool> teacherSchoolList = tcTeacherSchoolService.findAll();
        List<TeacherSchoolVo> voList = new ArrayList<>();
        TeacherSchoolVo vo = new TeacherSchoolVo();
        for (TcTeacherSchool tcTeacherSchool : teacherSchoolList) {
            vo = new TeacherSchoolVo();
            vo.setTeacherSchoolUuid(tcTeacherSchool.getUuid());
            vo.setTeacherSchoolName(tcTeacherSchool.getSchoolName());
            voList.add(vo);
        }
        return ResponseEntity.ok(Response.success(voList));
    }

    /**
     * 获取教师年级
     *
     * @param request
     * @return
     */
    @GetMapping("/getAllGrade")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 10000617, methodName = "getAllGrade", description = "获取教师年级", userTypes = TEACHER)
    public ResponseEntity<Response> getAllGrade(BaseRequest request) {
        List<SysEnum> sysEnumList = sysEnumService.findByEnumType(SYS_ENUM_TYPE_GRADE);
        List<SysEnumVo> voList = new ArrayList<>();
        SysEnumVo vo = new SysEnumVo();
        for (SysEnum sysEnum : sysEnumList) {
            vo = new SysEnumVo();
            vo.setName(sysEnum.getEnumName());
            vo.setValue(sysEnum.getEnumValue());
            voList.add(vo);
        }
        return ResponseEntity.ok(Response.success(voList));
    }

    /**
     * 获取教师年级偏好
     *
     * @param request
     * @return
     */
    @GetMapping("/getAllGradePreference")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 10000618, methodName = "getAllGradePreference", description = "获取教师年级偏好", userTypes = TEACHER)
    public ResponseEntity<Response> getAllGradePreference(BaseRequest request) {
        List<SysEnum> sysEnumList = sysEnumService.findByEnumType(SYS_ENUM_TYPE_GRADE_PREFERENCE);
        List<SysEnumVo> voList = new ArrayList<>();
        SysEnumVo vo = new SysEnumVo();
        for (SysEnum sysEnum : sysEnumList) {
            vo = new SysEnumVo();
            vo.setName(sysEnum.getEnumName());
            vo.setValue(sysEnum.getEnumValue());
            voList.add(vo);
        }
        return ResponseEntity.ok(Response.success(voList));
    }

    /**
     * 获取教师课程记录
     *
     * @param request
     * @return
     * @throws ParseException
     */
    //@GetMapping("/getCourseRecordList")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 10000619, methodName = "getCourseRecordList", description = "获取教师课程记录", userTypes = TEACHER)
    public ResponseEntity<Response> getCourseRecordList(CourseRecordRequest request) throws ParseException {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String teacherUuid = loginUserCache.getUserUuid();
        String userType = loginUserCache.getUserType();
        String subject = request.getSubject();
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();
        Integer courseType = request.getCourseType();
//        List<String> courseDateList = cpCourseService.findTeacherCourseRecordDateList(teacherUuid, subject, startDate, endDate, courseType);
        List<String> courseDateList = new ArrayList<>();
        //List<CourseDto> courseDtoTmpList = cpCourseService.findTeacherCourseRecordV1List(teacherUuid, subject, startDate, endDate, courseType, 1, Integer.MAX_VALUE);
        //正常有回放的才返回
        List<CourseDto> courseDtoTmpList = cpCourseService.findTeacherCourseRecordV1ListNew(teacherUuid, subject, startDate, endDate, courseType);
        Object[] objects = getCourseDtoPageList(courseDtoTmpList, 1, Integer.MAX_VALUE, true, userType);
        List<CourseDto> dtoList = (List<CourseDto>) objects[1];
        List<TeacherCourseRecordVo> voList = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        TeacherCourseRecordVo teacherCourseRecordVo;
        for (CourseDto dto : dtoList) {
            String courseUuid = dto.getCourseUuid();
            String courseDate = dto.getCourseDate();
            String endTime = dto.getEndTime();
            ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
            if (classRoom != null) {
                Long outRoomTime = classRoom.getOutRoomTime();
                if (outRoomTime != null) {
                    if (outRoomTime < dateFormat.parse(courseDate + " " + endTime).getTime()) {
                        endTime = dateFormat.format(outRoomTime).substring(11);
                    }
                }
            }
            dto.setEndTime(endTime);

            courseDateList.add(courseDate);
        }
        List<TeacherCourseRecordListVo> listVoList = new ArrayList<>();
        TeacherCourseRecordListVo listVo = new TeacherCourseRecordListVo();
        //courseDateList 去除重复日期
        courseDateList = removeDuplicate(courseDateList);
        for (String courseDate : courseDateList) {
            voList = new ArrayList<>();
            for (CourseDto dto : dtoList) {
                String courseUuid = dto.getCourseUuid();
                String startTime = dto.getStartTime();
                String endTime = dto.getEndTime();
                if (courseDate.equals(dto.getCourseDate())) {
                    teacherCourseRecordVo = new TeacherCourseRecordVo();
                    teacherCourseRecordVo.setCourseUuid(courseUuid);
                    teacherCourseRecordVo.setCourseDate(courseDate);
                    teacherCourseRecordVo.setStartTime(startTime);
                    teacherCourseRecordVo.setEndTime(endTime);
                    teacherCourseRecordVo.setSubject(dto.getSubject());
                    teacherCourseRecordVo.setCourseType(dto.getCourseType());
                    teacherCourseRecordVo.setName(dto.getStudentName());
                    teacherCourseRecordVo.setGrade(dto.getGrade());
                    teacherCourseRecordVo.setSubjectVersion(dto.getSubjectVersion());
                    teacherCourseRecordVo.setClassTeacherAppraiseUuid(dto.getClassTeacherAppraiseUuid());
                    //检查课程是否有回放数据
                    int recordStatus = getRecordStatus(courseUuid);
                    teacherCourseRecordVo.setRecordStatus(recordStatus);
                    voList.add(teacherCourseRecordVo);
                }
            }
            listVo = new TeacherCourseRecordListVo();
            listVo.setCourseDate(courseDate);
            listVo.setList(voList);
            listVoList.add(listVo);
        }
        return ResponseEntity.ok(Response.success(listVoList));
    }

    /**
     * 获取教师课程记录
     * 调用php
     * @param request
     * @return
     * @throws ParseException
     */
    @GetMapping("/getCourseRecordList")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 10000619, methodName = "getCourseRecordListPhp", description = "获取教师课程记录", userTypes = TEACHER)
    public ResponseEntity<Response> getCourseRecordListPhp(CourseRecordRequest request) throws ParseException {
        Response phpResponse =null;
        List<CourseDto> courseDtoTmpList =new ArrayList<>();
        String phpToken = redisService.getLoginUserCache(request.getToken()).getToken();
        List<TeacherCourseRecordListVo> listVoList = new ArrayList<>();

        try {
            String subject =request.getSubject();
            String startDate =request.getStartDate();
            String endDate =request.getEndDate();
            Integer courseType=request.getCourseType();

            String phpUrl = url.concat("teacher/getCourseRecordList");
            Map<String, String> paramMap = new HashMap<>();
            if(subject!=null){
                paramMap.put("subject",subject);
            }
            if(startDate!=null){
                paramMap.put("startDate",startDate);
            }
            if(endDate!=null){
                paramMap.put("endDate",endDate);
            }
            if(courseType!=null){
                paramMap.put("courseType",courseType.toString());
            }
            LOGGER.info("teacher/getCourseRecordList 传给php={}", JSON.toJSONString(paramMap));

            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer ".concat(phpToken));

            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            phpResponse = JSON.parseObject(phpResult, Response.class);
            Object object=phpResponse.getData();
            if(object!=null){
                courseDtoTmpList=JSON.parseArray(object.toString(),CourseDto.class);
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        try{

            List<String> courseDateList = new ArrayList<>();
            Object[] objects = getCourseDtoPageList(courseDtoTmpList, 1, Integer.MAX_VALUE, true, "TEACHER");
            List<CourseDto> dtoList = (List<CourseDto>) objects[1];
            List<TeacherCourseRecordVo> voList = new ArrayList<>();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            TeacherCourseRecordVo teacherCourseRecordVo;
            for (CourseDto dto : dtoList) {
                String courseUuid = dto.getCourseUuid();
                String courseDate = dto.getCourseDate();
                String endTime = dto.getEndTime();
                ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
                if (classRoom != null) {
                    Long outRoomTime = classRoom.getOutRoomTime();
                    if (outRoomTime != null) {
                        if (outRoomTime < dateFormat.parse(courseDate + " " + endTime).getTime()) {
                            endTime = dateFormat.format(outRoomTime).substring(11);
                        }
                    }
                }
                dto.setEndTime(endTime);

                courseDateList.add(courseDate);
            }

            TeacherCourseRecordListVo listVo = new TeacherCourseRecordListVo();
            courseDateList = removeDuplicate(courseDateList);
            for (String courseDate : courseDateList) {
                voList = new ArrayList<>();
                for (CourseDto dto : dtoList) {
                    String courseUuid = dto.getCourseUuid();
                    String startTime = dto.getStartTime();
                    String endTime = dto.getEndTime();
                    if (courseDate.equals(dto.getCourseDate())) {
                        teacherCourseRecordVo = new TeacherCourseRecordVo();
                        teacherCourseRecordVo.setCourseUuid(courseUuid);
                        teacherCourseRecordVo.setCourseDate(courseDate);
                        teacherCourseRecordVo.setStartTime(startTime);
                        teacherCourseRecordVo.setEndTime(endTime);
                        teacherCourseRecordVo.setSubject(dto.getSubject());
                        teacherCourseRecordVo.setCourseType(dto.getCourseType());
                        teacherCourseRecordVo.setName(dto.getStudentName());
                        teacherCourseRecordVo.setGrade(dto.getGrade());
                        teacherCourseRecordVo.setSubjectVersion(dto.getSubjectVersion());
                        teacherCourseRecordVo.setClassTeacherAppraiseUuid(dto.getClassTeacherAppraiseUuid());
                        int recordStatus = getRecordStatusPhp(courseUuid,phpToken);
                        teacherCourseRecordVo.setRecordStatus(recordStatus);
                        voList.add(teacherCourseRecordVo);
                    }
                }
                listVo = new TeacherCourseRecordListVo();
                listVo.setCourseDate(courseDate);
                listVo.setList(voList);
                listVoList.add(listVo);
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }

        return ResponseEntity.ok(Response.success(listVoList));
    }
    /**
     * 获取教师课程记录
     * 不改原方法的原则，新增此方法
     * 默认查近三天的，页面滚动条触顶一次，再向前查三天
     *
     * @param request
     * @throws ParseException
     */
    //@GetMapping("/getCourseRecordList")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 10000619, methodName = "getCourseRecordListNew", description = "获取教师课程记录", userTypes = TEACHER)
    public ResponseEntity<Response> getCourseRecordListNew(CourseRecordRequest request) throws ParseException {

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        String curDate = sf.format(c.getTime());
        c.add(Calendar.DAY_OF_MONTH, -5);
        String afterDate = sf.format(c.getTime());

        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String teacherUuid = loginUserCache.getUserUuid();
        String userType = loginUserCache.getUserType();
        String subject = request.getSubject();

        //下拉传入的开始时间
        String historyDate = request.getHistoryDate();
        //默认查历史五天包含当天
        String startDate = afterDate;
        String endDate = curDate;
        List<TeacherCourseRecordListVo> listVoList = new ArrayList<>();

        //页面选择时间段
        if ((StringUtils.isNotEmpty(request.getStartDate()) && startDate != curDate) && (StringUtils.isNotEmpty(request.getEndDate()) && endDate != afterDate)) {
            startDate = request.getStartDate();
            endDate = request.getEndDate();
            //如果触发了下拉
            if (StringUtils.isNotBlank(historyDate)) {
                //已加载日期的5天后
                Calendar calendar2 = Calendar.getInstance();
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

                Date historyDateTemp = sdf2.parse(historyDate);
                calendar2.setTime(historyDateTemp);
                calendar2.add(Calendar.DATE, -5);
                String three_days_after = sdf2.format(calendar2.getTime());
                //如果选择了1-30号，且下拉后最后结束时间不等于30号按下拉日期加载数据
                if (!Objects.equals(endDate, request.getEndDate())) {
                    startDate = three_days_after;
                    endDate = historyDate;
                }
            }
        } else {
            if (StringUtils.isNotBlank(historyDate)) {
                if (!sf.format(new Date()).equals(historyDate)) {
                    Calendar historyCalendar = Calendar.getInstance();
                    historyCalendar.setTime(sf.parse(historyDate));
                    historyCalendar.add(Calendar.DATE, -1);
                    //前一天
                    historyDate = sf.format(historyCalendar.getTime());
                }
                //已加载日期的3天后
                Calendar calendar2 = Calendar.getInstance();
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

                Date historyDateTemp = sdf2.parse(historyDate);
                calendar2.setTime(historyDateTemp);
                calendar2.add(Calendar.DATE, -5);
                String three_days_after = sdf2.format(calendar2.getTime());

                startDate = three_days_after;
                endDate = historyDate;
            }
        }
        Integer courseType = request.getCourseType();
        List<String> courseDateList = new ArrayList<>();
        //正常有回放的才返回
        List<CourseDto> courseDtoTmpList = new ArrayList<>();

        //下拉加载可能有空的
        if (StringUtils.isNotEmpty(historyDate)) {
            int count = 0;
            Calendar calendar2 = Calendar.getInstance();
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            //直到有5天数据
            while (count <= 5) {
                //5天一天天的查
                //for(int i=0;i<=5;i++){
                List<CourseDto> oneDayList = new ArrayList<>();
                String minDate = cpCourseService.findMinDate(teacherUuid, null);
                oneDayList = cpCourseService.findTeacherCourseRecordV1OneDay(teacherUuid, subject, historyDate, courseType);
                if (oneDayList.size() > 0) {
                    count++;
                    courseDtoTmpList.addAll(oneDayList);
                }
                //向后延一天
                Date historyDateTemp = sdf2.parse(historyDate);
                calendar2.setTime(historyDateTemp);
                calendar2.add(Calendar.DATE, -1);
                historyDate = sdf2.format(calendar2.getTime());
                //历史日期小于最初历史排课日期不再查询
                if (historyDate.compareTo(minDate) < 0) {
                    break;
                }
                //}
            }
        } else {
            courseDtoTmpList = cpCourseService.findTeacherCourseRecordV1ListNew(teacherUuid, subject, startDate, endDate, courseType);
        }
        Object[] objects = getCourseDtoPageList(courseDtoTmpList, 1, Integer.MAX_VALUE, true, userType);
        List<CourseDto> dtoList = (List<CourseDto>) objects[1];
        List<TeacherCourseRecordVo> voList = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        TeacherCourseRecordVo teacherCourseRecordVo;
        String planEndTime = "";
        for (CourseDto dto : dtoList) {
            String courseUuid = dto.getCourseUuid();
            String courseDate = dto.getCourseDate();
            String endTime = dto.getEndTime();
            planEndTime = dto.getEndTime();
            ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
            if (classRoom != null) {
                Long outRoomTime = classRoom.getOutRoomTime();
                if (outRoomTime != null) {
                    //有退出时间取退出时间
                    endTime = dateFormat.format(outRoomTime).substring(11);
                    dto.setEndTime(endTime);
                } else {
                    //无退出时间取排课结束时间
                    dto.setEndTime(endTime);
                }
            }
            courseDateList.add(courseDate);
        }
        TeacherCourseRecordListVo listVo = new TeacherCourseRecordListVo();
        //courseDateList 去除重复日期
        courseDateList = removeDuplicate(courseDateList);
        for (String courseDate : courseDateList) {
            voList = new ArrayList<>();
            for (CourseDto dto : dtoList) {
                String courseUuid = dto.getCourseUuid();
                String startTime = dto.getStartTime();
                String endTime = dto.getEndTime();
                if (courseDate.equals(dto.getCourseDate())) {
                    teacherCourseRecordVo = new TeacherCourseRecordVo();
                    teacherCourseRecordVo.setCourseUuid(courseUuid);
                    teacherCourseRecordVo.setCourseDate(courseDate);
                    teacherCourseRecordVo.setStartTime(startTime);
                    teacherCourseRecordVo.setEndTime(endTime);
                    //排课（计划）开始、结束时间
                    teacherCourseRecordVo.setPlanStartTime(startTime);
                    teacherCourseRecordVo.setPlanEndTime(planEndTime);

                    List<RoomRecord> roomRecordVoList = new ArrayList<>();
                    //根据房间id与老师id查老师与学生进出房间记录
                    List<ClassRecordExtendEntity> entityList = classRecordService.selectRoomTime(dto.getClassRoomUuid());
                    Integer voiceDuration = null;
                    for (ClassRecordExtendEntity entity : entityList) {
                        RoomRecord roomRecordVo = new RoomRecord();
                        long teacherEnterTime = 0L;
                        long teacherOuterTime = 0L;
                        long studentEnterTime = 0L;
                        long studentOuterTime = 0L;
                        long curCommonTime1 = 0L;
                        long curCommonTime = 0L;
                        if (entity.getRecordTime() != null) {
                            if (Objects.equals(entity.getUserType(), "TEACHER")) {
                                if (Objects.equals(entity.getRecordType(), 0)) {
                                    roomRecordVo.setTeacherOuterTime(entity.getRecordTime().toString());
                                } else if (Objects.equals(entity.getRecordType(), 1)) {
                                    roomRecordVo.setTeacherEnterTime(entity.getRecordTime().toString());
                                }
                                //老师当前进出记录时长
                                if (teacherOuterTime > teacherEnterTime) {
                                    curCommonTime = teacherOuterTime - teacherEnterTime;
                                    roomRecordVo.setTeacherTime((int) curCommonTime / 60000);
                                }
                            }
                            if (Objects.equals(entity.getUserType(), "STUDENT")) {
                                if (Objects.equals(entity.getRecordType(), 0)) {
                                    roomRecordVo.setStudentOuterTime(entity.getRecordTime().toString());
                                } else if (Objects.equals(entity.getRecordType(), 1)) {
                                    roomRecordVo.setStudentEnterTime(entity.getRecordTime().toString());
                                }
                                //学生当前进出记录时长
                                if (studentOuterTime > studentEnterTime) {
                                    curCommonTime1 = studentOuterTime - studentEnterTime;
                                    roomRecordVo.setStudentTime((int) curCommonTime1 / 60000);
                                }
                            }
                        }
                        //共同有效时长
                        voiceDuration = entity.getVoiceDuration();
                        roomRecordVoList.add(roomRecordVo);
                    }
                    //新返回进出记录list
                    teacherCourseRecordVo.setRoomRecordVo(roomRecordVoList);
                    teacherCourseRecordVo.setCommonTime(voiceDuration);

                    teacherCourseRecordVo.setSubject(dto.getSubject());
                    teacherCourseRecordVo.setCourseType(dto.getCourseType());
                    teacherCourseRecordVo.setName(dto.getStudentName());
                    teacherCourseRecordVo.setGrade(dto.getGrade());
                    teacherCourseRecordVo.setSubjectVersion(dto.getSubjectVersion());
                    teacherCourseRecordVo.setClassTeacherAppraiseUuid(dto.getClassTeacherAppraiseUuid());
                    //检查课程是否有回放数据
                    int recordStatus = getRecordStatus(courseUuid);
                    teacherCourseRecordVo.setRecordStatus(recordStatus);
                    voList.add(teacherCourseRecordVo);
                }
            }
            listVo = new TeacherCourseRecordListVo();
            listVo.setCourseDate(courseDate);
            listVo.setList(voList);
            listVoList.add(listVo);
        }
        return ResponseEntity.ok(Response.success(listVoList));
    }

    /**
     * 查看手写板申请
     *
     * @param request
     * @return
     */
    @GetMapping("/viewTabletApply")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 10000620, methodName = "viewTabletApply", description = "查看手写板申请", userTypes = TEACHER)
    public ResponseEntity<Response> viewTabletApply(BaseRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String phone = loginUserCache.getPhone();
        String teacherName = loginUserCache.getUserName();
        TcTablet tcTablet = tcTabletService.findByPhone(phone);
        TabletVo vo = new TabletVo();
        vo.setTeacherName(teacherName);
        vo.setPhone(phone);
        if (tcTablet == null) {
            vo.setIsOrder(0);
        } else {
            String provinceCode = tcTablet.getProvince();
            String postAddress = "";
            if (StringUtils.isNotBlank(provinceCode)) {
                SysArea sysArea = sysAreaService.findByAreaCode(provinceCode);
                postAddress = postAddress + sysArea.getAreaName();
            }
            String cityCode = tcTablet.getCity();
            if (StringUtils.isNotBlank(cityCode)) {
                SysArea sysArea = sysAreaService.findByAreaCode(cityCode);
                postAddress = postAddress + sysArea.getAreaName();
            }
            String districtCode = tcTablet.getDistrict();
            if (StringUtils.isNotBlank(districtCode)) {
                SysArea sysArea = sysAreaService.findByAreaCode(districtCode);
                postAddress = postAddress + sysArea.getAreaName();
            }
            postAddress = postAddress + tcTablet.getPostAddress();
            vo.setIsOrder(1);
            vo.setPostAddress(postAddress);
        }
        return ResponseEntity.ok(Response.success(vo));
    }

    /**
     * 保存手写板申请
     *
     * @param request
     * @return
     */
    @PostMapping("/saveTabletApply")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 10000621, methodName = "saveTabletApply", description = "保存手写板申请", userTypes = TEACHER)
    public ResponseEntity<Response> saveTabletApply(ApplyTabletRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String phone = loginUserCache.getPhone();
        TcTablet tcTablet = tcTabletService.findByPhone(phone);
        if (tcTablet != null) {
            return ResponseEntity.ok(Response.errorCustom("手写板已申请！"));
        }
        String teacherUuid = loginUserCache.getUserUuid();
        int courseCount = cpCourseService.countSuccessCourseByTeacherUuid(teacherUuid);
        if (courseCount == 0) {
            return ResponseEntity.ok(Response.errorCustom("你还没有成功上过一次正式课，暂时不能申请！"));
        }
        tcTablet = new TcTablet();
        tcTablet.setUuid(UUIDUtil.randomUUID2());
        tcTablet.setTcName(loginUserCache.getUserName());
        tcTablet.setPhone(phone);
        tcTablet.setProvince(request.getProvinceCode());
        tcTablet.setCity(request.getCityCode());
        tcTablet.setDistrict(request.getDistrictCode());
        tcTablet.setPostAddress(request.getPostAddress());
        tcTablet.setIsOrder(1);
        tcTabletService.save(tcTablet);
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 获取入门培训视频
     *
     * @param request
     * @return
     */
    @GetMapping("/getTrainingVideo")
    @LogRecordAnnotation(moduleCode = 100006, moduleName = "pc教师端", methodCode = 10000622, methodName = "getTrainingVideo", description = "获取入门培训视频", userTypes = TEACHER)
    public ResponseEntity<Response> getTrainingVideo(BaseRequest request) {
        List<SysEnum> sysEnumList = sysEnumService.findByEnumType("36");
        String videoUrl = "";
        if (sysEnumList == null || sysEnumList.size() == 0 || sysEnumList.get(0) == null /*|| StringUtils.isBlank(sysEnumList.get(0).getEnumValue())*/) {
//            return ResponseEntity.ok(Response.errorCustom("暂无视频！"));
        } else {
            String enumValue = sysEnumList.get(0).getEnumValue();
            videoUrl = enumValue == null ? "" : enumValue;
        }
        Map<String, String> map = new HashMap<>();
        map.put("videoUrl", videoUrl);
        return ResponseEntity.ok(Response.success(map));
    }

}
