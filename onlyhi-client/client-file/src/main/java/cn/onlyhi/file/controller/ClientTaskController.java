package cn.onlyhi.file.controller;

import cn.onlyhi.client.dto.CourseDto;
import cn.onlyhi.client.dto.CoursewareDto;
import cn.onlyhi.client.po.ClassMate;
import cn.onlyhi.client.po.ClassRecord;
import cn.onlyhi.client.po.ClassRecordData;
import cn.onlyhi.client.po.ClassRoom;
import cn.onlyhi.client.po.CpCourse;
import cn.onlyhi.client.service.ClassMateService;
import cn.onlyhi.client.service.ClassRecordDataService;
import cn.onlyhi.client.service.ClassRecordService;
import cn.onlyhi.client.service.ClassRoomService;
import cn.onlyhi.client.service.CoursewareService;
import cn.onlyhi.client.service.CpCourseService;
import cn.onlyhi.client.service.MessageService;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.request.BaseRequest;
import cn.onlyhi.common.util.*;
import cn.onlyhi.file.config.YmlMyConfig;
import cn.onlyhi.file.request.CourseUuidRequest;
import cn.onlyhi.file.request.ExportDataRequest;
import cn.onlyhi.file.util.CourseOssUtil;
import cn.onlyhi.file.util.CourseUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
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
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import static cn.onlyhi.common.constants.Constants.BASEPATH;
import static cn.onlyhi.common.constants.Constants.FILESEPARATOR;
import static cn.onlyhi.common.constants.Constants.RECORD_DIR_ROOT;
import static cn.onlyhi.common.constants.Constants.SENDPHONE;
import static cn.onlyhi.common.enums.ClientEnum.UserType.CC;
import static cn.onlyhi.common.enums.ClientEnum.UserType.CR;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/11/28.
 */
