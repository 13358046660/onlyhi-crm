package cn.onlyhi.file.controller;


import cn.onlyhi.client.dto.ClassRoomVO;
import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.po.ClassRoom;
import cn.onlyhi.client.po.CpCourse;
import cn.onlyhi.client.po.Leads;
import cn.onlyhi.client.service.*;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.enums.CodeEnum;
import cn.onlyhi.common.util.HttpUtil;
import cn.onlyhi.common.util.Response;
import cn.onlyhi.file.config.AgoraConfig;
import cn.onlyhi.file.config.YmlMyConfig;
import cn.onlyhi.file.request.*;
import cn.onlyhi.file.util.*;
import cn.onlyhi.file.vo.MasterAgoraVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
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
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

import static cn.onlyhi.common.constants.Constants.*;
import static cn.onlyhi.common.constants.Constants.PERSONOFCOURSE_KEY;
import static cn.onlyhi.common.enums.ClientEnum.UserType.CC;
import static cn.onlyhi.common.enums.ClientEnum.UserType.CR;
import static cn.onlyhi.common.enums.ClientEnum.UserType.STUDENT;
import static cn.onlyhi.common.enums.ClientEnum.UserType.TEACHER;
import static cn.onlyhi.common.enums.CodeEnum.*;
import static cn.onlyhi.common.enums.CodeEnum.COURSE_HAS_END;
import static cn.onlyhi.common.enums.CodeEnum.COURSE_NO_STARAT;
import static cn.onlyhi.common.util.ClientUtil.getAgoraUid;
import static cn.onlyhi.common.util.CmdExecuteUtil.exec;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/3/22.
 */
