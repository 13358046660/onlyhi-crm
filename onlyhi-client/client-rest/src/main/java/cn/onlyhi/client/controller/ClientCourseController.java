package cn.onlyhi.client.controller;


import cn.onlyhi.client.config.AgoraConfig;
import cn.onlyhi.client.dto.*;
import cn.onlyhi.client.po.*;
import cn.onlyhi.client.request.*;
import cn.onlyhi.client.service.*;
import cn.onlyhi.client.util.CourseUtilExtend;
import cn.onlyhi.client.vo.*;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.enums.CodeEnum;
import cn.onlyhi.common.util.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static cn.onlyhi.common.constants.Constants.BASEPATH;
import static cn.onlyhi.common.constants.Constants.PERSONOFCOURSE_KEY;
import static cn.onlyhi.common.enums.ClientEnum.UserType.*;
import static cn.onlyhi.common.enums.CodeEnum.*;
import static cn.onlyhi.common.util.ClientUtil.getAgoraUid;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/3/22.
 */
@RestController
@RequestMapping("/client/course")
public class ClientCourseController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCourseController.class);

    @Autowired
    private ClassRoomService classRoomService;
    @Autowired
    private ClassAppraiseStarService classAppraiseStarService;
    @Autowired
    private ClassAppraiseService classAppraiseService;
    @Autowired
    private UserService userService;
    @Autowired
    private PatriarchLeadsService patriarchLeadsService;
    @Autowired
    private AgoraConfig agoraConfig;
    @Autowired
    private CpCourseService cpCourseService;
    @Autowired
    private ClassMateService classMateService;
    @Autowired
    private ClassRecordService classRecordService;
    @Value("${phpStaging.url}")
    private String url;
    private int  count=0;
    /**
     * 获取上课人员的声网uid列表
     *
     * @param request
     * @return
     */
    //@GetMapping("/getAgoraUidList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001163, methodName = "getAgoraUidList", description = "获取上课人员的声网uid列表")
    public ResponseEntity<Response> getAgoraUidList(CourseUuidRequest request) {
        String courseUuid = request.getCourseUuid();
        CpCourse cpCourse = cpCourseService.findByUuid(courseUuid);
        if (cpCourse == null) {
            return ResponseEntity.ok(Response.error(NO_COURSE));
        }
        AgoraVo agoraVo = new AgoraVo();
        String leadsUuid = cpCourse.getLeadsUuid();
        String teacherUuid = cpCourse.getTeacherUuid();
        Leads leads = leadsService.findByUuid(leadsUuid);
        if (leads == null) {
            return ResponseEntity.ok(Response.error(COURSE_NO_STUDENT));
        }
        String ccUuid = leads.getCcUuid();
        String crUuid = leads.getCrUuid();

        List<Integer> tsAgoraUidList = new ArrayList<>();
        List<UserDto> tsUserUuidList = userService.findAllMonitor();   //拥有教学监课角色的userUuid列表
        for (UserDto userDto : tsUserUuidList) {
            tsAgoraUidList.add(getAgoraUid(userDto.getUserUuid(), MONITOR.name()));
        }
        List<Integer> qcAgoraUidList = new ArrayList<>();
        List<UserDto> qcUserUuidList = userService.findQCMonitor();   //拥有QC监课角色的userUuid列表
        for (UserDto userDto : qcUserUuidList) {
            qcAgoraUidList.add(getAgoraUid(userDto.getUserUuid(), QC.name()));
        }

        agoraVo.setStuAgoraUid(getAgoraUid(leadsUuid, STUDENT.name()));
        if (StringUtils.isNotBlank(teacherUuid)) {
            agoraVo.setTeaAgoraUid(getAgoraUid(teacherUuid, TEACHER.name()));
        }
        PatriarchLeads patriarchLeads = patriarchLeadsService.findByLeadsUuid(leadsUuid);
        Integer patriarchAgoraUid = null;
        if (patriarchLeads != null) {
            patriarchAgoraUid = getAgoraUid(patriarchLeads.getPatriarchUuid(), PATRIARCH.name());
        }
        agoraVo.setPatAgoraUid(patriarchAgoraUid);
        if (StringUtils.isNotBlank(ccUuid)) {
            agoraVo.setCcAgoraUid(getAgoraUid(ccUuid, CC.name()));
        }
        if (StringUtils.isNotBlank(crUuid)) {
            agoraVo.setCrAgoraUid(getAgoraUid(crUuid, CR.name()));
        }
        agoraVo.setTsAgoraUidList(tsAgoraUidList);
        agoraVo.setQcAgoraUidList(qcAgoraUidList);
        return ResponseEntity.ok(Response.success(agoraVo));
    }
    /**
     * 获取上课人员的声网uid列表
     * 调用php
     * @param request
     * @return
     */
    @GetMapping("/getAgoraUidList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001163, methodName = "getAgoraUidListPhp", description = "获取上课人员的声网uid列表")
    public ResponseEntity<Response> getAgoraUidListPhp(CourseUuidRequest request) {
        Response phpResponse =null;
        if(StringUtil.isNotStringNull(request.getToken()) && StringUtil.isNotStringNull(request.getCourseUuid())){
            String phpToken = redisService.getLoginUserCache(request.getToken()).getToken();
            String phpUrl = url.concat("client/course/getAgoraUidList?courseUuid=".concat(request.getCourseUuid()));
            try {
                CloseableHttpClient client = HttpClients.createDefault();
                HttpGet post=new HttpGet(phpUrl);
                post.setHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
                post.setHeader("Authorization", "Bearer ".concat(phpToken));
                CloseableHttpResponse response = client.execute(post);
                HttpEntity entity=response.getEntity();
                String result= EntityUtils.toString(entity,"UTF-8");
                if(result==null){
                    LOGGER.info("获取上课人员的声网uid列表 php 返回空");
                }
                phpResponse = JSON.parseObject(result, Response.class);
            }catch (Exception e){
                LOGGER.error(e.getMessage());
            }
        }

        return ResponseEntity.ok(phpResponse);
    }

    /**
     * 获取学生和老师的上课轨迹数据
     *
     * @param request
     * @return
     * @throws Exception
     */
    //@GetMapping("/getTrackData")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001154, methodName = "getTrackData", description = "获取学生和老师的上课轨迹数据")
    public ResponseEntity<Response> getTrackData(CourseUuidRequest request) throws IOException {
        String courseUuid = request.getCourseUuid();
        CpCourse cpCourse = cpCourseService.findByUuid(courseUuid);
        //只返回有回放的
        //CpCourse cpCourse = cpCourseService.selectNormalBack(courseUuid);
        Integer classStatus = cpCourse.getClassStatus();
        Integer studentClassStatus = cpCourse.getStudentClassStatus();
        if (Objects.equals("classStatus", "0") || Objects.equals("studentClassStatus", "0")) {
            LOGGER.info("cpCourse.getClassStatus()={},cpCourse.getStudentClassStatus()={}", classStatus, studentClassStatus);
            return ResponseEntity.ok(Response.error(NO_RECORD_DATA));
        }
        ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
        //ClassRoom classRoom = classRoomService.selectNormalRoomGold(courseUuid);
        if (classRoom == null) {
            LOGGER.info("classRoom == null");
            return ResponseEntity.ok(Response.error(NO_RECORD_DATA));
        }
        Integer statisticsStatus = classRoom.getStatisticsStatus();
        /**判断是否昨天的录制，且状态未统计（最大可能是录制未成功没有相应录制文件），则提示"TEMP_NO_RECORD_DATA*/
      /*  boolean isYesterday=false;
        if(classRoom.getOutRoomTime()!=null && StringUtils.isNotBlank(classRoom.getOutRoomTime().toString())){
            Long outTime=classRoom.getOutRoomTime();
            isYesterday= DateUtil.isYesterday(outTime);
        }
        if (isYesterday && statisticsStatus == 0) {
            return ResponseEntity.ok(Response.error(TEMP_NO_RECORD_DATA));
        }*/
        String trackUrl = classRoom.getTrackUrl();
        String voiceUrl = classRoom.getVoiceUrl();
        Integer voiceDuration = classRoom.getVoiceDuration();
        String flacVoiceUrl = classRoom.getFlacVoiceUrl();
        //|| StringUtils.isBlank(flacVoiceUrl)
        if (statisticsStatus == 1 || StringUtils.isBlank(trackUrl) || StringUtils.isBlank(voiceUrl) || voiceDuration == null) {
            return ResponseEntity.ok(Response.error(NO_RECORD_DATA));
        }
        URL url = new URL(trackUrl);
        InputStream is = url.openStream();
        TrackData trackData = SerializationUtils.deserialize(is);
        is.close();
        trackData.setVoiceUrl(voiceUrl);
        trackData.setDuration(voiceDuration);
        trackData.setFlacVoiceUrl(flacVoiceUrl);
        return ResponseEntity.ok(Response.success(trackData));
    }
    /**
     * 获取学生和老师的上课轨迹数据
     * 调用php
     * 回放列表需求变更了，上课或未上课都显示，没有音频、轨迹url及时长的，播放按钮不可点
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/getTrackData")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001154, methodName = "getTrackDataPhp", description = "获取学生和老师的上课轨迹数据")
    public ResponseEntity<Response> getTrackDataPhp(CourseUuidRequest request) throws IOException {
        //String phpToken=redisService.getLoginUserCache(request.getToken()).getToken();
        //LOGGER.info("phpToken={}",phpToken);
        String courseUuid = request.getCourseUuid();
        /*CpCourse cpCourse=null;
        try{
           String phpUrl =url.concat("client/course/detail");
            LOGGER.info("phpUrl={}",phpUrl);
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post=new HttpPost(phpUrl);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            post.setHeader("Authorization", "Bearer ".concat(phpToken));

            JSONObject param= new JSONObject();
            param.put("courseUuid", courseUuid);
            StringEntity stringEntity = new StringEntity(param.toString());
            post.setEntity(stringEntity);

            LOGGER.info("post 前phpToken ={}",phpToken);
            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity=response.getEntity();
            String result= EntityUtils.toString(entity,"UTF-8");
            LOGGER.info("getTrackDataPhp result ={}",result);
            cpCourse = JSON.parseObject(result, CpCourse.class);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }*/

         /* Integer classStatus = cpCourse.getClassStatus();
        Integer studentClassStatus = cpCourse.getStudentClassStatus();

       if (Objects.equals(classStatus, 0) || Objects.equals(studentClassStatus, 0)) {
            return ResponseEntity.ok(Response.error(NO_RECORD_DATA));
        }*/
        ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
     /*   if (classRoom == null) {
             LOGGER.info("classRoom == null");
             return ResponseEntity.ok(Response.error(NO_RECORD_DATA));
        }*/
        String trackUrl =null;
        String voiceUrl =null;
        Integer voiceDuration=null;
        if(classRoom!=null){
            Integer statisticsStatus = classRoom.getStatisticsStatus();
            trackUrl = classRoom.getTrackUrl();
            voiceUrl = classRoom.getVoiceUrl();
            voiceDuration = classRoom.getVoiceDuration();
            if (statisticsStatus == 1 || StringUtils.isBlank(trackUrl) || StringUtils.isBlank(voiceUrl) || voiceDuration == null) {
                return ResponseEntity.ok(Response.error(NO_RECORD_DATA));
            }
        }else {
            return ResponseEntity.ok(Response.error(NO_COURSE));
        }

        URL url = new URL(trackUrl);
        InputStream is = url.openStream();
        TrackData trackData = SerializationUtils.deserialize(is);
        is.close();
        trackData.setVoiceUrl(voiceUrl);
        trackData.setDuration(voiceDuration);
        return ResponseEntity.ok(Response.success(trackData));
    }
    /**
     * 获取学生和老师的上课轨迹数据
     * boss系统用
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/getTrackDataPhp")
    public ResponseEntity<Response> getTrackDataPhpNew(CourseUuidRequest request) throws IOException {
        TrackData trackData =new TrackData();
        try{
            String courseUuid = request.getCourseUuid();
            ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
            if(classRoom!=null){
                Integer statisticsStatus = classRoom.getStatisticsStatus();
                String trackUrl = classRoom.getTrackUrl();
                String voiceUrl = classRoom.getVoiceUrl();
                Integer voiceDuration = classRoom.getVoiceDuration();
                if (statisticsStatus == 1 || StringUtils.isBlank(trackUrl) || StringUtils.isBlank(voiceUrl) || voiceDuration == null) {
                    return ResponseEntity.ok(Response.error(NO_RECORD_DATA));
                }
                URL url = new URL(trackUrl);
                InputStream is = url.openStream();
                trackData = SerializationUtils.deserialize(is);
                is.close();
                trackData.setVoiceUrl(voiceUrl);
                trackData.setDuration(voiceDuration);
            }else{
                return ResponseEntity.ok(Response.error(NO_COURSE));
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(Response.success(trackData));
    }
    /**
     * 获取学生和老师的上课轨迹数据
     * 不修改原方法原则，冗余此方法
     * 解决讲师中心上传的课件转换为图片后在教室内不能加载，由于讲师中心上传的课件在oss上，与客户端存入表的图片url不一致
     * 调用：测试环境/getTrackDataNew
     * 发布线上改为getTrackData注释原/getTrackData
     * @param request
     * @return
     * @throws Exception
     */
    //@GetMapping("/getTrackDataNew")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001154, methodName = "getTrackDataOss", description = "获取学生和老师的上课轨迹数据")
    public ResponseEntity<Response> getTrackDataOss(CourseUuidRequest request) throws IOException {
        String courseUuid = request.getCourseUuid();
        CpCourse cpCourse = cpCourseService.findByUuid(courseUuid);
        Integer classStatus = cpCourse.getClassStatus();
        Integer studentClassStatus = cpCourse.getStudentClassStatus();
        if (Objects.equals("classStatus", "0") || Objects.equals("studentClassStatus", "0")) {
            LOGGER.info("cpCourse.getClassStatus()={},cpCourse.getStudentClassStatus()={}", classStatus, studentClassStatus);
            return ResponseEntity.ok(Response.error(NO_RECORD_DATA));
        }
        ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
        if (classRoom == null) {
            LOGGER.info("classRoom == null");
            return ResponseEntity.ok(Response.error(NO_RECORD_DATA));
        }
        Integer statisticsStatus = classRoom.getStatisticsStatus();

        String trackUrl;
        String voiceUrl;
        Integer voiceDuration = classRoom.getVoiceDuration();
        if (statisticsStatus == 1 || voiceDuration == null) {
            return ResponseEntity.ok(Response.error(NO_RECORD_DATA));
        }
        TrackData trackData;
        //根据oss key 取
        if(!Objects.equals(classRoom.getTrackOssKey(),null)){
            OSSClient client = OssUtils.generateOssClient();

            String m4aOssKey=classRoom.getM4aOssKey();
            String m4aOssUrl = OssUtils.generateFileUrl(client,m4aOssKey);

            String trackOssKey=classRoom.getTrackOssKey();
            trackUrl=OssUtils.generateFileUrl(client,trackOssKey);
            client.shutdown();

            //close()必须放到前面再set其它值，否则trackData返回不了voiceUrl等其它字段值
            URL url = new URL(trackUrl);
            InputStream is = url.openStream();
            trackData = SerializationUtils.deserialize(is);
            is.close();

            trackData.setVoiceUrl(m4aOssUrl);
            trackData.setDuration(voiceDuration);
            //轨迹是在解析原轨迹文件的过程中存到TrackData对象的,不需要set

        }else {
            trackUrl = classRoom.getTrackUrl();
            voiceUrl=classRoom.getVoiceUrl();
            URL url = new URL(trackUrl);
            InputStream is = url.openStream();
            trackData = SerializationUtils.deserialize(is);
            is.close();

            trackData.setVoiceUrl(voiceUrl);
            trackData.setDuration(voiceDuration);
        }
        List<String> imagesUrl = trackData.getImageUrl();
        LOGGER.info("返回页面imagesUrl"+imagesUrl);
        /***解决讲师中心上传的课件转换为图片后在教室内不能加载，由于讲师中心上传的课件在oss上，与客户端存入表的图片url不一致***/
        if (imagesUrl != null && imagesUrl.size() > 0) {
            List<String> imagesUrlNew = new ArrayList<>();
            for (String image : imagesUrl) {
                if (image.contains("https://image.onlyhi.cn/")) {
                    image = image.replace("https://image.onlyhi.cn", "http://courseware.onlyhi.cn");
                    image = image.substring(0, image.indexOf("?"));
                    imagesUrlNew.add(image);
                }
            }
            LOGGER.info("返回页面imagesUrlNew"+imagesUrlNew);
            if(imagesUrlNew.size()>0){
                trackData.setImageUrl(imagesUrlNew);
            }
        }
        /***课程回放列表需要显示有效上课时间段时启用***/
        try {
        /*    String yesterday = DateFormatUtils.format(System.currentTimeMillis() - 24 * 60 * 60 * 1000, "yyyy-MM-dd");
            String dateDir = yesterday.replace("-", "");
            File recordLogsDir = new File(BASEPATH + dateDir);
            Map<Long, Long> commonPeriodMap = CourseUtilExtend.recordCommonPeriod(courseUuid, recordLogsDir, classMateService, classRecordService, classRoomService, cpCourseService);
            trackData.setCommonPeriod(commonPeriodMap);*/
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(Response.success(trackData));
    }

    /**
     * 获取学生和老师的上课轨迹数据
     * 注释原getTrackData()，新增此方法，由于录制mp4后改动较多包括判断条件及上传音频文件到阿里云后取下载路径
     * 调用：测试环境mp4用此url
     *
     * @param request
     * @return
     * @throws Exception
     */
    //@GetMapping("/getTrackDataNew")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001154, methodName = "getTrackData", description = "获取学生和老师的上课轨迹数据")
    public ResponseEntity<Response> getTrackDataNew(CourseUuidRequest request) throws IOException {
        String courseUuid = request.getCourseUuid();
        CpCourse cpCourse = cpCourseService.findByUuid(courseUuid);

        Integer classStatus = cpCourse.getClassStatus();
        Integer studentClassStatus = cpCourse.getStudentClassStatus();
        if (Objects.equals("classStatus", "0") || Objects.equals("studentClassStatus", "0")) {
            LOGGER.info("cpCourse.getClassStatus()={},cpCourse.getStudentClassStatus()={}", classStatus, studentClassStatus);
            return ResponseEntity.ok(Response.error(NO_RECORD_DATA));
        }
        ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);

        if (classRoom == null) {
            LOGGER.info("未进入房间！");
            return ResponseEntity.ok(Response.error(NO_ROOM));
        }
        Integer statisticsStatus = classRoom.getStatisticsStatus();
        //判断是否昨天的录制，且状态未统计（最大可能是录制未成功没有相应录制文件)
   /*     boolean isYesterday=false;
        if(classRoom.getOutRoomTime()!=null && StringUtils.isNotBlank(classRoom.getOutRoomTime().toString())){
           Long outTime=classRoom.getOutRoomTime();
           isYesterday= DateUtil.isYesterday(outTime);
        }*/
        //**有一个情况，老师或学生直接关闭了软件，客户端未触发下课，没走统计方法，也会提示未回放，凌晨1点会定时任务重新统计，第二天也能看到回放，则提示"TEMP_NO_RECORD_DATA*/
    /*   if (isYesterday && statisticsStatus == 0) {
            return ResponseEntity.ok(Response.error(TEMP_NO_RECORD_DATA));
            //针对上面情况，需要有个前置条件，就是没触发下课时，且录制成功，提示“未正常下课，转换中，次日可看”或者check，课程下课时间+拖课时间一到，就触发下课
        }*/
        String trackUrl="";
        String voiceUrl = classRoom.getVoiceUrl();
        String mp4VideoUrl = classRoom.getMp4VideoUrl();
        Integer videoDuration=classRoom.getVideoDuration();
        //Integer voiceDuration = classRoom.getVoiceDuration();
        //|| StringUtils.isBlank(trackUrl) || voiceDuration == null
        if (statisticsStatus == 1 || videoDuration==null) {
            return ResponseEntity.ok(Response.error(NO_RECORD_DATA));
        }
        /**由于历史,课程id_compose轨迹文件是从原轨迹文件序列化后,存url到TrackData对象中的，无法恢复；
         * 为兼容原来的回放能加载序列化的轨迹文件路径，
         * 如果track_oss_key在class_room不为空，是新上传到oss的，根据oss key取轨迹文件url、音频文件url、mp4视频文件url
         **/
        TrackData trackData;
        //根据oss key 取
        if(!Objects.equals(classRoom.getTrackOssKey(),null)){
            OSSClient client = OssUtils.generateOssClient();

            String mp4OssKey=classRoom.getMp4OssKey();
            String mp4OssVideoUrl = OssUtils.generateFileUrl(client,mp4OssKey);

            String trackOssKey=classRoom.getTrackOssKey();
            trackUrl=OssUtils.generateFileUrl(client,trackOssKey);

            client.shutdown();

            //close()必须放到前面再set其它值，否则trackData返回不了voiceUrl等其它字段值
            URL url = new URL(trackUrl);
            InputStream is = url.openStream();
            trackData = SerializationUtils.deserialize(is);
            is.close();

            trackData.setDuration(videoDuration);
            trackData.setMp4Url(mp4OssVideoUrl);
            trackData.setVideoDuration(videoDuration);
            LOGGER.info("mp4OssVideoUrl！"+mp4OssVideoUrl);
            LOGGER.info("trackData.getMp4Url()！"+trackData.getMp4Url());
            //轨迹是在解析原轨迹文件的过程中存到TrackData对象的,不需要set

        }else {
            trackUrl = classRoom.getTrackUrl();
            URL url = new URL(trackUrl);
            InputStream is = url.openStream();
            trackData = SerializationUtils.deserialize(is);
            is.close();

            trackData.setVoiceUrl(voiceUrl);
            trackData.setMp4Url(mp4VideoUrl);
            trackData.setVideoDuration(videoDuration);
            LOGGER.info("mp4VideoUrl！"+mp4VideoUrl);
        }
        LOGGER.info("最后返回！"+trackData.getMp4Url());
        return ResponseEntity.ok(Response.success(trackData));
    }

    /**
     * 查看评价
     *
     * @param request
     * @return
     */
    //@GetMapping("/viewAppraise")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001132, methodName = "viewAppraise", description = "查看评价")
    public ResponseEntity<Response> viewAppraise(ViewAppraiseRequest request) {
        ViewAppraiseVo vo = new ViewAppraiseVo();
        //评价详情
        ClassAppraise classAppraise = classAppraiseService.findByUuid(request.getClassAppraiseUuid());
        if (classAppraise == null) {
            return ResponseEntity.ok(Response.errorCustom("无此评价！"));
        }
        //课程详情 老师、科目、年级
        CourseDto courseDto = cpCourseService.findCourseDetailsByUuid(classAppraise.getCourseUuid());
        vo.setTeacherName(courseDto.getTeacherName());
        vo.setSubject(courseDto.getSubject());
        vo.setGrade(courseDto.getGrade());
        vo.setRemark(classAppraise.getRemark());
        Integer star = classAppraise.getStar();
        vo.setStar(star);
        String classAppraiseStarUuids = classAppraise.getClassAppraiseStarUuids();
        List<String> appraiseStarVoList = new ArrayList<>();
        if (StringUtils.isNotBlank(classAppraiseStarUuids)) {
            if (!classAppraiseStarUuids.contains(",")) {
                ClassAppraiseStar appraiseStar = classAppraiseStarService.findByUuid(classAppraiseStarUuids);
                appraiseStarVoList.add(appraiseStar.getContent());
            } else {
                List<String> classAppraiseStarUuidList = Arrays.asList(classAppraiseStarUuids.split(","));
                appraiseStarVoList = classAppraiseStarService.findContentsByUuids(classAppraiseStarUuidList);
            }
        }
        vo.setList(appraiseStarVoList);

        return ResponseEntity.ok(Response.success(vo));
    }
    /**
     * 查看评价
     * 调用php
     * @param request
     * @return
     */
    @GetMapping("/viewAppraise")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001132, methodName = "viewAppraisePhp", description = "查看评价")
    public ResponseEntity<Response> viewAppraisePhp(ViewAppraiseRequest request) {
        Response phpResponse =null;
        try{
            String phpCacheToken = redisService.getLoginUserCache(request.getToken()).getToken();
            String phpUrl = url.concat("client/course/viewAppraise");
            String classAppraiseUuid=request.getClassAppraiseUuid();
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("classAppraiseUuid", classAppraiseUuid);
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer ".concat(phpCacheToken));
            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);
            if(phpResult==null){
                LOGGER.info("client/course/viewAppraise php 返回空= {}");
            }
            phpResponse = JSON.parseObject(phpResult, Response.class);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(phpResponse);
    }
    @PostMapping("/saveAppraise")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001131, methodName = "saveAppraise", description = "保存评价")
    public ResponseEntity<Response> saveAppraise(SaveAppraiseRequest request) {
        return saveAppraiseComPhp(request);
    }

    @PostMapping("/saveAppraiseByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001130, methodName = "saveAppraiseByJson", description = "保存评价")
    public ResponseEntity<Response> saveAppraiseByJson(@RequestBody SaveAppraiseRequest request) {
        return saveAppraiseComPhp(request);
    }
    /**
     * 保存评价
     * 调用php
     * @param request
     * @return
     */
    private ResponseEntity<Response> saveAppraiseComPhp(SaveAppraiseRequest request) {
        //String appraiseUuid="";
        Response phpResponse =null;
        try{
            Integer star = request.getStar();
            String classAppraiseStarUuids = request.getClassAppraiseStarUuids();
            String remark = request.getRemark();
            String courseUuid = request.getCourseUuid();
            LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
            String phpCacheToken=loginUserCache.getToken();
            String appraiserUuid= loginUserCache.getUserUuid();

            String phpUrl = url.concat("client/course/saveAppraiseByJson");
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("courseUuid", courseUuid);
            paramMap.put("classAppraiseUuid", UUIDUtil.randomUUID2());
            //只有学生评价调
            paramMap.put("appraiserIdentity", "1");
            paramMap.put("appraiserUuid", appraiserUuid);
            if(star!=null){
                paramMap.put("star", star.toString());
            }
            paramMap.put("classAppraiseStarUuids", classAppraiseStarUuids);
            paramMap.put("remark", remark);

            LOGGER.info("client/course/saveAppraiseByJson 传给php={}", JSON.toJSONString(paramMap));

            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headerMap.put("Authorization", "Bearer ".concat(phpCacheToken));
            String phpResult = HttpUtil.sendPost(phpUrl, paramMap, headerMap);

            LOGGER.info("学生保存评价 phpResult={}", phpResult);
            phpResponse = JSON.parseObject(phpResult, Response.class);

         /*   if(!Objects.equals(phpResponse.getCode(), CodeEnum.SUCCESS)){
                return ResponseEntity.ok(phpResponse);
            }else {
                Map<Object,Object> object=(Map<Object, Object>) phpResponse.getData();
                appraiseUuid=(String) object.get("classAppraiseUuid");
            }*/
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(phpResponse);
    }
    /**
     * 保存评价
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> saveAppraiseCom(SaveAppraiseRequest request) {
        int star = request.getStar();
        String classAppraiseStarUuids = request.getClassAppraiseStarUuids();
        String remark = request.getRemark();
        String courseUuid = request.getCourseUuid();
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userUuid = loginUserCache.getUserUuid();
        String userType = loginUserCache.getUserType();
        Integer appraiserIdentity = null;
        if (STUDENT.name().equals(userType)) {
            appraiserIdentity = 1;
        }
        ClassAppraise appraise = classAppraiseService.findByCourseUuid(courseUuid);
        if (appraise != null) {
            return ResponseEntity.ok(Response.error(COURSE_HAS_APPRAISE));
        }
        ClassAppraise classAppraise = new ClassAppraise();
        classAppraise.setCourseUuid(courseUuid);
        classAppraise.setClassAppraiseUuid(UUIDUtil.randomUUID2());
        classAppraise.setAppraiserIdentity(appraiserIdentity);
        classAppraise.setAppraiserUuid(userUuid);
        classAppraise.setStar(star);
        classAppraise.setClassAppraiseStarUuids(classAppraiseStarUuids);
        classAppraise.setRemark(remark);
        classAppraiseService.save(classAppraise);
        return ResponseEntity.ok(Response.success(classAppraise.getClassAppraiseUuid()));
    }
    /**
     * 获取星级评价选项列表
     * 调用php
     * @param request
     * @return
     */
    @GetMapping("/getStarContentList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001129, methodName = "getStarContentListPhp", description = "获取星级评价选项列表")
    public ResponseEntity<Response> getStarContentListPhp(StarRequest request) {
        Response phpResponse =null;
       try {
           Integer star = request.getStar();
           if(star!=null){
               String phpCacheToken = redisService.getLoginUserCache(request.getToken()).getToken();
               String phpUrl = url.concat("client/course/getStarContentList");
               Map<String, String> headerMap = new HashMap<>();
               headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
               headerMap.put("Authorization", "Bearer ".concat(phpCacheToken));

               Map<String, String> paramMap = new HashMap<>();
               if(star!=null){
                   paramMap.put("star",star.toString());
               }
               String phpResult = HttpUtil.sendPost(phpUrl,paramMap,headerMap);
               if(phpResult==null){
                   LOGGER.info("获取星级评价选项列表 php 返回空={}");
               }

               phpResponse = JSON.parseObject(phpResult, Response.class);
               Object object=phpResponse.getData();
               DataVO dataVO=JSON.parseObject(object.toString(),DataVO.class);
               if(Objects.equals(dataVO.getList().size(),0)){
                   //正常情况客户端也不能直接崩溃
                   LOGGER.info("php返回空会导致学生课程记录-提交对老师的评价导致客户端崩溃");
               }
           }
       }catch (Exception e){
           LOGGER.error(e.getMessage());
       }
        return ResponseEntity.ok(phpResponse);
    }
    /**
     * 获取星级评价选项列表
     *
     * @param request
     * @return
     */
    //@GetMapping("/getStarContentList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001129, methodName = "getStarContentList", description = "获取星级评价选项列表")
    public ResponseEntity<Response> getStarContentList(StarRequest request) {
        int star = request.getStar();
        List<ClassAppraiseStar> list = classAppraiseStarService.findByStar(star);
        List<ClassAppraiseStarVo> voList = TransferUtil.transfer(list, ClassAppraiseStarVo.class);
        return ResponseEntity.ok(Response.success(voList));
    }

    /**
     * 上课评价页面
     *
     * @param request
     * @return
     */
    @GetMapping("/classAppraisePage")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001128, methodName = "classAppraisePage", description = "上课评价页面")
    public ResponseEntity<Response> classAppraisePage(CourseUuidRequest request) {
        String courseUuid = request.getCourseUuid();
        CourseDto courseDto = cpCourseService.findCourseDetailsByUuid(courseUuid);
        ClassAppraisePageVo vo = new ClassAppraisePageVo();
        vo.setTeacherName(courseDto.getTeacherName());
        vo.setGrade(courseDto.getGrade());
        vo.setSubject(courseDto.getSubject());
        return ResponseEntity.ok(Response.success(vo));
    }

    @PostMapping("/iosUpdateEndTime")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 1000011, methodName = "iosUpdateEndTime", description = "结束课程，退出房间")
    public ResponseEntity<Response> iosUpdateEndTime(@RequestBody CourseUuidRequest request) throws ParseException {
        return updateEndTimeComPhp(request);
    }

    @PostMapping("/updateEndTime")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 1000012, methodName = "updateEndTime", description = "结束课程，退出房间")
    public ResponseEntity<Response> updateEndTime(CourseUuidRequest request) throws ParseException {
        return updateEndTimeComPhp(request);
    }

    /**
     * 结束课程，退出房间
     * 新增对应客户端测试版本
     * 新调用了updateEndTimeComNew
     *
     * @param request
     * @return
     */
    @PostMapping("/iosUpdateEndTimeNew")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 1000011, methodName = "iosUpdateEndTime", description = "结束课程，退出房间")
    public ResponseEntity<Response> iosUpdateEndTimeNew(@RequestBody CourseUuidRequest request) throws ParseException {
        return updateEndTimeComNew(request);
    }

    /**
     * 结束课程，退出房间
     * 新增对应anroid mp4录制的版本
     * 新调用了updateEndTimeComNew
     *
     * @param request
     * @return
     */
    @PostMapping("/updateEndTimeNew")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 1000012, methodName = "updateEndTime", description = "结束课程，退出房间")
    public ResponseEntity<Response> updateEndTimeNew(CourseUuidRequest request) throws ParseException {
        return updateEndTimeComNew(request);
    }

    /**
     * 结束课程，退出房间
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> updateEndTimeCom(CourseUuidRequest request) {
        try {
            LOGGER.info("结束课程，退出房间，统计课时、将轨迹和音频数据入库...");
            String token = request.getToken();
            LoginUserCache loginUserCache = redisService.getLoginUserCache(token);
            String userType = loginUserCache.getUserType();
            String courseUuid = request.getCourseUuid();
            String userUuid = loginUserCache.getUserUuid();
            long currentTimeMillis = System.currentTimeMillis();
            CpCourse course = cpCourseService.findByUuid(request.getCourseUuid());
            if (course != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String startCourseTime = course.getCourseDate().concat(" ").concat(course.getStartTime()).concat(":00");
                Date date = simpleDateFormat.parse(startCourseTime);
                long startCourseDate = date.getTime() / 1000;
                if (currentTimeMillis / 1000 < startCourseDate) {
                    return ResponseEntity.ok(Response.error(ILLEGAL_OUT_ROOM));
                } else {
                    //LOGGER.info("已过上课时间！");
                }
            }
            int i = cpCourseService.finishClass(courseUuid, userUuid, currentTimeMillis, userType);
            if (i == -1) {
                return ResponseEntity.ok(Response.error(NO_COURSE));
            }
            if (i == -2) {
                return ResponseEntity.ok(Response.error(CANNOT_END_COURSE));
            }
            if (i > 0 && (STUDENT.name().equals(userType) || TEACHER.name().equals(userType))) {
                LOGGER.info("调用client-file方法将轨迹和音频数据入库...");
                String baseUrl = ymlMyConfig.getUploadAddress();
                String url = baseUrl + "/client/course/statisticsTeacherRecordData";
                LOGGER.info("url={}", url);
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("token", token);
                paramMap.put("courseUuid", courseUuid);
                String result = HttpUtil.sendPost(url, paramMap);
                LOGGER.info("result={}", result);
            }
            //下课，房间人数-1
            if (STUDENT.name().equals(userType) || TEACHER.name().equals(userType) || CC.name().equals(userType) || CR.name().equals(userType)) {
                redisService.decr(PERSONOFCOURSE_KEY + courseUuid);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(Response.success());
    }
    /**
     * 结束课程，退出房间
     * 调用php
     * @param request
     * @return
     */
    private ResponseEntity<Response> updateEndTimeComPhp(CourseUuidRequest request) {

            String token = request.getToken();
            LoginUserCache loginUserCache = redisService.getLoginUserCache(token);
            String userType = loginUserCache.getUserType();
            String courseUuid = request.getCourseUuid();
            String userUuid = loginUserCache.getUserUuid();
            long currentTimeMillis = System.currentTimeMillis();
            String phpToken = loginUserCache.getToken();
            CpCourse course=null;

            try {
                String phpUrl =url.concat("client/course/detail");
                CloseableHttpClient client = HttpClients.createDefault();
                HttpPost post=new HttpPost(phpUrl);
                post.setHeader("Content-Type","application/json;charset=utf-8");
                post.setHeader("Authorization", "Bearer ".concat(loginUserCache.getToken()));
                post.setHeader("Accept", "application/json");

                JSONObject param= new JSONObject();
                param.put("courseUuid", courseUuid);

                StringEntity stringEntity = new StringEntity(param.toString());
                post.setEntity(stringEntity);

                //LOGGER.info("param={}",JSON.toJSONString(param));
                //LOGGER.info("post={}",JSON.toJSONString(post));

                CloseableHttpResponse response = client.execute(post);
                HttpEntity entity=response.getEntity();
                String phpResult= EntityUtils.toString(entity,"UTF-8");
                if(phpResult==null){
                    LOGGER.info("结束课程，退出房间 client/course/detail php返回空");
                }

                Response phpResponse = JSON.parseObject(phpResult, Response.class);
                Object object=phpResponse.getData();
                course=JSON.parseObject(object.toString(),CpCourse.class);

            }catch (Exception e){
                LOGGER.error(e.getMessage());
            }

            try {
                if (course != null) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String startCourseTime = course.getCourseDate().concat(" ").concat(course.getStartTime()).concat(":00");
                    Date date = simpleDateFormat.parse(startCourseTime);
                    long startCourseDate = date.getTime() / 1000;
                    if (currentTimeMillis / 1000 < startCourseDate) {
                        return ResponseEntity.ok(Response.error(ILLEGAL_OUT_ROOM));
                    }
                 }
            LOGGER.info("调finishClassPhp");
            int i = cpCourseService.finishClassPhp(courseUuid, userUuid, currentTimeMillis, userType,phpToken);
            if (i == -1) {
                return ResponseEntity.ok(Response.error(NO_COURSE));
            }
            if (i == -2) {
                return ResponseEntity.ok(Response.error(CANNOT_END_COURSE));
            }
            if (i > 0 && (STUDENT.name().equals(userType) || TEACHER.name().equals(userType))) {
                LOGGER.info("调用client-file方法将轨迹和音频数据入库...");
                String baseUrl = ymlMyConfig.getUploadAddress();
                String url = baseUrl + "/client/course/statisticsTeacherRecordData";
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("token", token);
                paramMap.put("courseUuid", courseUuid);
                String result = HttpUtil.sendPost(url, paramMap);
                LOGGER.info("result={}", result);
            }
            //下课，房间人数-1
            if (STUDENT.name().equals(userType) || TEACHER.name().equals(userType) || CC.name().equals(userType) || CR.name().equals(userType)) {
                redisService.decr(PERSONOFCOURSE_KEY + courseUuid);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 结束课程，退出房间
     * 新增此方法主要对应客户端测试版本的mp4下课执行统计
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> updateEndTimeComNew(CourseUuidRequest request) throws ParseException {
        LOGGER.info("结束课程，退出房间，统计课时、将轨迹和音频数据入库...");
        String token = request.getToken();
        LoginUserCache loginUserCache = redisService.getLoginUserCache(token);
        String userType = loginUserCache.getUserType();
        String courseUuid = request.getCourseUuid();
        String userUuid = loginUserCache.getUserUuid();
        long currentTimeMillis = System.currentTimeMillis();
        CpCourse course = cpCourseService.findByUuid(request.getCourseUuid());
        if (course != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String startCourseTime = course.getCourseDate().concat(" ").concat(course.getStartTime()).concat(":00");
            Date date = simpleDateFormat.parse(startCourseTime);
            long startCourseDate = date.getTime() / 1000;
            if (currentTimeMillis / 1000 < startCourseDate) {
                return ResponseEntity.ok(Response.error(ILLEGAL_OUT_ROOM));
            } else if (currentTimeMillis / 1000 > startCourseDate) {
                LOGGER.info("已过上课时间！");
            }
        }
        int i = cpCourseService.finishClass(courseUuid, userUuid, currentTimeMillis, userType);
        if (i == -1) {
            return ResponseEntity.ok(Response.error(NO_COURSE));
        }
        if (i == -2) {
            return ResponseEntity.ok(Response.error(CANNOT_END_COURSE));
        }
        if (i > 0 && (STUDENT.name().equals(userType) || TEACHER.name().equals(userType))) {
            LOGGER.info("调用client-file方法统计课时、将轨迹和音频数据入库...");
            String baseUrl = ymlMyConfig.getUploadAddress();
            String url = baseUrl + "/client/course/statisticsTeacherRecordDataNew";
            LOGGER.info("url={}", url);
            Map<String, String> paramMap = new HashMap<>();
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            paramMap.put("token", token);
            paramMap.put("courseUuid", courseUuid);
            String result = HttpUtil.sendPost(url, paramMap,headerMap);
            LOGGER.info("result={}", result);
        }
        //下课，房间人数-1
        if (STUDENT.name().equals(userType) || TEACHER.name().equals(userType) || CC.name().equals(userType) || CR.name().equals(userType)) {
            redisService.decr(PERSONOFCOURSE_KEY + courseUuid);
        }
        return ResponseEntity.ok(Response.success());
    }

    @PostMapping("/statisticsClassTimeByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 1000015, methodName = "statisticsClassTimeByJson", description = "统计流量时长")
    public ResponseEntity<Response> statisticsClassTimeByJson(@RequestBody CourseRoomRequest request) {
        return statisticsClassTimeComm(request);
    }

    @PostMapping("/statisticsClassTime")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 1000016, methodName = "statisticsClassTime", description = "统计流量时长")
    public ResponseEntity<Response> statisticsClassTime(CourseRoomRequest request) {
        return statisticsClassTimeComm(request);
    }

    /**
     * 统计流量时长
     *
     * @param request
     * @return
     */
    private ResponseEntity<Response> statisticsClassTimeComm(CourseRoomRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userUuid = loginUserCache.getUserUuid();
        String userType = loginUserCache.getUserType();
        String courseUuid = request.getCourseUuid();
        BigDecimal classTime = new BigDecimal(request.getClassTime());
        ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
        if (classRoom == null) {
            return ResponseEntity.ok(Response.error(NO_COURSE));
        }
        //更新流量时长
        classRoom.setUpdateUid(userUuid);

        if (STUDENT.name().equalsIgnoreCase(userType)) {
            BigDecimal studentClassTime = classRoom.getStudentClassTime();
            classRoom.setStudentClassTime(studentClassTime.add(classTime));
        } else if (TEACHER.name().equalsIgnoreCase(userType)) {
            BigDecimal teacherClassTime = classRoom.getTeacherClassTime();
            classRoom.setTeacherClassTime(teacherClassTime.add(classTime));
        }
        classRoomService.update(classRoom);
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 延长课时（仅老师调用）
     *
     * @param request
     * @return
     */
    //@PostMapping("/overtimeCourseByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001172, methodName = "overtimeCourseByJson", description = "延长课时（仅老师调用）"
            , userTypes = TEACHER)
    public ResponseEntity<Response> overtimeCourseByJson(@RequestBody CourseOvertimeRequest request) throws ParseException {
        String courseUuid = request.getCourseUuid();
        int overtime = request.getOvertime();
        CpCourse cpCourse = cpCourseService.findByUuid(courseUuid);
        if (cpCourse == null) {
            return ResponseEntity.ok(Response.error(NO_COURSE));
        }
        if (Objects.equals(cpCourse.getClassStatus(), 1)) {
            return ResponseEntity.ok(Response.error(COURSE_HAS_END));
        }
        ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
        if (classRoom == null) {
            return ResponseEntity.ok(Response.error(COURSE_NO_STARAT));
        }
        String courseDate = cpCourse.getCourseDate();
        String endTime = cpCourse.getEndTime();
        Date date = DateUtils.parseDate(courseDate + " " + endTime, "yyyy-MM-dd HH:mm");
        Date date1 = DateUtils.parseDate(courseDate + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
        if ((date1.getTime() - date.getTime()) < (classRoom.getOvertime() + overtime) * 60 * 1000) {
            return ResponseEntity.ok(Response.error(NO_OVERTIME));
        }
        classRoomService.updateOvertimeByCourseUuid(courseUuid, overtime);
        return ResponseEntity.ok(Response.success());
    }
    /**
     * 延长课时（仅老师调用）
     * 调用php
     * @param request
     * @return
     */
    @PostMapping("/overtimeCourseByJson")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001172, methodName = "overtimeCourseByJsonPhp", description = "延长课时（仅老师调用）", userTypes = TEACHER)
    public ResponseEntity<Response> overtimeCourseByJsonPhp(@RequestBody CourseOvertimeRequest request) throws ParseException {
        String courseUuid = request.getCourseUuid();
        int overtime = request.getOvertime();
        String phpToken = redisService.getLoginUserCache(request.getToken()).getToken();
        Response phpResponse=null;
        CpCourse cpCourse = null;
        try{
            String phpUrl =url.concat("client/course/detail");
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post=new HttpPost(phpUrl);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            post.setHeader("Authorization", "Bearer ".concat(phpToken));

            JSONObject param= new JSONObject();
            param.put("courseUuid", courseUuid);

            StringEntity stringEntity = new StringEntity(param.toString());
            post.setEntity(stringEntity);

            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity=response.getEntity();
            String result= EntityUtils.toString(entity,"UTF-8");
            if(result==null){
                LOGGER.info("延长课时（仅老师调用） client/course/detail 返回空");
            }

            phpResponse = JSON.parseObject(result, Response.class);
            Object object=phpResponse.getData();
            if(object!=null){
                cpCourse=JSON.parseObject(object.toString(),CpCourse.class);
                if (cpCourse == null) {
                    return ResponseEntity.ok(Response.error(NO_COURSE));
                }
                if (Objects.equals(cpCourse.getClassStatus(), 1)) {
                    return ResponseEntity.ok(Response.error(COURSE_HAS_END));
                }
                ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
                if (classRoom == null) {
                    return ResponseEntity.ok(Response.error(COURSE_NO_STARAT));
                }
                String courseDate = cpCourse.getCourseDate();
                String endTime = cpCourse.getEndTime();
                Date date = DateUtils.parseDate(courseDate + " " + endTime, "yyyy-MM-dd HH:mm");
                Date date1 = DateUtils.parseDate(courseDate + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
                if ((date1.getTime() - date.getTime()) < (classRoom.getOvertime() + overtime) * 60 * 1000) {
                    return ResponseEntity.ok(Response.error(NO_OVERTIME));
                }
                classRoomService.updateOvertimeByCourseUuid(courseUuid, overtime);
            }

        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }

        try {
            String phpUrl =url.concat("client/course/overtimeCourseByJson");
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post=new HttpPost(phpUrl);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            post.setHeader("Authorization", "Bearer ".concat(phpToken));

            JSONObject param= new JSONObject();
            param.put("courseUuid", courseUuid);
            param.put("overtime", overtime);

            LOGGER.info("延长课时传给php ={}",JSON.toJSONString(param));
            StringEntity stringEntity = new StringEntity(param.toString());
            post.setEntity(stringEntity);

            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity=response.getEntity();
            String result= EntityUtils.toString(entity,"UTF-8");
            LOGGER.info("延长课时 result ={}",result);
            phpResponse = JSON.parseObject(result, Response.class);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(phpResponse);

    }
    /**
     * 客户端-员工技术支持登陆后课程列表检索
     *
     * @param request
     * @return
     * @author wqz
     */
    @PostMapping("/todayCourse")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001173, methodName = "todayCourseTechnical", description = "技术支持登陆后课程列表", userTypes = TA)
    public ResponseEntity<Response> todayCourseTechnical(@RequestBody TodayCourseVo request) throws ParseException {
        Map<String, Object> map = cpCourseService.selectTodayCourse(request);
        return ResponseEntity.ok(Response.success(map));
    }
}
