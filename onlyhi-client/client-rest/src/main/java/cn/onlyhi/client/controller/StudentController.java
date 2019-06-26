package cn.onlyhi.client.controller;

import cn.onlyhi.client.dto.CourseDto;
import cn.onlyhi.client.dto.LeadsSignDto;
import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.dto.LoginUserCachePhp;
import cn.onlyhi.client.po.ClassRoom;
import cn.onlyhi.client.po.Leads;
import cn.onlyhi.client.po.LeadsExt;
import cn.onlyhi.client.request.*;
import cn.onlyhi.client.service.CpPayRequestService;
import cn.onlyhi.client.service.LeadsSignService;
import cn.onlyhi.client.service.StudentService;
import cn.onlyhi.client.vo.CourseCalendarVo;
import cn.onlyhi.client.vo.CourseVo;
import cn.onlyhi.client.vo.LeadsVo;
import cn.onlyhi.client.vo.StudentBaseInfoVo;
import cn.onlyhi.client.vo.StudentClassTimeVo;
import cn.onlyhi.client.vo.StudentCourseRecordListVo;
import cn.onlyhi.client.vo.StudentCourseRecordVo;
import cn.onlyhi.client.vo.StudentCourseRemindVo;
import cn.onlyhi.client.vo.StudentCourseScheduleVo;
import cn.onlyhi.client.vo.StudentNoEndCourseListVo;
import cn.onlyhi.client.vo.StudentNoEndCourseVo;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.enums.CodeEnum;
import cn.onlyhi.common.request.BaseRequest;
import cn.onlyhi.common.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static cn.onlyhi.common.enums.ClientEnum.UserType.STUDENT;
import static cn.onlyhi.common.enums.CodeEnum.STUDENT_HAS_SIGN;
import static cn.onlyhi.common.util.ClientUtil.removeDuplicate;
import static cn.onlyhi.common.util.DateUtil.getDayOfWeekList;
import static cn.onlyhi.common.util.DateUtil.getWeekOfMonth;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2018/4/13.
 */