@RestController
@RequestMapping("/client/task")
public class ClientTaskController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientTaskController.class);

    @Autowired
    private ClassMateService classMateService;
    @Autowired
    private ClassRecordService classRecordService;
    @Autowired
    private ClassRoomService classRoomService;
    @Autowired
    private CpCourseService cpCourseService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private YmlMyConfig ymlMyConfig;
    @Autowired
    private CoursewareService coursewareService;
    @Autowired
    private ClassRecordDataService classRecordDataService;
    @Autowired
    private HttpServletResponse httpServletResponse;
    @Value("${phpStaging.url}")
    private String url;

    /**
     * 统计昨天的数据(音频和轨迹数据):老师未下课统计的数据和学生和cc、cr的音频数据
     * 上传oss 调CourseOssUtil.statisticsTeacherRecordDataByThread
     * 及 CourseOssUtil.statisticsSCRecordDataByThread
     * @param request
     * @return
     * @throws Exception
     */
    //@GetMapping("/statisticsStudentRecordData")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001178, methodName = "statisticsStudentRecordData", description = "上课异常的课程进行音频、轨迹、课时统计", checkToken = false)
    public ResponseEntity<Response> statisticsStudentRecordData(BaseRequest request) throws Exception {
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
                        LOGGER.info("courseUuidList_sc.add(courseUuid)");
                        courseUuidList_sc.add(courseUuid);
                    }
                }
                List<ClassRecordData> classRecordDataList = classRecordDataService.findByClassRoomUuidAndRecordRole(classRoomUuid, 0);
                if (classRecordDataList == null || classRecordDataList.size() == 0) {
                    courseUuidList_st.add(courseUuid);
                    LOGGER.info("courseUuidList_st.add(courseUuid)");
                }
                List<ClassRecord> classRecordList = classRecordService.findByClassRoomUuid(classRoomUuid);
                if (classRecordList == null || classRecordList.size() == 0) {
                    courseUuidList_ks.add(courseUuid);
                    LOGGER.info("courseUuidList_ks.add(courseUuid)");
                }
            }
        }
        LOGGER.info("未统计的学生与老师回放数据courseUuidList_st={}", courseUuidList_st);
        LOGGER.info("需统计的学生与cc或cr的音频数据={}", courseUuidList_sc);
        LOGGER.info("需统计的学生与老师的课时和上课进出记录courseUuidList_ks={}", courseUuidList_ks);

        if (courseUuidList_st.size() > 0) {
            for (String courseUuid : courseUuidList_st) {
                LOGGER.info("统计学生与老师音频数据.");
                //检查老师学生是否共同在线上课
                Integer comTotalTime = CourseUtil.checkAllInClass(courseUuid, recordLogsDir, classMateService, classRecordService, classRoomService, cpCourseService);
                LOGGER.info("comTotalTime={}", comTotalTime);
                //统计的学生与老师回放数据
                CourseUtil.statisticsTeacherRecordDataByThread(courseUuid, classRoomService, classMateService, recordLogsDir, messageService, classRecordDataService, RECORD_DIR_ROOT, ymlMyConfig, coursewareService, comTotalTime);
            }
        }
        if (courseUuidList_sc.size() > 0) {
            for (String courseUuid : courseUuidList_sc) {
                LOGGER.info("统计学生与cc或cr的音频数据.");
                //检查老师学生是否共同在线上课
                Integer comTotalTime = CourseUtil.checkAllInClass(courseUuid, recordLogsDir, classMateService, classRecordService, classRoomService, cpCourseService);
                LOGGER.info("comTotalTime={}", comTotalTime);
                //统计的学生与cc或cr的音频数据
                CourseUtil.statisticsSCRecordDataByThread(courseUuid, classRoomService, classMateService, recordLogsDir, messageService, classRecordDataService, RECORD_DIR_ROOT, ymlMyConfig,comTotalTime);
            }
        }
        if (courseUuidList_ks.size() > 0) {
            for (String courseUuid : courseUuidList_ks) {
                LOGGER.info("统计学生与老师的课时和上课进出记录.");
                CourseUtil.parseAndStatisticsClassRecordDBDataByThread(courseUuid, recordLogsDir, classMateService, classRecordService, classRoomService, cpCourseService, messageService);
            }
        }
        return ResponseEntity.ok(Response.success());
    }
    /**
     * 调用php
     * 手动恢复进出记录临时加参数@RequestParam("restoreDate") String restoreDate
     * @return
     * @throws Exception
     */
    @GetMapping("/statisticsStudentRecordData")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001178, methodName = "statisticsStudentRecordDataPhp", description = "上课异常的课程进行音频、轨迹、课时统计", checkToken = false)
    public ResponseEntity<Response> statisticsStudentRecordDataPhp() throws Exception {
        String yesterday = DateFormatUtils.format(System.currentTimeMillis() - 24 * 60 * 60 * 1000, "yyyy-MM-dd");
        String dateDir = yesterday.replace("-", "");
        File recordLogsDir = new File(BASEPATH + dateDir);
        if (!recordLogsDir.exists()) {
            throw new Exception("音频录制目录不存在！");
        }
        Response phpResponse=null;
        List<CpCourse> cpCourseList =new ArrayList<>();
        String phpUrl = url.concat("client/course/info");
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post=new HttpPost(phpUrl);
            //客户端发送给服务器端的数据格式
            post.setHeader("Content-Type","application/json;charset=utf-8");

            JSONObject param= new JSONObject();
            param.put("yesterday", yesterday);
            StringEntity stringEntity = new StringEntity(param.toString());
            post.setEntity(stringEntity);
            CloseableHttpResponse response = client.execute(post);

            HttpEntity entity=response.getEntity();
            String result= EntityUtils.toString(entity,"UTF-8");
            //LOGGER.info("client/course/info result ={}",result);
            phpResponse = JSON.parseObject(result, Response.class);
            Object object=phpResponse.getData();
            if(object!=null){
                cpCourseList =JSON.parseArray(object.toString(),CpCourse.class);
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }

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
        LOGGER.info("未统计的学生与老师回放数据courseUuidList_st={}", courseUuidList_st);
        LOGGER.info("需统计的学生与cc或cr的音频数据={}", courseUuidList_sc);
        LOGGER.info("需统计的学生与老师的课时和上课进出记录courseUuidList_ks={}", courseUuidList_ks);

        if (courseUuidList_st.size() > 0) {
            for (String courseUuid : courseUuidList_st) {
                //统计的学生与老师回放数据
                CourseUtil.statisticsTeacherRecordDataByThreadPhp(courseUuid, classRoomService, classMateService, recordLogsDir, messageService, classRecordDataService, RECORD_DIR_ROOT, ymlMyConfig, coursewareService, 0);
            }
        }
        if (courseUuidList_sc.size() > 0) {
            for (String courseUuid : courseUuidList_sc) {
                //统计的学生与cc或cr的音频数据
                CourseUtil.statisticsSCRecordDataByThreadPhp(courseUuid, classRoomService, classMateService, recordLogsDir, messageService, classRecordDataService, RECORD_DIR_ROOT, ymlMyConfig,0);
            }
        }
        if (courseUuidList_ks.size() > 0) {
            for (String courseUuid : courseUuidList_ks) {
                //统计学生与老师的课时和上课进出记录
                CourseUtil.parseAndStatisticsClassRecordDBDataByThreadPhp(courseUuid, recordLogsDir, classMateService, classRecordService, classRoomService, cpCourseService, messageService);
            }
        }
        return ResponseEntity.ok(Response.success());
    }
    /**
     * 解析单个课程的老师和学生的轨迹数据
     *
     * @param request
     * @return
     */
    @PostMapping("/parseTrackDataAndSave")
    public ResponseEntity<Response> parseTrackDataAndSave(CourseUuidRequest request) {
        String courseUuid = request.getCourseUuid();
        LOGGER.info("*****课程{}保存音频和解析轨迹文件线程开始！*****", courseUuid);
        ClassRoom room = classRoomService.findByCourseUuid(courseUuid);

        ClassRoom classRoom = new ClassRoom();
        classRoom.setCourseUuid(courseUuid);
        try {
            List<ClassRecordData> classRecordDataList1 = classRecordDataService.findByClassRoomUuidAndRecordRole(room.getClassRoomUuid(), 0);
            if (classRecordDataList1 != null && classRecordDataList1.size() != 0) {
                CourseUtil.uniteRecord(classRecordDataList1, courseUuid, RECORD_DIR_ROOT, ymlMyConfig, classRoomService, coursewareService);
            }
            classRoom.setStatisticsStatus(2);
        } catch (Exception e) {
            classRoom.setStatisticsStatus(1);
            LOGGER.info("*****课程{}保存音频和解析轨迹文件异常！*****", courseUuid, e);
            try {
                messageService.sendExceptionMessage(SENDPHONE, courseUuid, e.getMessage());
            } catch (Exception e1) {
                LOGGER.error(e1.getMessage());
            }
            LOGGER.error(e.getMessage());
        }
        classRoomService.updateByCourseUuid(classRoom);
        LOGGER.info("*****课程{}保存音频和解析轨迹文件结束！*****", courseUuid);
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 统计上传课件及转换时间和上课数统计
     *
     * @param request
     * @return
     */
    @GetMapping("/exportData")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001181, methodName = "exportData", description = "统计上传课件及转换时间和上课数统计", checkToken = false)
    public ResponseEntity<Response> exportData(ExportDataRequest request) throws Exception {
        String exportDate = request.getExportDate();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (!DateUtil.isLessTomorrow(dateFormat.parse(exportDate))) {
            return ResponseEntity.ok(Response.errorCustom("导出日期不要大于今天!"));
        }
        String exportEndDate = dateFormat.format(new Date(dateFormat.parse(exportDate).getTime() + 24 * 60 * 60 * 1000));
        //课件信息
        List<CoursewareDto> coursewareDtoList = coursewareService.findTeacherCouresewaresByCreateTime(exportDate, exportEndDate);
        //转换时间
        String toPdfCmd = "cat /www/service/logs/client-file/info." + exportDate + ".log|grep 转pdf时间";
        String toImageCmd = "cat /www/service/logs/client-file/info." + exportDate + ".log|grep 转image时间";
        if (dateFormat.format(new Date()).equals(exportDate)) {
            toPdfCmd = "cat /www/service/logs/client-file/info.log|grep 转pdf时间";
            toImageCmd = "cat /www/service/logs/client-file/info.log|grep 转image时间";
        }
        String[] toPdfCmds = {"/bin/sh", "-c", toPdfCmd};
        String[] toImageCmds = {"/bin/sh", "-c", toImageCmd};
        Map<String, String> toPdfMap = getCoursewareConverInfo(toPdfCmds, exportDate);
        Map<String, String> toImageMap = getCoursewareConverInfo(toImageCmds, exportDate);

        List<Map<String, String>> coursewareMapList = new ArrayList<>();
        Map<String, String> coursewareMap = new HashMap<>();
        for (CoursewareDto dto : coursewareDtoList) {
            coursewareMap = new HashMap<>();
            coursewareMap.put("uploadTime", dateFormat.format(dto.getCreateTime()));
            coursewareMap.put("teacherName", dto.getTeacherName());
            if (Objects.equals(dto.getConverStatus(),0)) {
                coursewareMap.put("converStatus", "转换未开始");
            } else if (Objects.equals(dto.getConverStatus(),1)) {
                coursewareMap.put("converStatus", "转换中");
            } else if (Objects.equals(dto.getConverStatus(),2)) {
                coursewareMap.put("converStatus", "转换成功");
            } else if (Objects.equals(dto.getConverStatus(),3)) {
                coursewareMap.put("converStatus", "转换失败");
            }
            coursewareMap.put("originalName", dto.getOriginalName());
            String coursewareUrl = dto.getCoursewareUrl();
            if (StringUtils.isNotEmpty(coursewareUrl)) {
                String coursewareName = coursewareUrl.substring(coursewareUrl.lastIndexOf(FILESEPARATOR) + 1, coursewareUrl.lastIndexOf("."));
                String converPdfTime = toPdfMap.getOrDefault(coursewareName, "");
                String converImageTime = toImageMap.getOrDefault(coursewareName, "");
                coursewareMap.put("converPdfTime", converPdfTime);
                coursewareMap.put("converImageTime", converImageTime);
            }
            coursewareMapList.add(coursewareMap);
        }
        Map<String, String> coursewareHeadMap = new LinkedHashMap<>();
        coursewareHeadMap.put("uploadTime", "上传时间");
        coursewareHeadMap.put("teacherName", "教师姓名");
        coursewareHeadMap.put("converStatus", "转换状态");
        coursewareHeadMap.put("originalName", "上传课件");
        coursewareHeadMap.put("converPdfTime", "转pdf时间");
        coursewareHeadMap.put("converImageTime", "转image时间");

        //上课数信息
        List<CourseDto> courseDtoList = cpCourseService.findByCourseDate(exportDate);
        List<Map<String, String>> courseMapList = new ArrayList<>();
        Map<String, String> courseMap = new HashMap<>();
        for (CourseDto dto : courseDtoList) {
            courseMap = new HashMap<>();
            courseMap.put("courseDate", dto.getCourseDate());
            if (Objects.equals(dto.getCourseType(), 0)) {
                courseMap.put("courseType", "测评课");
            } else if (Objects.equals(dto.getCourseType(),1)) {
                courseMap.put("courseType", "正式课");
            } else if (Objects.equals(dto.getCourseType(),2)) {
                courseMap.put("courseType", "调试课");
            }
            courseMap.put("teacherName", dto.getTeacherName());
            courseMap.put("studentName", dto.getStudentName());
            courseMap.put("realLength", String.valueOf(dto.getRealLength()));
            courseMap.put("leadsLength", String.valueOf(dto.getLeadsLength()));
            courseMap.put("teacherLength", String.valueOf(dto.getTeacherLength()));
            courseMap.put("voiceUrl", dto.getVoiceUrl());
            courseMapList.add(courseMap);
        }
        Map<String, String> courseHeadMap = new LinkedHashMap<>();
        courseHeadMap.put("courseDate", "课程日期");
        courseHeadMap.put("courseType", "课程类型");
        courseHeadMap.put("teacherName", "教师姓名");
        courseHeadMap.put("studentName", "学生姓名");
        courseHeadMap.put("realLength", "共同在线时长");
        courseHeadMap.put("leadsLength", "学生在线时长");
        courseHeadMap.put("teacherLength", "教师在线时长");
        courseHeadMap.put("voiceUrl", "音频数据");


        Map<Map<String, String>, List<Map<String, String>>> map = new LinkedHashMap<>();
        map.put(coursewareHeadMap, coursewareMapList);
        map.put(courseHeadMap, courseMapList);

        File saveFile = new File("/www/clientfile/uploadPath/exportData", "上传课件数及上课数统计_" + exportDate + ".xlsx");
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }
        List<String> sheetNames = Arrays.asList("上传课件统计", "上课统计");

        ExportUtil.wirteExcelFile2(map, sheetNames, saveFile);
        if (request.isDownloadFlag()) {
            //下载saveFile
            String fileUrl = saveFile.getPath().replace("/www/clientfile/", ymlMyConfig.getUploadAddress());
            DownloadUtil.download(httpServletResponse, fileUrl, saveFile.getName());
        }
        return ResponseEntity.ok(Response.success());
    }

    /**
     * @param cmd
     * @return Map<String, String> 文件名/转换时间
     * @throws Exception
     */
    private Map<String, String> getCoursewareConverInfo(String[] cmd, String exportDate) {
        Map<String, String> map = new HashMap<>();
        CmdExecResult cmdExecResult = null;
        try {
            cmdExecResult = CmdExecuteUtil.exec(cmd, true);
            if (cmdExecResult.isExecStatus()) {
                String successInfo = cmdExecResult.getSuccessInfo();
                String[] infos = successInfo.split(System.lineSeparator());
                for (String info : infos) {
                    if (info.contains("cat") || !info.contains(exportDate)) {
                        continue;
                    }
                    String fileName = info.substring(info.indexOf("文件") + 2, info.lastIndexOf("."));
                    String converTime = info.substring(info.lastIndexOf(":") + 1, info.lastIndexOf("ms"));
                    map.put(fileName, converTime);
                }
            }
        } catch (Exception e) {
        }
        return map;
    }
}
