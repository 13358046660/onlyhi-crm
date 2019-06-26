package cn.onlyhi.file.controller;

import cn.onlyhi.client.po.*;
import cn.onlyhi.client.service.*;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.request.BaseRequest;
import cn.onlyhi.common.util.Response;
import cn.onlyhi.file.config.AgoraConfig;
import cn.onlyhi.file.config.YmlMyConfig;
import cn.onlyhi.file.request.UploadTrackFileRequest;
import cn.onlyhi.file.util.CourseOssUtil;
import cn.onlyhi.file.util.CourseUtil;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static cn.onlyhi.common.constants.Constants.*;
import static cn.onlyhi.common.enums.ClientEnum.UserType.*;

/**
 * @Author wqz
 * 测试环境、应用录制mp4文件存到磁盘或上传到oss切换
 * <p>
 * Created by wqz on 2018/7/1.
 */
@RestController
@RequestMapping("/client/course")
public class ClientCourseOssController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCourseOssController.class);

    @Autowired
    private ClassRoomService classRoomService;
    @Autowired
    private CoursewareService coursewareService;
    @Autowired
    private YmlMyConfig ymlMyConfig;
    @Autowired
    private ClassMateService classMateService;

    @Autowired
    private ClassRecordDataService classRecordDataService;
    @Autowired
    private LeadsService leadsService;
    @Autowired
    protected RedisService redisService;
    @Autowired
    private AgoraConfig agoraConfig;
    @Autowired
    private ClassRecordService classRecordService;
    @Autowired
    private CpCourseService cpCourseService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private HttpServletResponse httpServletResponse;
    private static ConcurrentLinkedQueue<String> cmdQueue = new ConcurrentLinkedQueue<>();

    /**
     * 学生或老师点下课，统计老师和学生的回放数据(音频和轨迹数据)
     * 测试环境录制mp4(/www/clientfile/uploadPath/recordDir/20180703)上传mp4原文件、合并后的_av.mp4文件、合并的轨迹文件到阿里云
     * 提供：测试环境上传oss用/statisticsTeacherRecordDataOss 且调CourseOssUtil.statisticsTeacherRecordDataByThreadNew
     * 测试环境需要 存放磁盘用/statisticsTeacherRecordDataNew 且调CourseUtil.statisticsTeacherRecordDataByThreadNew
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/statisticsTeacherRecordDataOss")
    public ResponseEntity<Response> statisticsTeacherRecordDataMp4(UploadTrackFileRequest request) throws Exception {
        LOGGER.info(" 学生或老师下课，统计老师和学生的回放数据(音频和轨迹数据)");
        {
            String courseUuid = request.getCourseUuid();
            String dateDir = DateFormatUtils.format(new Date(), "yyyyMMdd");
            File recordLogsDir = new File(BASEPATH, dateDir);
            if (!recordLogsDir.exists()) {
                LOGGER.info("courseUuid={}：音频录制目录不存在！", courseUuid);
                return ResponseEntity.ok(Response.success());
            }
            if (cmdQueue.contains(courseUuid)) {
                String info = "courseUuid={" + courseUuid + "}已统计!";
                LOGGER.info(info);
                return ResponseEntity.ok(Response.errorCustom(info));
            }
            cmdQueue.add(courseUuid);
            CourseOssUtil.statisticsTeacherRecordDataByThreadNew(courseUuid, cpCourseService, classRoomService, classMateService, recordLogsDir, messageService, classRecordDataService, RECORD_DIR_ROOT, ymlMyConfig, coursewareService);
            return ResponseEntity.ok(Response.success());
        }
    }

    /**
     * 未正常触发下课，定时任务统计
     * 统计昨天的数据(音频和轨迹数据):老师未下课统计的数据和学生和cc、cr的音频数据
     * 测试环境录制mp4(/www/clientfile/uploadPath/recordDir/20180703)上传mp4原文件、合并后的_av.mp4文件、合并的轨迹文件到阿里云
     * 变动：statisticsTeacherRecordDataByThread调用了新方法uniteRecordNew（录制加入了视频，需要再生成mp4文件）
     * 提供：测试环境需要上传oss用/statisticsTeacherRecordDataOss
     * 且调CourseOssUtil.statisticsTeacherRecordDataByThreadNew及
     * CourseOssUtil.statisticsSCRecordDataByThreadNew
     * 存放磁盘用/statisticsTeacherRecordDataNew
     * 且调CourseUtil.statisticsTeacherRecordDataByThreadNew及
     * CourseUtil.statisticsSCRecordDataByThreadNew
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/statisticsStudentRecordDataOss")
    public ResponseEntity<Response> statisticsStudentRecordDataMp4() throws Exception {
        String yesterday = DateFormatUtils.format(System.currentTimeMillis() - 24 * 60 * 60 * 1000, "yyyy-MM-dd");
        String dateDir = yesterday.replace("-", "");
        File recordLogsDir = new File(BASEPATH + dateDir);
        if (!recordLogsDir.exists()) {
            throw new Exception("音频录制目录不存在！");
        }
        List<CpCourse> cpCourseList = cpCourseService.findStartedCourseByCourseDate(yesterday);
        //未统计的学生与老师回放数据
        List<String> courseUuidList_st = new ArrayList<>();
        //需统计的学生与cc或cr的音频数据
        List<String> courseUuidList_sc = new ArrayList<>();
        //需统计的学生与老师的课时和上课进出记录
        List<String> courseUuidList_ks = new ArrayList<>();
        for (CpCourse cpCourse : cpCourseList) {
            String courseUuid = cpCourse.getUuid();
            ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
            if (classRoom != null) {
                String classRoomUuid = classRoom.getClassRoomUuid();
                List<ClassMate> classMateList = classMateService.findByClassRoomUuid(classRoomUuid);
                for (ClassMate classMate : classMateList) {
                    String userType = classMate.getUserType();
                    if (CC.name().equals(userType) || CR.name().equals(userType)) {
                        courseUuidList_sc.add(courseUuid);
                    }
                }
                List<ClassRecordData> classRecordDataList = classRecordDataService.findByClassRoomUuidAndRecordRole(classRoomUuid, 0);
                if (classRecordDataList == null || classRecordDataList.size() == 0) {
                    courseUuidList_st.add(courseUuid);
                }
                List<ClassRecord> classRecordList = classRecordService.findByClassRoomUuid(classRoomUuid);
                if (classRecordList == null || classRecordList.size() == 0) {
                    courseUuidList_ks.add(courseUuid);
                }
            }
        }
        LOGGER.info("courseUuidList_st={}", courseUuidList_st);
        LOGGER.info("courseUuidList_sc={}", courseUuidList_sc);
        LOGGER.info("courseUuidList_ks={}", courseUuidList_ks);

        if (courseUuidList_st.size() > 0) {
            for (String courseUuid : courseUuidList_st) {
                //统计的学生与老师回放数据
                CourseOssUtil.statisticsTeacherRecordDataByThreadNew(courseUuid, cpCourseService, classRoomService, classMateService, recordLogsDir, messageService, classRecordDataService, RECORD_DIR_ROOT, ymlMyConfig, coursewareService);
            }
        }
        if (courseUuidList_sc.size() > 0) {
            for (String courseUuid : courseUuidList_sc) {
                //统计的学生与cc或cr的音频数据
                CourseOssUtil.statisticsSCRecordDataByThreadNew(courseUuid, classRoomService, classMateService, recordLogsDir, messageService, classRecordDataService, RECORD_DIR_ROOT, ymlMyConfig);
            }
        }
        if (courseUuidList_ks.size() > 0) {
            for (String courseUuid : courseUuidList_ks) {
                CourseUtil.parseAndStatisticsClassRecordDBDataByThread(courseUuid, recordLogsDir, classMateService, classRecordService, classRoomService, cpCourseService, messageService);
            }
        }
        return ResponseEntity.ok(Response.success());
    }
}