@RestController
@RequestMapping("/student")
public class StudentController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StudentController.class);
    @Autowired
    private CpPayRequestService cpPayRequestService;
    @Autowired
    private LeadsSignService leadsSignService;
    @Autowired
    private StudentService studentService;
    @Value("${phpStaging.url}")
    private String url;
    /**
     * 学生基本信息
     *
     * @param request
     * @return
     */
    //@GetMapping("/baseInfo")
    @LogRecordAnnotation(moduleCode = 1000010, moduleName = "pc学生端", methodCode = 10000101, methodName = "baseInfo", description = "学生基本信息", userTypes = STUDENT)
    public ResponseEntity<Response> baseInfo(BaseRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String leadsName = loginUserCache.getUserName();
        String leadsUuid = loginUserCache.getUserUuid();

        String monthDate = DateFormatUtils.format(new Date(), "yyyy-MM");
        //本月消耗的课时
        double classTime = cpCourseService.findClassTimeByLeadsUuidAndMonth(leadsUuid, monthDate);
        LeadsExt leadsExt = leadsExtService.findLeadsExtByLeadsUuid(leadsUuid);
        String iconurl = "";
        if (leadsExt != null) {
            iconurl = leadsExt.getIconurl() == null ? "" : leadsExt.getIconurl();
        }
        //本月所有有排课的学生总数
        int courseLeadsCount = cpCourseService.countCourseLeadsByMonthDate(monthDate);
        //本月小于消耗课时的学生数
        int lessClassTimeLeadsCount = cpCourseService.countLessClassTimeLeadsByMonthDate(classTime, monthDate);
        //本月消耗课时超过其他人比率
        String surpassRatio = "0%";
        if (courseLeadsCount > 0) {
            surpassRatio = new BigDecimal(lessClassTimeLeadsCount * 100 / courseLeadsCount).setScale(0, BigDecimal.ROUND_HALF_UP).toString() + "%";
        }

        //本月学生没有参加课程的节数
        int noJoinClassCount = cpCourseService.countNoJoinClassCountByMonthDate(leadsUuid, monthDate);
        //本月学生课程总节数
        int courseTotalCount = cpCourseService.countCourseByLeadsUuidByMonthDate(leadsUuid, monthDate);
        //本月出勤率
        String attendanceRate = "0%";
        if (courseTotalCount > 0) {
            attendanceRate = new BigDecimal((courseTotalCount - noJoinClassCount) * 100 / courseTotalCount).setScale(0, BigDecimal.ROUND_HALF_UP).toString() + "%";
        }

        StudentBaseInfoVo vo = new StudentBaseInfoVo();
        vo.setName(leadsName);
        vo.setPhoto(iconurl);
        vo.setHour(classTime);
        vo.setSurpassRatio(surpassRatio);
        vo.setNoJoinClassCount(noJoinClassCount);
        vo.setAttendanceRate(attendanceRate);
        vo.setIsSign(leadsSignService.isSign(leadsUuid));

        return ResponseEntity.ok(Response.success(vo));
    }

    /**
     * 学生基本信息
     * 调用php
     * @param request
     * @return
     */
    @GetMapping("/baseInfo")
    @LogRecordAnnotation(moduleCode = 1000010, moduleName= "pc学生端", methodCode = 10000101, methodName = "baseInfoPhp", description = "学生基本信息", userTypes = STUDENT)
    public ResponseEntity<Response> baseInfoPhp( BaseRequest request) {
        Response phpResponse =null;
        try
        {
            LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
            String phpUrl = url.concat("student/baseInfo");
            Map<String, String> paramMap = new HashMap<>();
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer ".concat(loginUserCache.getToken()));
            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            LOGGER.info("student/baseInfo ={}", phpResult);
            phpResponse = JSON.parseObject(phpResult, Response.class);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(phpResponse);
    }
    /**
     * 学生课时信息（剩余课时和总课时）
     *
     * @param request
     * @return
     */
    //@GetMapping("/classTime")
    @LogRecordAnnotation(moduleCode = 1000010, moduleName = "pc学生端", methodCode = 10000102, methodName = "classTime", description = "学生课时（剩余课时和总课时）", userTypes = STUDENT)
    public ResponseEntity<Response> classTime(BaseRequest request) {
        String leadsUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        //普通总课时
        double ordinaryTotalTime = cpPayRequestService.findCourseTimeByLeadsUuidAndCourseLevel(leadsUuid, 0);
        //清北总课时
        double qbTotalTime = cpPayRequestService.findCourseTimeByLeadsUuidAndCourseLevel(leadsUuid, 1);
        //精品总课时
        double qualityTotalTime = cpPayRequestService.findCourseTimeByLeadsUuidAndCourseLevel(leadsUuid, 2);

        //普通消耗总课时
        double ordinaryExpendTime = cpCourseService.findClassTimeByLeadsUuidAndCourseLevel(leadsUuid, 0);
        //清北消耗总课时
        double qbExpendTime = cpCourseService.findClassTimeByLeadsUuidAndCourseLevel(leadsUuid, 1);
        //精品消耗总课时
        double qualityExpendTime = cpCourseService.findClassTimeByLeadsUuidAndCourseLevel(leadsUuid, 2);

        StudentClassTimeVo vo = new StudentClassTimeVo();
        vo.setOrdinaryTotalTime(ordinaryTotalTime);
        vo.setQbTotalTime(qbTotalTime);
        vo.setQualityTotalTime(qualityTotalTime);
        vo.setOrdinarySurplusTime(ordinaryTotalTime - ordinaryExpendTime);
        vo.setQbSurplusTime(qbTotalTime - qbExpendTime);
        vo.setQualitySurplusTime(qualityTotalTime - qualityExpendTime);

        return ResponseEntity.ok(Response.success(vo));
    }
    /**
     * 学生课时信息（剩余课时和总课时）
     * 调用php
     * @param request
     * @return
     */
    @GetMapping("/classTime")
    @LogRecordAnnotation(moduleCode = 1000010, moduleName = "pc学生端", methodCode = 10000102, methodName = "classTimePhp", description = "学生课时（剩余课时和总课时）", userTypes = STUDENT)
    public ResponseEntity<Response> classTimePhp(BaseRequest request) {
        Response phpResponse =null;
        try{
            LoginUserCache phpCacheToken = redisService.getLoginUserCache(request.getToken());
            String phpUrl = url.concat("student/classTime");
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("classTime token", request.getToken());
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer "+phpCacheToken.getToken());
            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            LOGGER.info("classTimePhp phpResult {}",phpResult);
            phpResponse = JSON.parseObject(phpResult, Response.class);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(Response.success(phpResponse.getData()));
    }
    /**
     * 根据日期查看排课列表
     *
     * @param request
     * @return
     * @throws ParseException
     */
    //@GetMapping("/findCourseListByDate")
    @LogRecordAnnotation(moduleCode = 1000010, moduleName = "pc学生端", methodCode = 10000103, methodName = "findCourseListByDate", description = "根据日期查看排课列表", userTypes = STUDENT)
    public ResponseEntity<Response> findCourseListByDate(CourseDateRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String leadsUuid = loginUserCache.getUserUuid();
        String courseDate = request.getCourseDate();
        List<CourseDto> courseDtoList = cpCourseService.findByLeadsUuidAndCourseDate(leadsUuid, courseDate);

        List<CourseVo> voList = TransferUtil.transfer(courseDtoList, CourseVo.class);
        Page<CourseVo> page = new Page<>(voList.size(), voList);
        return ResponseEntity.ok(Response.success(page));
    }
    /**
     * 根据日期查看排课列表
     * 调用php
     * @param request
     * @return
     * @throws ParseException
     */
    @GetMapping("/findCourseListByDate")
    @LogRecordAnnotation(moduleCode = 1000010, moduleName = "pc学生端", methodCode = 10000103, methodName = "findCourseListByDatePhp", description = "根据日期查看排课列表", userTypes = STUDENT)
    public ResponseEntity<Response> findCourseListByDatePhp(CourseDateRequest request) {
        Response phpResponse =null;
        try {
            String phpCacheToken = redisService.getLoginUserCache(request.getToken()).getToken();
            String phpUrl = url.concat("student/findCourseListByDate");
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("courseDate",request.getCourseDate());
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer ".concat(phpCacheToken));
            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            if(phpResult==null){
                LOGGER.info("根据日期查看排课列表 student/findCourseListByDate 返回空");
            }

            phpResponse =JSON.parseObject(phpResult, Response.class);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(phpResponse);
    }
    /**
     * 今日未结束课程列表
     *
     * @param request
     * @return
     * @throws ParseException
     */
    //@GetMapping("/todayNoEndCourseList")
    @LogRecordAnnotation(moduleCode = 1000010, moduleName = "pc学生端", methodCode = 10000104, methodName = "todayNoEndCourseList", description = "今日未结束课程列表", userTypes = STUDENT)
    public ResponseEntity<Response> todayNoEndCourseList(BaseRequest request) throws ParseException {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userType = loginUserCache.getUserType();
        String leadsUuid = loginUserCache.getUserUuid();
        String courseDate = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        List<CourseDto> courseDtoTmpList = cpCourseService.findNoEndCourseListByLeadsUuidAndCourseDate(leadsUuid, courseDate);
        Object[] objects = getCourseDtoPageList(courseDtoTmpList, 1, Integer.MAX_VALUE, false, userType);
        List<CourseDto> courseDtoList = (List<CourseDto>) objects[1];
        List<CourseVo> voList = TransferUtil.transfer(courseDtoList, CourseVo.class);
        return ResponseEntity.ok(Response.success(voList));
    }
    /**
     * 今日未结束课程列表
     * 调用php
     * @param request
     * @return
     * @throws ParseException
     */
    @GetMapping("/todayNoEndCourseList")
    @LogRecordAnnotation(moduleCode = 1000010, moduleName = "pc学生端", methodCode = 10000104, methodName = "todayNoEndCourseListPhp", description = "今日未结束课程列表", userTypes = STUDENT)
    public ResponseEntity<Response> todayNoEndCourseListPhp(BaseRequest request) throws ParseException {
        List<CourseVo> voList =new ArrayList<>();
        try {
            LoginUserCachePhp phpCacheToken = redisService.getLoginUserCachePhp(request.getToken());
            String phpUrl = url.concat("student/todayNoEndCourseList");
            Map<String, String> paramMap = new HashMap<>();
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer ".concat(phpCacheToken.getToken()));
            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            if(phpResult==null){
                LOGGER.info("学生今日未结束课程列表student/todayNoEndCourseList 返回空");
            }

            Response phpResponse = JSON.parseObject(phpResult, Response.class);

            Object object=phpResponse.getData();
            List<CourseDto> courseDtoTmpList=JSON.parseArray(object.toString(),CourseDto.class);
            //LOGGER.info("学生今日未结束课程列表 courseDtoTmpList={}",JSON.toJSONString(courseDtoTmpList));

            Object[] objects = getCourseDtoPageList(courseDtoTmpList, 1, Integer.MAX_VALUE, false, "STUDENT");
            List<CourseDto> courseDtoList = (List<CourseDto>) objects[1];
            voList = TransferUtil.transfer(courseDtoList, CourseVo.class);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(Response.success(voList));
    }
    /**
     * 课程日历
     *
     * @param request
     * @return
     */
    //@GetMapping("/courseCalendar")
    @LogRecordAnnotation(moduleCode = 1000010, moduleName = "pc学生端", methodCode = 10000105, methodName = "courseCalendar", description = "课程日历", userTypes = STUDENT)
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
        String leadsUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        List<String> courseDateList = cpCourseService.findCourseDateByLeadsUuidAndYearMonth(leadsUuid, dateTime);
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
    @LogRecordAnnotation(moduleCode = 1000010, moduleName = "pc学生端", methodCode = 10000105, methodName = "courseCalendarPhp", description = "课程日历", userTypes = STUDENT)
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
        List<String> courseDateList=new ArrayList<>();
        try{
            LoginUserCache loginUserCache=redisService.getLoginUserCache(request.getToken());
            String phpUrl =url.concat("student/courseCalendar");
            String userUuid=loginUserCache.getUserUuid();
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("userUuid",userUuid);
            paramMap.put("dateTime", dateTime);
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer ".concat(loginUserCache.getToken()));
            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            if(phpResult==null){
                LOGGER.info("学生课程日历 student/courseCalendar 返回空");
            }

            Response phpResponse = JSON.parseObject(phpResult, Response.class);
            Object object=phpResponse.getData();

            if(object!=null){
                courseDateList=JSON.parseArray(object.toString(),String.class);
                LOGGER.info("courseDateList={}", courseDateList);
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }

        List<List<CourseCalendarVo>> voLists = new ArrayList<>();
        try{
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
            List<CourseCalendarVo> vos = new ArrayList<>();
            for (int i = 0; i < 6; i++) {   //一个月六周
                vos = new ArrayList<>();
                for (int j = 0; j < 7; j++) {
                    CourseCalendarVo courseCalendarVo = voList.get(7 * i + j);
                    vos.add(courseCalendarVo);
                }
                voLists.add(vos);
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(Response.success(voLists));
    }


    @GetMapping("/sign")
    @LogRecordAnnotation(moduleCode = 1000010, moduleName = "pc学生端", methodCode = 10000106, methodName = "sign", description = "签到", userTypes = STUDENT)
    public ResponseEntity<Response> sign(BaseRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String leadsUuid = loginUserCache.getUserUuid();
        LeadsSignDto leadsSignDto =null;
        if(leadsUuid!=null){
            leadsSignDto = leadsSignService.sign(leadsUuid);
            if (leadsSignDto == null) {
                return ResponseEntity.ok(Response.error(STUDENT_HAS_SIGN));
            }
        }
        return ResponseEntity.ok(Response.success(leadsSignDto));
    }


    /**
     * 待上课列表
     *
     * @param request
     * @return
     */
    //@GetMapping("/getNoEndCourseList")
    @LogRecordAnnotation(moduleCode = 1000010, moduleName = "pc学生端", methodCode = 10000107, methodName = "getNoEndCourseList", description = "待上课列表", userTypes = STUDENT)
    public ResponseEntity<Response> getNoEndCourseList(BaseRequest request) throws ParseException {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userType = loginUserCache.getUserType();
        String leadsUuid = loginUserCache.getUserUuid();
        int pageNo = 1;
        int pageSize = Integer.MAX_VALUE;
        List<CourseDto> courseDtoTmpList = cpCourseService.findNoEndCourseByLeadsUuid(leadsUuid, pageNo, pageSize);
        Object[] objects = getCourseDtoPageList(courseDtoTmpList, pageNo, pageSize, false, userType);
        List<CourseDto> courseDtoList = (List<CourseDto>) objects[1];
        List<String> courseDateList = courseDtoList.stream().map(courseDto -> courseDto.getCourseDate()).collect(Collectors.toList());
        //courseDateList 去除重复日期
        courseDateList = removeDuplicate(courseDateList);

        List<StudentNoEndCourseVo> voList = new ArrayList<>();
        StudentNoEndCourseVo studentNoEndCourseVo;
        StudentNoEndCourseListVo listVo = new StudentNoEndCourseListVo();
        List<StudentNoEndCourseListVo> listVoList = new ArrayList<>();
        for (String courseDate : courseDateList) {
            voList = new ArrayList<>();
            for (CourseDto dto : courseDtoList) {
                String courseUuid = dto.getCourseUuid();
                String startTime = dto.getStartTime();
                String endTime = dto.getEndTime();
                if (courseDate.equals(dto.getCourseDate())) {
                    studentNoEndCourseVo = new StudentNoEndCourseVo();
                    studentNoEndCourseVo.setCourseUuid(courseUuid);
                    studentNoEndCourseVo.setCourseDate(courseDate);
                    studentNoEndCourseVo.setStartTime(startTime);
                    studentNoEndCourseVo.setEndTime(endTime);
                    studentNoEndCourseVo.setSubject(dto.getSubject());
                    studentNoEndCourseVo.setCourseType(dto.getCourseType());
                    studentNoEndCourseVo.setTeacherName(dto.getTeacherName());
                    studentNoEndCourseVo.setStudentClassStatus(dto.getStudentClassStatus());
                    studentNoEndCourseVo.setGrade(dto.getGrade());
                    voList.add(studentNoEndCourseVo);
                }
            }
            listVo = new StudentNoEndCourseListVo();
            listVo.setCourseDate(courseDate);
            listVo.setList(voList);
            listVoList.add(listVo);
        }
        return ResponseEntity.ok(Response.success(listVoList));
    }

    /**
     * 待上课列表
     * 调用php
     * @param request
     * @return
     */
    @GetMapping("/getNoEndCourseList")
    @LogRecordAnnotation(moduleCode = 1000010, moduleName = "pc学生端", methodCode = 10000107, methodName = "getNoEndCourseListPhp", description = "待上课列表", userTypes = STUDENT)
    public ResponseEntity<Response> getNoEndCourseListPhp(BaseRequest request) throws ParseException {
        int pageNo = 1;
        int pageSize = Integer.MAX_VALUE;
        List<CourseDto> courseDtoTmpList =new ArrayList<>();
        try{
            String phpCacheToken = redisService.getLoginUserCache(request.getToken()).getToken();
            String phpUrl = url.concat("student/getNoEndCourseList");
            Map<String, String> paramMap = new HashMap<>();
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer ".concat(phpCacheToken));

            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            if(phpResult==null){
                LOGGER.info("学生待上课列表student/getNoEndCourseList 返回空");
            }

            Response phpResponse = JSON.parseObject(phpResult, Response.class);
            Object object=phpResponse.getData();
            courseDtoTmpList =JSON.parseArray(object.toString(),CourseDto.class);

        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        Object[] objects ={};
        if(courseDtoTmpList.size()>0){
            objects = getCourseDtoPageList(courseDtoTmpList, pageNo, pageSize, false, "STUDENT");
        }
        List<StudentNoEndCourseListVo> listVoList = new ArrayList<>();
        if(objects.length>0){
            List<CourseDto> courseDtoList = (List<CourseDto>) objects[1];
            List<String> courseDateList = courseDtoList.stream().map(courseDto -> courseDto.getCourseDate()).collect(Collectors.toList());
            //courseDateList 去除重复日期
            courseDateList = removeDuplicate(courseDateList);
            List<StudentNoEndCourseVo> voList = new ArrayList<>();
            StudentNoEndCourseVo studentNoEndCourseVo;
            StudentNoEndCourseListVo listVo = new StudentNoEndCourseListVo();

            for (String courseDate : courseDateList) {
                voList = new ArrayList<>();
                for (CourseDto dto : courseDtoList) {
                    String courseUuid = dto.getCourseUuid();
                    String startTime = dto.getStartTime();
                    String endTime = dto.getEndTime();
                    if (courseDate.equals(dto.getCourseDate())) {
                        studentNoEndCourseVo = new StudentNoEndCourseVo();
                        studentNoEndCourseVo.setCourseUuid(courseUuid);
                        studentNoEndCourseVo.setCourseDate(courseDate);
                        studentNoEndCourseVo.setStartTime(startTime);
                        studentNoEndCourseVo.setEndTime(endTime);
                        studentNoEndCourseVo.setSubject(dto.getSubject());
                        studentNoEndCourseVo.setCourseType(dto.getCourseType());
                        studentNoEndCourseVo.setTeacherName(dto.getTeacherName());
                        studentNoEndCourseVo.setStudentClassStatus(dto.getStudentClassStatus());
                        studentNoEndCourseVo.setGrade(dto.getGrade());
                        voList.add(studentNoEndCourseVo);
                    }
                }
                listVo = new StudentNoEndCourseListVo();
                listVo.setCourseDate(courseDate);
                listVo.setList(voList);
                listVoList.add(listVo);
            }
        }

        return ResponseEntity.ok(Response.success(listVoList));
    }
    /**
     * 获取学生课程记录
     *
     * @param request
     * @return
     * @throws ParseException
     */
    //@GetMapping("/getCourseRecordList")
    @LogRecordAnnotation(moduleCode = 1000010, moduleName = "pc学生端", methodCode = 10000108, methodName = "getCourseRecordList", description = "获取学生课程记录", userTypes = STUDENT)
    public ResponseEntity<Response> getCourseRecordList(CourseRecordRequest request) throws ParseException {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String leadsUuid = loginUserCache.getUserUuid();
        String userType = loginUserCache.getUserType();
        String subject = request.getSubject();
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();
        List<String> courseDateList = new ArrayList<>();
        //List<CourseDto> courseDtoTmpList = cpCourseService.findStudentCourseRecordV1List(leadsUuid, subject, startDate, endDate, 1, Integer.MAX_VALUE);
        //正常有回放的才返回
        List<CourseDto> courseDtoTmpList = cpCourseService.findStudentCourseRecordV1ListNew(leadsUuid, subject, startDate, endDate);
        Object[] objects = getCourseDtoPageList(courseDtoTmpList, 1, Integer.MAX_VALUE, true, userType);
        List<CourseDto> dtoList = (List<CourseDto>) objects[1];
        List<StudentCourseRecordVo> voList = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        StudentCourseRecordVo studentCourseRecordVo;
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
        List<StudentCourseRecordListVo> listVoList = new ArrayList<>();
        StudentCourseRecordListVo listVo = new StudentCourseRecordListVo();
        //courseDateList 去除重复日期
        courseDateList = removeDuplicate(courseDateList);
        for (String courseDate : courseDateList) {
            voList = new ArrayList<>();
            for (CourseDto dto : dtoList) {
                String courseUuid = dto.getCourseUuid();
                String startTime = dto.getStartTime();
                String endTime = dto.getEndTime();
                if (courseDate.equals(dto.getCourseDate())) {
                    studentCourseRecordVo = new StudentCourseRecordVo();
                    studentCourseRecordVo.setCourseUuid(courseUuid);
                    studentCourseRecordVo.setCourseDate(courseDate);
                    studentCourseRecordVo.setStartTime(startTime);
                    studentCourseRecordVo.setEndTime(endTime);
                    studentCourseRecordVo.setSubject(dto.getSubject());
                    studentCourseRecordVo.setCourseType(dto.getCourseType());
                    studentCourseRecordVo.setName(dto.getTeacherName());
                    studentCourseRecordVo.setClassTeacherAppraiseUuid(dto.getClassTeacherAppraiseUuid());
                    studentCourseRecordVo.setClassAppraiseUuid(dto.getClassAppraiseUuid());
                    //检查课程是否有回放数据
                    int recordStatus = getRecordStatus(courseUuid);
                    studentCourseRecordVo.setRecordStatus(recordStatus);
                    voList.add(studentCourseRecordVo);
                }
            }
            listVo = new StudentCourseRecordListVo();
            listVo.setCourseDate(courseDate);
            listVo.setList(voList);
            listVoList.add(listVo);
        }
        return ResponseEntity.ok(Response.success(listVoList));
    }
    /**
     * 获取学生课程记录
     * 调用php
     * @param request
     * @return
     * @throws ParseException
     */
    @GetMapping("/getCourseRecordList")
    @LogRecordAnnotation(moduleCode = 1000010, moduleName = "pc学生端", methodCode = 10000108, methodName = "getCourseRecordListPhp", description = "获取学生课程记录", userTypes = STUDENT)
    public ResponseEntity<Response> getCourseRecordListPhp(CourseRecordRequest request) throws ParseException {
        String phpToken = redisService.getLoginUserCache(request.getToken()).getToken();
        List<CourseDto> courseDtoTmpList =new ArrayList<>();
        List<StudentCourseRecordListVo> listVoList = new ArrayList<>();
        try {
            String phpUrl = url.concat("student/getCourseRecordList");
            Map<String, String> paramMap = new HashMap<>();
            if(request.getSubject()!=null){
                paramMap.put("subject",request.getSubject());
            }
            if(request.getStartDate()!=null){
                paramMap.put("startDate",request.getStartDate());
            }
            if(request.getEndDate()!=null){
                paramMap.put("endDate",request.getEndDate());
            }
            if(request.getCourseType()!=null){
                paramMap.put("courseType",request.getCourseType().toString());
            }
            LOGGER.info("student/getCourseRecordList 传给php ={}", JSON.toJSONString(paramMap));

            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer ".concat(phpToken));
            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            if(phpResult==null){
                LOGGER.info("获取学生课程记录 返回空");
            }

            Response phpResponse = JSON.parseObject(phpResult, Response.class);
            Object object=phpResponse.getData();
            if(object!=null){
                courseDtoTmpList =JSON.parseArray(object.toString(),CourseDto.class);
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }

        try {
            List<String> courseDateList = new ArrayList<>();
            Object[] objects = getCourseDtoPageList(courseDtoTmpList, 1, Integer.MAX_VALUE, true, "STUDENT");
            List<CourseDto> dtoList = (List<CourseDto>) objects[1];
            List<StudentCourseRecordVo> voList = new ArrayList<>();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            StudentCourseRecordVo studentCourseRecordVo;
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

            StudentCourseRecordListVo listVo = new StudentCourseRecordListVo();
            //courseDateList 去除重复日期
            courseDateList = removeDuplicate(courseDateList);
            for (String courseDate : courseDateList) {
                voList = new ArrayList<>();
                for (CourseDto dto : dtoList) {
                    String courseUuid = dto.getCourseUuid();
                    String startTime = dto.getStartTime();
                    String endTime = dto.getEndTime();
                    if (courseDate.equals(dto.getCourseDate())) {
                        studentCourseRecordVo = new StudentCourseRecordVo();
                        studentCourseRecordVo.setCourseUuid(courseUuid);
                        studentCourseRecordVo.setCourseDate(courseDate);
                        studentCourseRecordVo.setStartTime(startTime);
                        studentCourseRecordVo.setEndTime(endTime);
                        studentCourseRecordVo.setSubject(dto.getSubject());
                        studentCourseRecordVo.setCourseType(dto.getCourseType());
                        studentCourseRecordVo.setName(dto.getTeacherName());
                        studentCourseRecordVo.setClassTeacherAppraiseUuid(dto.getClassTeacherAppraiseUuid());
                        studentCourseRecordVo.setClassAppraiseUuid(dto.getClassAppraiseUuid());
                        //检查课程是否有回放数据
                        int recordStatus = getRecordStatusPhp(courseUuid,phpToken);
                        studentCourseRecordVo.setRecordStatus(recordStatus);
                        voList.add(studentCourseRecordVo);
                    }
                }
                listVo = new StudentCourseRecordListVo();
                listVo.setCourseDate(courseDate);
                listVo.setList(voList);
                listVoList.add(listVo);
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("校验后学生课程记录 ={}", JSON.toJSONString(listVoList));
        return ResponseEntity.ok(Response.success(listVoList));
    }
    /**
     * 获取学生课程记录
     * 不改原方法的原则，新增此方法
     * 默认查近三天的，页面滚动条触顶一次，再向前查三天
     *
     * @param request
     * @return
     * @throws ParseException
     */
    //@GetMapping("/getCourseRecordList")
    @LogRecordAnnotation(moduleCode = 1000010, moduleName = "pc学生端", methodCode = 10000108, methodName = "getCourseRecordListNew", description = "获取学生课程记录", userTypes = STUDENT)
    public ResponseEntity<Response> getCourseRecordListNew(CourseRecordRequest request) throws ParseException {

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        String curDate = sf.format(c.getTime());
        c.add(Calendar.DAY_OF_MONTH, -5);
        String afterDate = sf.format(c.getTime());

        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String leadsUuid = loginUserCache.getUserUuid();
        String userType = loginUserCache.getUserType();
        String subject = request.getSubject();


        //下拉传入的开始时间
        String historyDate = request.getHistoryDate();
        //默认查历史三天包含当天
        String startDate = afterDate;
        String endDate = curDate;

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
                List<CourseDto> oneDayList = new ArrayList<>();
                String minDate = cpCourseService.findMinDate(null, leadsUuid);
                oneDayList = cpCourseService.findStudentCourseRecordV1ListNew(leadsUuid, subject, startDate, endDate);
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
            }
        } else {
            courseDtoTmpList = cpCourseService.findStudentCourseRecordV1ListNew(leadsUuid, subject, startDate, endDate);
        }
        Object[] objects = getCourseDtoPageList(courseDtoTmpList, 1, Integer.MAX_VALUE, true, userType);
        List<CourseDto> dtoList = (List<CourseDto>) objects[1];
        List<StudentCourseRecordVo> voList = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        StudentCourseRecordVo studentCourseRecordVo;
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
        List<StudentCourseRecordListVo> listVoList = new ArrayList<>();
        StudentCourseRecordListVo listVo = new StudentCourseRecordListVo();
        //courseDateList 去除重复日期
        courseDateList = removeDuplicate(courseDateList);
        for (String courseDate : courseDateList) {
            voList = new ArrayList<>();
            for (CourseDto dto : dtoList) {
                String courseUuid = dto.getCourseUuid();
                String startTime = dto.getStartTime();
                String endTime = dto.getEndTime();
                if (courseDate.equals(dto.getCourseDate())) {
                    studentCourseRecordVo = new StudentCourseRecordVo();
                    studentCourseRecordVo.setCourseUuid(courseUuid);
                    studentCourseRecordVo.setCourseDate(courseDate);
                    studentCourseRecordVo.setStartTime(startTime);
                    studentCourseRecordVo.setEndTime(endTime);
                    studentCourseRecordVo.setSubject(dto.getSubject());
                    studentCourseRecordVo.setCourseType(dto.getCourseType());
                    studentCourseRecordVo.setName(dto.getTeacherName());
                    studentCourseRecordVo.setClassTeacherAppraiseUuid(dto.getClassTeacherAppraiseUuid());
                    studentCourseRecordVo.setClassAppraiseUuid(dto.getClassAppraiseUuid());
                    //检查课程是否有回放数据
                    int recordStatus = getRecordStatus(courseUuid);
                    studentCourseRecordVo.setRecordStatus(recordStatus);
                    voList.add(studentCourseRecordVo);
                }
            }
            listVo = new StudentCourseRecordListVo();
            listVo.setCourseDate(courseDate);
            listVo.setList(voList);
            listVoList.add(listVo);
        }
        return ResponseEntity.ok(Response.success(listVoList));

    }

    /**
     * 课表
     *
     * @param request
     * @return
     */
    @GetMapping("/courseSchedule")
    @LogRecordAnnotation(moduleCode = 1000010, moduleName = "pc学生端", methodCode = 10000109, methodName = "courseSchedule", description = "课表", userTypes = STUDENT)
    public ResponseEntity<Response> courseSchedule(DateTimeRequest request) throws ParseException {
        String leadsUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        String dateTime = request.getDateTime();
        String[] dateTimes = dateTime.split("-");
        String yearStr = dateTimes[0];
        String monthStr = dateTimes[1];
        int year = Integer.parseInt(yearStr);
        int month = Integer.parseInt(monthStr);
        List<List<StudentCourseScheduleVo>> voLists = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {  //一个月六周
            int weekOfMonth = i;
            List<StudentCourseScheduleVo> courseScheduleVoList = getCourseScheduleOfWeek(year, month, weekOfMonth, leadsUuid);
            voLists.add(courseScheduleVoList);
        }
        return ResponseEntity.ok(Response.success(voLists));
    }

    private List<StudentCourseScheduleVo> getCourseScheduleOfWeek(int year, int month, int weekOfMonth, String leadsUuid) throws ParseException {
        List<String> dayListOfWeek = getDayOfWeekList(year, month, weekOfMonth);
        List<StudentCourseScheduleVo> voList = new ArrayList<>();
        StudentCourseScheduleVo vo = new StudentCourseScheduleVo();
        for (String day : dayListOfWeek) {
            vo = new StudentCourseScheduleVo();
            List<CourseDto> courseDtoList = cpCourseService.findByLeadsUuidAndCourseDate(leadsUuid, day);
            List<StudentCourseRemindVo> studentCourseRemindVoList = TransferUtil.transfer(courseDtoList, StudentCourseRemindVo.class);

            vo.setCourseDate(day);
            vo.setCourseScheduleList(studentCourseRemindVoList);
            voList.add(vo);
        }
        return voList;
    }


    /**
     * 学生个人信息
     *
     * @param request
     * @return
     * @throws ParseException
     */
   // @GetMapping("/info")
    @LogRecordAnnotation(moduleCode = 1000010, moduleName = "pc学生端", methodCode = 100001010, methodName = "info", description = "学生个人信息", userTypes = STUDENT)
    public ResponseEntity<Response> info(BaseRequest request) {
        String leadsUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        Leads leads = leadsService.findByUuid(leadsUuid);
        LeadsExt leadsExt = leadsExtService.findLeadsExtByLeadsUuid(leadsUuid);
        String iconurl = "";
        if (leadsExt != null) {
            iconurl = leadsExt.getIconurl() == null ? "" : leadsExt.getIconurl();
        }
        LeadsVo vo = new LeadsVo();
        vo.setPhoto(iconurl);
        vo.setName(leads.getName());
        vo.setGrade(leads.getGrade());
        vo.setSex(leads.getSex());
        vo.setPhone(leads.getPhone());
        vo.setSubject(leads.getSubject());
        String address = (leads.getProvince() == null ? "" : leads.getProvince()) + (leads.getCity() == null ? "" : leads.getCity()) + (leads.getCount() == null ? "" : leads.getCount());
        vo.setAddress(address);
        vo.setSchool(leads.getSchool());
        vo.setSchoolLevel(leads.getSchoolLevel());
        vo.setGradeRank(leads.getGradeRank());
        return ResponseEntity.ok(Response.success(vo));
    }
    /**
     * 学生个人信息
     * 调用php
     * @param request
     * @return
     * @throws ParseException
     */
    @GetMapping("/info")
    @LogRecordAnnotation(moduleCode = 1000010, moduleName = "pc学生端", methodCode = 100001010, methodName = "infoPhp", description = "学生个人信息", userTypes = STUDENT)
    public ResponseEntity<Response> infoPhp(BaseRequest request) {
        Response phpResponse =null;
        try{
            String phpToken= redisService.getLoginUserCache(request.getToken()).getToken();
            String phpUrl = url.concat("student/info");
            Map<String, String> headerMap = new HashMap<>();
            Map<String, String> paramMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer ".concat(phpToken));
            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            phpResponse = JSON.parseObject(phpResult, Response.class);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }

        return ResponseEntity.ok(phpResponse);
    }
    /**
     * （正式生）学生端根据手机号更新年级
     *
     * @param request
     * @return
     * @throws ParseException
     */
    @PostMapping("/updateGrade")
    @LogRecordAnnotation(moduleCode = 1000011, moduleName = "pc学生端", methodCode = 100001011, methodName = "updateGrade", description = "学生端根据手机号更新年级", userTypes = STUDENT)
    public ResponseEntity<Response> updateGrade(DateTimeListRequest request) {
        String leadsUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        Leads leads = leadsService.findByUuid(leadsUuid);
        if(StringUtils.isNotEmpty(request.getGrade()) && !Objects.equals(leads.getPhone(),null))
        {
            int count=leadsService.updateGradeByUuid(leadsUuid, request.getGrade());
            int count1=studentService.updateGradeByPhone(request.getGrade(),leads.getPhone());
            if(count==0 && count1==0){
                return ResponseEntity.ok(Response.error(CodeEnum.FAILURE));
            }
        }
        return ResponseEntity.ok(Response.success(CodeEnum.SUCCESS));
    }
}