@RestController
@RequestMapping("/client/course")
public class ClientCourseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCourseController.class);

    @Autowired
    private ClassRoomService classRoomService;
    @Autowired
    private CoursewareService coursewareService;
    @Autowired
    private YmlMyConfig ymlMyConfig;
    @Autowired
    private ClassMateService classMateService;
    @Autowired
    private CpCourseService cpCourseService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private ClassRecordDataService classRecordDataService;
    @Autowired
    private LeadsService leadsService;
    @Autowired
    protected RedisService redisService;
    @Autowired
    private AgoraConfig agoraConfig;
    private static ConcurrentLinkedQueue<String> cmdQueue = new ConcurrentLinkedQueue<>();
    @Autowired
    private ClassRecordService classRecordService;
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AsyncTaskConfig.class);
    AsyncTaskService asyncTaskService = context.getBean(AsyncTaskService.class);
    @Value("${phpStaging.url}")
    private String url;
    /**
     * iosGetCourseRoom,getCourseRoom这两个url从之前的rest工程里挪到file里，因为rest工程没有执行录制脚本的条件
     * 录制音频
     *
     * @param request
     * @return
     */
    @PostMapping("/iosGetCourseRoom")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 1000013, methodName = "iosGetCourseRoom", description = "上课获取房间信息并记录时间")
    public ResponseEntity<Response> iosGetCourseRoom(@RequestBody CourseUuidRequest request) throws Exception {
        return getCourseRoomComNewPhp(request);
    }

    @PostMapping("/getCourseRoom")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 1000014, methodName = "getCourseRoom", description = "上课获取房间信息并记录时间")
    public ResponseEntity<Response> getCourseRoom(CourseUuidRequest request) throws Exception {
        return getCourseRoomComNewPhp(request);
    }

    /**
     * 进入房间
     *
     * @param request
     * @return
     * @apiNote 修改流程，只要进入房间即开始录制  wqz
     */
    private ResponseEntity<Response> getCourseRoomComNew(CourseUuidRequest request) {
        ClassRoomVO classRoomOut = new ClassRoomVO();
        try {
            LOGGER.info("进入房间...");
            //当前进入房间人数
            Integer curPersonNum = 0;
            curPersonNum++;

            LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
            String userType = loginUserCache.getUserType();
            String courseUuid = request.getCourseUuid();

            String userUuid = loginUserCache.getUserUuid();
            String userName = loginUserCache.getUserName();
            int agoraUid = loginUserCache.getAgoraUid();


            //进入，房间人数+1
            if (STUDENT.name().equals(userType) || TEACHER.name().equals(userType) || CC.name().equals(userType) || CR.name().equals(userType)) {
                redisService.incr(PERSONOFCOURSE_KEY + courseUuid);
                redisService.expire(PERSONOFCOURSE_KEY + courseUuid, 3600 * 24);
            }
            ClassRoomVO classRoomVOIn = new ClassRoomVO();
            classRoomVOIn.setUserType(userType);
            classRoomVOIn.setCourseUuid(courseUuid);
            classRoomVOIn.setUserUuid(userUuid);
            classRoomVOIn.setUserName(userName);
            classRoomVOIn.setAgoraUid(agoraUid);
            classRoomVOIn.setAppId(agoraConfig.getAppId());
            classRoomVOIn.setAppCertificate(agoraConfig.getAppCertificate());

            classRoomOut = cpCourseService.enterRoom(classRoomVOIn);

            if (Objects.equals(classRoomOut.getInvalid_course(), "INVALID_COURSE")) {
                return ResponseEntity.ok(Response.error(INVALID_COURSE));
            } else if (Objects.equals(classRoomOut.getCourse_no_starat(), "COURSE_NO_STARAT")) {
                return ResponseEntity.ok(Response.error(COURSE_NO_STARAT));
            } else if (Objects.equals(classRoomOut.getCourse_has_end(), "COURSE_HAS_END")) {
                return ResponseEntity.ok(Response.error(COURSE_HAS_END));
            }
            LOGGER.info("进入房间后房间信息记录完成调用录制方法...");
            recordNew(courseUuid);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(Response.success(classRoomOut));
    }
    /**
     * 进入房间
     * 调用php
     * @param request
     * @return
     * @apiNote 修改流程，只要进入房间即开始录制  wqz
     */
    private ResponseEntity<Response> getCourseRoomComNewPhp(CourseUuidRequest request) {
        ClassRoomVO classRoomOut = new ClassRoomVO();
        try {
            LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
            String userType = loginUserCache.getUserType();
            String courseUuid = request.getCourseUuid();

            String userUuid = loginUserCache.getUserUuid();
            String userName = loginUserCache.getUserName();
            int agoraUid = loginUserCache.getAgoraUid();

            String phpToken = loginUserCache.getToken();

            //进入，房间人数+1
            if (STUDENT.name().equals(userType) || TEACHER.name().equals(userType) || CC.name().equals(userType) || CR.name().equals(userType)) {
                redisService.incr(PERSONOFCOURSE_KEY + courseUuid);
                redisService.expire(PERSONOFCOURSE_KEY + courseUuid, 3600 * 24);
            }
            ClassRoomVO classRoomVOIn = new ClassRoomVO();
            classRoomVOIn.setUserType(userType);
            classRoomVOIn.setCourseUuid(courseUuid);
            classRoomVOIn.setUserUuid(userUuid);
            classRoomVOIn.setUserName(userName);
            if(agoraUid<=0){
                LOGGER.error("php返回userUuid 为空");
                return ResponseEntity.ok(Response.error(PARAMETER_ERROR));
            }else {
                classRoomVOIn.setAgoraUid(agoraUid);
            }
            classRoomVOIn.setAppId(agoraConfig.getAppId());
            classRoomVOIn.setAppCertificate(agoraConfig.getAppCertificate());

            //LOGGER.info("enterRoomPhp...");
            classRoomOut = cpCourseService.enterRoomPhp(classRoomVOIn,phpToken);

            //LOGGER.info("classRoomOut={}",JSON.toJSONString(classRoomOut));

            if (Objects.equals(classRoomOut.getInvalid_course(), "INVALID_COURSE")) {
                return ResponseEntity.ok(Response.error(INVALID_COURSE));
            } else if (Objects.equals(classRoomOut.getCourse_no_starat(), "COURSE_NO_STARAT")) {
                return ResponseEntity.ok(Response.error(COURSE_NO_STARAT));
            } else if (Objects.equals(classRoomOut.getCourse_has_end(), "COURSE_HAS_END")) {
                return ResponseEntity.ok(Response.error(COURSE_HAS_END));
            }
            LOGGER.info("进入房间后房间信息记录完成调用录制方法...");
            recordNewPhp(courseUuid,phpToken);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        //LOGGER.info("classRoomOut={}",classRoomOut);
        return ResponseEntity.ok(Response.success(classRoomOut));
    }
    /**
     * 录制音频
     *
     * @return
     */
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001152, methodName = "record", description = "录制音频")
    public ResponseEntity<Response> recordNew(String courseUuid) {
        LOGGER.info("触发录制音频...");
        try {
            //当前进入房间人数
            Integer curPersonNum = 0;
            curPersonNum++;
            CpCourse cpCourse = cpCourseService.findByUuid(courseUuid);
            if (cpCourse == null) {
                LOGGER.info("无此课程...");
                return ResponseEntity.ok(Response.error(NO_COURSE));
            }
            ClassRoom room = classRoomService.findByCourseUuid(courseUuid);
            if (room == null) {
                LOGGER.info("未进入房间...");
                return ResponseEntity.ok(Response.error(NO_ROOM));
            }
            long currentTimeMillis = System.currentTimeMillis();
            String idleLimitSec = "";
            String courseEndTimeStr = cpCourse.getCourseDate() + " " + cpCourse.getEndTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = simpleDateFormat.parse(courseEndTimeStr);
            long overTime = room.getOvertime() == null ? 0 : room.getOvertime();
            long courseEndTime = date.getTime() / 1000 + overTime;
            long curTime = currentTimeMillis / 1000;
            if (!Objects.equals(courseEndTimeStr, null) && curTime < courseEndTime) {
                long diffTime = courseEndTime - curTime;
                idleLimitSec = String.valueOf(diffTime);//进入房间到课程结束的剩余时间
            }
            String recordId = room.getRecordId().toString();//录制id
            String commChannelId = room.getCommChannelId();//通信频道Id
            String signallingChannelId = room.getSignallingChannelId();
            String recordPath = "/www/hktRecord/recordDir";
            File recordDir = new File(recordPath);
            if (!recordDir.exists()) {
                recordDir.mkdirs();
            }
            String leadsUuid = cpCourse.getLeadsUuid();
            String teacherUuid = cpCourse.getTeacherUuid();
            Leads leads = leadsService.findByUuid(leadsUuid);
            if (leads == null) {
                return ResponseEntity.ok(Response.error(COURSE_NO_STUDENT));
            }
            String ccUuid = leads.getCcUuid();
            String crUuid = leads.getCrUuid();
            MasterAgoraVo masterAgoraVo = new MasterAgoraVo();
            masterAgoraVo.setStuAgoraUid(getAgoraUid(leadsUuid, STUDENT.name()));
            if (StringUtils.isNotBlank(teacherUuid)) {
                masterAgoraVo.setTeaAgoraUid(getAgoraUid(teacherUuid, TEACHER.name()));
            }
            if (StringUtils.isNotBlank(ccUuid)) {
                masterAgoraVo.setCcAgoraUid(getAgoraUid(ccUuid, CC.name()));
            }
            if (StringUtils.isNotBlank(crUuid)) {
                masterAgoraVo.setCrAgoraUid(getAgoraUid(crUuid, CR.name()));
            }
            masterAgoraVo.setCourseType(cpCourse.getCourseType());
            String[] cmds = new String[15];
            cmds[0] = "/www/hktRecord/source/Recorder_local";
            cmds[1] = "--uid";
            cmds[2] = recordId;
            cmds[3] = "--channel";
            cmds[4] = commChannelId;
            cmds[5] = "--courseUuid";
            cmds[6] = courseUuid;
            cmds[7] = "--idle";
            cmds[8] = idleLimitSec;
            cmds[9] = "--signallingChannelId";
            cmds[10] = signallingChannelId;
            cmds[11] = "--recordFileRootDir";
            cmds[12] = recordPath;
            cmds[13] = "--masterUids";
            cmds[14] = JSONObject.toJSONString(masterAgoraVo);
            LOGGER.info("录制入参:" + JSONObject.toJSONString(cmds));
            //线程池管理
            //asyncTaskService.executeAsyncTask(cmds, curPersonNum);
            new Thread(() -> {
                try {
                    exec(cmds, false);
                } catch (Exception e) {
                    LOGGER.error("命令执行异常:{}", e.getMessage());
                }
            }).start();
        } catch (Exception e) {
            LOGGER.error("录制执行异常:{}", e.getMessage());
        }
        return ResponseEntity.ok(Response.success());
    }
    /**
     * 录制音频
     *
     * @return
     */
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001152, methodName = "recordNewPhp", description = "录制音频")
    public ResponseEntity<Response> recordNewPhp(String courseUuid,String phpToken) {
        try {
            String phpUrl =url.concat("client/enterRoom/check");
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post=new HttpPost(phpUrl);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            post.setHeader("Authorization", "Bearer ".concat(phpToken));

            JSONObject param= new JSONObject();
            param.put("courseUuid", courseUuid);
            StringEntity stringEntity = new StringEntity(param.toString());
            post.setEntity(stringEntity);

            //LOGGER.info("传给php post={}",JSON.toJSONString(post));
            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity=response.getEntity();
            String result= EntityUtils.toString(entity,"UTF-8");
            LOGGER.info("client/enterRoom/check={}",result);
            Response phpResponse = JSON.parseObject(result, Response.class);
            Object object=phpResponse.getData();
            CpCourse cpCourse=JSON.parseObject(object.toString(),CpCourse.class);

            if (cpCourse == null) {
                return ResponseEntity.ok(Response.error(NO_COURSE));
            }
            ClassRoom room = classRoomService.findByCourseUuid(courseUuid);
            if (room == null) {
                return ResponseEntity.ok(Response.error(NO_ROOM));
            }
            long currentTimeMillis = System.currentTimeMillis();
            String idleLimitSec = "";
            String courseEndTimeStr = cpCourse.getCourseDate() + " " + cpCourse.getEndTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = simpleDateFormat.parse(courseEndTimeStr);
            long overTime = room.getOvertime() == null ? 0 : room.getOvertime();
            long courseEndTime = date.getTime() / 1000 + overTime;
            long curTime = currentTimeMillis / 1000;
            if (!Objects.equals(courseEndTimeStr, null) && curTime < courseEndTime) {
                long diffTime = courseEndTime - curTime;
                //进入房间到课程结束的剩余时间
                idleLimitSec = String.valueOf(diffTime);
            }
            //录制id
            String recordId = room.getRecordId().toString();
            //通信频道Id
            String commChannelId = room.getCommChannelId();
            String signallingChannelId = room.getSignallingChannelId();
            String recordPath = "/www/hktRecord/recordDir";
            File recordDir = new File(recordPath);
            if (!recordDir.exists()) {
                recordDir.mkdirs();
            }
            String leadsUuid = cpCourse.getLeadsUuid();
            String teacherUuid = cpCourse.getTeacherUuid();
            Leads leads =null;
            try{
               String phpUrl2 =url.concat("client/student/check");
               Map<String, String> paramMap = new HashMap<>();
               paramMap.put("leadsUuid", leadsUuid);
               LOGGER.info("client/student/check 传给php={}",paramMap);

               Map<String, String> headerMap = new HashMap<>();
               headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
               headerMap.put("Authorization", "Bearer ".concat(phpToken));
               String phpResult = HttpUtil.sendPost(phpUrl2, paramMap, headerMap);
               LOGGER.info("client/student/check={}",phpResult);
               Response phpResponse2 = JSON.parseObject(phpResult, Response.class);
               Object object2=phpResponse2.getData();
               leads =JSON.parseObject(object2.toString(),Leads.class);
               if (leads == null) {
                   return ResponseEntity.ok(Response.error(COURSE_NO_STUDENT));
               }else if(!Objects.equals(CodeEnum.SUCCESS.getCode(),phpResponse2.getCode())){
                   return ResponseEntity.ok(phpResponse2);
               }
             }catch (Exception e){
               LOGGER.error(e.getMessage());
             }

            String ccUuid = leads.getCcUuid();
            String crUuid = leads.getCrUuid();

            MasterAgoraVo masterAgoraVo = new MasterAgoraVo();
            masterAgoraVo.setStuAgoraUid(getAgoraUid(leadsUuid, STUDENT.name()));
            if (StringUtils.isNotBlank(teacherUuid)) {
                masterAgoraVo.setTeaAgoraUid(getAgoraUid(teacherUuid, TEACHER.name()));
            }
           if (StringUtils.isNotBlank(ccUuid)) {
                masterAgoraVo.setCcAgoraUid(getAgoraUid(ccUuid, CC.name()));
            }
            if (StringUtils.isNotBlank(crUuid)) {
                masterAgoraVo.setCrAgoraUid(getAgoraUid(crUuid, CR.name()));
            }
            masterAgoraVo.setCourseType(cpCourse.getCourseType());

            String[] cmds = new String[15];
            cmds[0] = "/www/hktRecord/source/Recorder_local";
            cmds[1] = "--uid";
            cmds[2] = recordId;
            cmds[3] = "--channel";
            cmds[4] = commChannelId;
            cmds[5] = "--courseUuid";
            cmds[6] = courseUuid;
            cmds[7] = "--idle";
            cmds[8] = idleLimitSec;
            cmds[9] = "--signallingChannelId";
            cmds[10] = signallingChannelId;
            cmds[11] = "--recordFileRootDir";
            cmds[12] = recordPath;
            cmds[13] = "--masterUids";
            cmds[14] = JSONObject.toJSONString(masterAgoraVo);
            LOGGER.info("录制入参:={}",JSONObject.toJSONString(cmds));
            new Thread(() -> {
                try {
                    exec(cmds, false);
                } catch (Exception e) {
                    LOGGER.error("命令执行异常:{}", e.getMessage());
                }
            }).start();
        } catch (Exception e) {
            LOGGER.error("录制执行异常:{}", e.getMessage());
        }
        return ResponseEntity.ok(Response.success());
    }
    /**
     * 判断房间是否存在人：true：存在；false：不存在
     *
     * @param courseUuid
     * @return
     */
    protected boolean isExistPersonOfCourse(String courseUuid) {
        String values = redisService.get(PERSONOFCOURSE_KEY + courseUuid);
        if (StringUtils.isBlank(values) || Integer.parseInt(values) <= 0) {
            return false;
        }
        return true;
    }
    /**以上是2018.5.29号上线第二天新增内容*/
    /**以上是2018.5.29号上线第二天新增内容*/
    /**以上是2018.5.29号上线第二天新增内容*/
    /**
     * 录制音频
     * 调用php
     * @param request
     * @return
     */
    @PostMapping("/record")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001152, methodName = "record", description = "录制音频")
    public ResponseEntity<Response> record(RecordRequest request) throws Exception {
      /*  String courseUuid = request.getCourseUuid();
        CpCourse cpCourse = cpCourseService.findByUuid(courseUuid);
        if (cpCourse == null) {
            return ResponseEntity.ok(Response.error(NO_COURSE));
        }
        String recordId = request.getRecordId();
        String commChannelId = request.getCommChannelId();
        String idleLimitSec = request.getIdleLimitSec();
        String signallingChannelId = request.getSignallingChannelId();
        String recordRole = request.getRecordRole();
        String recordRoleUid = request.getRecordRoleUid();

        String recordPath = "/www/hktRecord/recordDir";
        File recordDir = new File(recordPath);
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }

        String leadsUuid = cpCourse.getLeadsUuid();
        String teacherUuid = cpCourse.getTeacherUuid();
        Leads leads = leadsService.findByUuid(leadsUuid);
        if (leads == null) {
            return ResponseEntity.ok(Response.error(COURSE_NO_STUDENT));
        }
        String ccUuid = leads.getCcUuid();
        String crUuid = leads.getCrUuid();
        MasterAgoraVo masterAgoraVo = new MasterAgoraVo();
        masterAgoraVo.setStuAgoraUid(getAgoraUid(leadsUuid, STUDENT.name()));
        if (StringUtils.isNotBlank(teacherUuid)) {
            masterAgoraVo.setTeaAgoraUid(getAgoraUid(teacherUuid, TEACHER.name()));
        }
        if (StringUtils.isNotBlank(ccUuid)) {
            masterAgoraVo.setCcAgoraUid(getAgoraUid(ccUuid, CC.name()));
        }
        if (StringUtils.isNotBlank(crUuid)) {
            masterAgoraVo.setCrAgoraUid(getAgoraUid(crUuid, CR.name()));
        }
        masterAgoraVo.setCourseType(cpCourse.getCourseType());
        String[] cmds = new String[19];
        cmds[0] = "/www/hktRecord/source/Recorder_local";
        cmds[1] = "--uid";
        cmds[2] = recordId;
        cmds[3] = "--channel";
        cmds[4] = commChannelId;
        cmds[5] = "--courseUuid";
        cmds[6] = courseUuid;
        cmds[7] = "--idle";
        cmds[8] = idleLimitSec;
        cmds[9] = "--signallingChannelId";
        cmds[10] = signallingChannelId;
        cmds[11] = "--recordRole";
        cmds[12] = recordRole;
        cmds[13] = "--recordRoleUid";
        cmds[14] = recordRoleUid;
        cmds[15] = "--recordFileRootDir";
        cmds[16] = recordPath;
        cmds[17] = "--masterUids";
        cmds[18] = JSONObject.toJSONString(masterAgoraVo);

        try {
            exec(cmds, false);
        } catch (Exception e) {
            LOGGER.error("命令执行异常:{}", e.getMessage());
        }*/

        return ResponseEntity.ok(Response.success());
    }

    /**
     * 上传轨迹文件
     *
     * @param request
     * @return
     */
    @PostMapping("/uploadTrack")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001153, methodName = "uploadTrack", description = "上传轨迹文件")
    public ResponseEntity<Response> uploadTrack(UploadTrackFileRequest request) throws Exception {
        MultipartFile multipartFile = request.getFile();
        if (multipartFile == null || multipartFile.getSize() == 0) {
            return ResponseEntity.ok(Response.error(NULL_FILE));
        }
        String originalFilename = multipartFile.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename) || !originalFilename.contains(".db")) {
            return ResponseEntity.ok(Response.error(INVALID_FILE));
        }
        String dateStr = DateFormatUtils.format(new Date(), "yyyyMMdd");
        String saveDir = RECORD_DIR_ROOT + FILESEPARATOR + dateStr;
        String dateStrFileName = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
        String courseUuid = request.getCourseUuid();
        String saveFileName = courseUuid + "_" + dateStrFileName + "_client.db";
        File uploadFile = new File(saveDir, saveFileName);
        if (!uploadFile.getParentFile().exists()) {
            uploadFile.getParentFile().mkdirs();
        }
        //上传文件
        LOGGER.info("dir of uploadTrackFile is {}", uploadFile.getPath());
        Files.copy(multipartFile.getInputStream(), Paths.get(saveDir, saveFileName));
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 学生或老师下课，统计老师和学生的回放数据(音频和轨迹数据)
     * 上传oss 调CourseOssUtil.statisticsTeacherRecordDataByThread
     *
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/statisticsTeacherRecordData")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001177, methodName = "statisticsTeacherRecordData", description = "学生或老师下课，统计老师和学生的回放数据(音频和轨迹数据)", userTypes = {TEACHER, STUDENT})
    public ResponseEntity<Response> statisticsTeacherRecordData(UploadTrackFileRequest request) throws Exception {
        LOGGER.info(" 学生或老师下课，统计老师和学生的回放数据(音频和轨迹数据)");
        String courseUuid = request.getCourseUuid();
        String dateDir = DateFormatUtils.format(new Date(), "yyyyMMdd");
        File recordLogsDir = new File(BASEPATH, dateDir);
        if (!recordLogsDir.exists()) {
            LOGGER.info("recordLogsDir." + recordLogsDir);
            LOGGER.info("courseUuid={}：音频录制目录不存在！", courseUuid);
            return ResponseEntity.ok(Response.success());
        }
       if (cmdQueue.contains(courseUuid)) {
            String info = "courseUuid={" + courseUuid + "}已统计!";
            LOGGER.info(info);
            return ResponseEntity.ok(Response.errorCustom(info));
        }
        cmdQueue.add(courseUuid);
        //调用php
        CourseUtil.statisticsTeacherRecordDataByThreadPhp(courseUuid,classRoomService, classMateService, recordLogsDir, messageService, classRecordDataService, RECORD_DIR_ROOT, ymlMyConfig, coursewareService, 0);
        return ResponseEntity.ok(Response.success());
    }
}
