package cn.onlyhi.client.controller;


import cn.onlyhi.client.dto.CourseDto;
import cn.onlyhi.client.dto.CourseLeadsDto;
import cn.onlyhi.client.dto.CourseRecordDto;
import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.po.ClassRoom;
import cn.onlyhi.client.po.PatriarchLeads;
import cn.onlyhi.client.request.CourseRecordRequest;
import cn.onlyhi.client.service.CourseLeadsService;
import cn.onlyhi.client.service.PatriarchLeadsService;
import cn.onlyhi.client.vo.*;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.enums.CodeEnum;
import cn.onlyhi.common.request.PageRequest;
import cn.onlyhi.common.util.HttpUtil;
import cn.onlyhi.common.util.Page;
import cn.onlyhi.common.util.Response;
import cn.onlyhi.common.util.TransferUtil;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static cn.onlyhi.common.enums.ClientEnum.UserType.PATRIARCH;
import static cn.onlyhi.common.enums.ClientEnum.UserType.STUDENT;
import static cn.onlyhi.common.util.ClientUtil.getAgoraUid;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/3/17.
 */
@RestController
@RequestMapping("/client/teacher")
public class ClientTeacherController extends BaseController {

    @Autowired
    private CourseLeadsService courseLeadsService;
    @Autowired
    private PatriarchLeadsService patriarchLeadsService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientTeacherController.class);
    @Value("${phpStaging.url}")
    private String url;
    /**
     * 教师课程记录（一对一）
     *
     * @param request
     * @return
     */
    @GetMapping("/getCourseRecordV1List")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001148, methodName = "getCourseRecordV1List", description = "教师课程记录（一对一）")
    public ResponseEntity<Response> getCourseRecordV1List(CourseRecordRequest request) throws ParseException {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String teacherUuid = loginUserCache.getUserUuid();
        String userType = loginUserCache.getUserType();
        String subject = request.getSubject();
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();
        Integer pageNo = request.getPageNo();
        Integer pageSize = request.getPageSize();
        List<CourseDto> courseDtoTmpList = cpCourseService.findTeacherCourseRecordV1List(teacherUuid, subject, startDate, endDate, null, 1, Integer.MAX_VALUE);
        Object[] objects = getCourseDtoPageList(courseDtoTmpList, pageNo, pageSize, true,userType);
        int count = (int) objects[0];
        List<CourseDto> dtoList = (List<CourseDto>) objects[1];
        List<CourseListVo> voList = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        CourseListVo courseListVo;
        for (CourseDto dto : dtoList) {
            String courseUuid = dto.getCourseUuid();
            String courseDate = dto.getCourseDate();
            String startTime = dto.getStartTime();
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
            courseListVo = new CourseListVo();
            courseListVo.setCourseUuid(courseUuid);
            courseListVo.setCourseDate(courseDate);
            courseListVo.setStartTime(startTime);
            courseListVo.setEndTime(endTime);
            courseListVo.setSubject(dto.getSubject());
            courseListVo.setTeacherName(dto.getTeacherName());
            courseListVo.setCourseType(dto.getCourseType());
            courseListVo.setName(dto.getStudentName());
            courseListVo.setClassTime(dto.getClassTime());
            courseListVo.setClassTeacherAppraiseUuid(dto.getClassTeacherAppraiseUuid());
            voList.add(courseListVo);
        }
        Page<CourseListVo> page = new Page<>(count, voList);
        return ResponseEntity.ok(Response.success(page));
    }

    /**
     * 教师课程记录（一对多）
     *
     * @param request
     * @return
     */
    //@GetMapping("/getCourseRecordList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001134, methodName = "getCourseRecordList", description = "教师课程记录（一对多）")
    public ResponseEntity<Response> getCourseRecordList(CourseRecordRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String teacherUuid = loginUserCache.getUserUuid();
        String subject = request.getSubject();
        String startDate = request.getStartDate();
        String endDate = request.getEndDate();
        Integer pageNo = request.getPageNo();
        Integer pageSize = request.getPageSize();
        long count = cpCourseService.countTeacherCourseRecordList(teacherUuid, subject, startDate, endDate);
        List<CourseRecordDto> dtoList = cpCourseService.findTeacherCourseRecordList(teacherUuid, subject, startDate, endDate, pageNo, pageSize);
        Page<CourseRecordDto> page = new Page<>(count, dtoList);
        return ResponseEntity.ok(Response.success(page));
    }
    /**
     * 教师待上课列表（一对一）
     * 调用php
     * @param request
     * @return
     */
    @GetMapping("/noEndCourseV1List")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001136, methodName = "noEndCourseV1ListPhp", description = "教师待上课列表（一对一）")
    public ResponseEntity<Response> noEndCourseV1ListPhp(PageRequest request) throws ParseException {
        Response phpResponse =null;
        try {
            String token=redisService.getLoginUserCache(request.getToken()).getToken();
            String phpUrl = url.concat("client/teacher/noEndCourseV1List");
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post=new HttpPost(phpUrl);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            post.setHeader("Authorization", "Bearer ".concat(token));

            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity=response.getEntity();
            String result= EntityUtils.toString(entity,"UTF-8");
            if(result==null){
                LOGGER.info("教师待上课列表 返回空");
            }

            phpResponse = JSON.parseObject(result, Response.class);
            Object object=phpResponse.getData();
            if(object!=null){
                DataVO dataVO=JSON.parseObject(object.toString(),DataVO.class);
                if(Objects.equals(dataVO.getList().size(),0)){
                    LOGGER.info("教师待上课列表,返回空会导致进入房间失败");
                }
            }
            }catch (Exception e){
                LOGGER.info("Exception={}", e.getMessage());
            }
            return ResponseEntity.ok(phpResponse);
    }
    /**
     * 教师待上课列表（一对一）
     *
     * @param request
     * @return
     */
    //@GetMapping("/noEndCourseV1List")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001136, methodName = "noEndCourseV1List", description = "教师待上课列表（一对一）")
    public ResponseEntity<Response> noEndCourseV1List(PageRequest request) throws ParseException {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String teacherUuid = loginUserCache.getUserUuid();
        String userType = loginUserCache.getUserType();
        Integer pageNo = request.getPageNo();
        Integer pageSize = request.getPageSize();
        List<CourseDto> courseDtoTmpList = cpCourseService.findTeacherNoEndV1Course(teacherUuid, 1, Integer.MAX_VALUE);
        Object[] objects = getCourseDtoPageList(courseDtoTmpList, pageNo, pageSize, false,userType);
        int count = (int) objects[0];
        List<CourseDto> courseDtoList = (List<CourseDto>) objects[1];
        List<TeacherNoEndV1CourseListVo> voList = new ArrayList<>();
        TeacherNoEndV1CourseListVo vo;
        for (CourseDto courseDto : courseDtoList) {
            vo = TransferUtil.transfer(courseDto, TeacherNoEndV1CourseListVo.class);
            String leadsUuid = courseDto.getLeadsUuid();
            PatriarchLeads patriarchLeads = patriarchLeadsService.findByLeadsUuid(leadsUuid);
            Integer patriarchAgoraUid = null;
            if (patriarchLeads != null) {
                patriarchAgoraUid = getAgoraUid(patriarchLeads.getPatriarchUuid(), PATRIARCH.name());
            }
            vo.setChannelPatriarchId(patriarchAgoraUid);
            vo.setChannelStudentId(getAgoraUid(leadsUuid, STUDENT.name()));
            voList.add(vo);
        }
        Page<TeacherNoEndV1CourseListVo> page = new Page<>(count, voList);
        return ResponseEntity.ok(Response.success(page));
    }

    /**
     * 教师待上课列表（一对多）
     *
     * @param request
     * @return
     */
    @GetMapping("/noEndCourseList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001146, methodName = "noEndCourseList", description = "教师待上课列表（一对多）")
    public ResponseEntity<Response> noEndCourseList(PageRequest request) {
        String teacherUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        Integer pageNo = request.getPageNo();
        Integer pageSize = request.getPageSize();
        long count = cpCourseService.countTeacherNoEndCourse(teacherUuid);
        List<CourseDto> courseDtoList = cpCourseService.findTeacherNoEndCourse(teacherUuid, pageNo, pageSize);
        List<TeacherNoStartCourseListVo> voList = new ArrayList<>();
        TeacherNoStartCourseListVo vo;
        for (CourseDto dto : courseDtoList) {
            String courseUuid = dto.getCourseUuid();
            String courseDate = dto.getCourseDate();
            String startTime = dto.getStartTime();
            String endTime = dto.getEndTime();
            String teacherName = dto.getTeacherName();
            Integer courseType = dto.getCourseType();
            String subject = null;

            List<CourseLeadsVo> courseLeadsVoList = new ArrayList<>();
            CourseLeadsVo courseLeadsVo;
            List<CourseLeadsDto> courseLeadsDtoList = courseLeadsService.findByCourseUuid(courseUuid);
            for (CourseLeadsDto courseLeadsDto : courseLeadsDtoList) {
                subject = courseLeadsDto.getSubject();
                String leadsUuid = courseLeadsDto.getLeadsUuid();
                String leadsName = courseLeadsDto.getLeadsName();

                courseLeadsVo = new CourseLeadsVo();
                courseLeadsVo.setChannelStudentId(getAgoraUid(leadsUuid, STUDENT.name()));
                courseLeadsVo.setStudentName(leadsName);
                courseLeadsVoList.add(courseLeadsVo);
            }

            vo = new TeacherNoStartCourseListVo();
            vo.setCourseUuid(courseUuid);
            vo.setCourseDate(courseDate);
            vo.setStartTime(startTime);
            vo.setEndTime(endTime);
            vo.setSubject(subject);
            vo.setTeacherName(teacherName);
            vo.setStudentList(courseLeadsVoList);
            vo.setCourseType(courseType);

            voList.add(vo);
        }
        Page<TeacherNoStartCourseListVo> page = new Page<>(count, voList);
        return ResponseEntity.ok(Response.success(page));
    }


}
