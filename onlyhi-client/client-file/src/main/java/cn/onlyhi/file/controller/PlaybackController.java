package cn.onlyhi.file.controller;

import cn.onlyhi.client.dto.ClassRecordDBData;
import cn.onlyhi.client.dto.TrackData;
import cn.onlyhi.client.po.*;
import cn.onlyhi.client.service.*;
import cn.onlyhi.common.util.*;
import cn.onlyhi.file.config.YmlMyConfig;
import cn.onlyhi.file.request.UploadTrackFileRequest;
import cn.onlyhi.file.util.CourseUtil;
import cn.onlyhi.file.util.StatisticsClassTimeResult;
import cn.onlyhi.file.vo.CourseDateVo;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static cn.onlyhi.common.constants.Constants.*;
import static cn.onlyhi.common.enums.ClientEnum.UserType.CC;
import static cn.onlyhi.common.enums.ClientEnum.UserType.CR;
import static cn.onlyhi.common.util.ClientUtil.str2ms;
import static cn.onlyhi.common.util.CmdExecuteUtil.exec;
import static cn.onlyhi.common.util.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.copyFile;

/**
 * @Author wqz
 * <p>
 * 只要录制正常的，没有回放的，手动更新class_room生成voice_url，track_url，voice_duration正常回放
 * 不改原方法的原则，所以会冗余重复方法较多，在原CourseUtil对应的方法上去掉了线程执行，手动不需要线程执行
 * Created by wqz on 2018/07/10.
 */
@RestController
@RequestMapping("/client/task")
public class PlaybackController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaybackController.class);

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

    /**
     * 改动前：老师未点击下课，每天凌晨1:00点执行定时任务/statisticsStudentRecordData，统计昨天的数据(音频和轨迹数据):老师未下课统计的数据和学生和cc、cr的音频数据
     * 改动后：手动统计任意时间的数据(音频和轨迹数据):老师未下课统计的数据和学生和cc、cr的音频数据
     * 如果目录/www/clientfile/uploadPath/recordDir/20180707没有生成解析后的轨迹文件，手动正常解析
     *
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/statisticsStudentPlayback")
    public ResponseEntity<Response> statisticsPlayback(UploadTrackFileRequest request) throws Exception {
        //CourseUtil.uniteRecord(null,courseUuid,RECORD_DIR_ROOT,ymlMyConfig,classRoomService,coursewareService);
        //String courseDate = DateFormatUtils.format(request.getCourseDate(), "yyyy-MM-dd");
        String dateDir = request.getRestoreDate().replace("-", "");
        File recordLogsDir = new File(BASEPATH + dateDir);
        if (!recordLogsDir.exists()) {
            throw new Exception("音频录制目录不存在！");
        }
        List<CpCourse> cpCourseList = cpCourseService.findStartedCourseByCourseDateNew(request.getRestoreDate(), request.getCourseUuid());
        //未统计的学生与老师回放数据
        List<String> courseUuidList_st = new ArrayList<>();
        //需统计的学生与cc或cr的音频数据
        List<String> courseUuidList_sc = new ArrayList<>();
        //需统计的学生与老师的课时和上课进出记录
        List<CourseDateVo> courseUuidList_ks = new ArrayList<>();
        for (CpCourse cpCourse : cpCourseList) {
            LOGGER.info("手动更新的课程日期." + cpCourse.getCourseDate());
            CourseDateVo courseDateVo = new CourseDateVo();
            String courseUuid = cpCourse.getUuid();
            ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
            if (classRoom != null) {
                LOGGER.info("进入房间时间." + classRoom.getEnterRoomTime());
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
                    recordLogsDir = new File(BASEPATH + dateDir);
                    courseDateVo.setCourseId(courseUuid);
                    courseDateVo.setRecordId(classRoom.getRecordId());
                    courseUuidList_ks.add(courseDateVo);
                }
            }
        }
        LOGGER.info("未统计的学生与老师回放数据courseUuidList_st={}", courseUuidList_st);
        LOGGER.info("需统计的学生与老师的课时和上课进出记录courseUuidList_ks={}", courseUuidList_ks);

        if (courseUuidList_st.size() > 0) {
            for (String courseUuid : courseUuidList_st) {
                //统计的学生与老师回放数据
                LOGGER.info("统计的学生与老师回放数据.");
                //检查老师学生是否共同在线上课
                Integer comTotalTime = CourseUtil.checkAllInClass(courseUuid, recordLogsDir, classMateService, classRecordService, classRoomService, cpCourseService);
                LOGGER.info("comTotalTime={}", comTotalTime);
                statisticsTeacherRecordDataByThread(courseUuid, cpCourseService, classRoomService, classMateService, recordLogsDir, messageService, classRecordDataService, RECORD_DIR_ROOT, ymlMyConfig, coursewareService, comTotalTime);
            }
        }
        if (courseUuidList_ks.size() > 0) {
            for (CourseDateVo vo : courseUuidList_ks) {
                LOGGER.info("解析客户端进出房间记录，统计课时.");
                parseAndStatisticsClassRecordDBDataByThread(vo.getCourseId(), recordLogsDir, classMateService, classRecordService, classRoomService, cpCourseService, messageService,vo.getRecordId());
            }
        }
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 统计老师和学生的回放数据(音频和轨迹数据)
     *
     * @param courseUuid
     * @param cpCourseService
     * @param classRoomService
     * @param classMateService
     * @param recordLogsDir
     * @param messageService
     * @param classRecordDataService
     * @param recordDir
     * @param ymlMyConfig
     * @param coursewareService
     */
    public static void statisticsTeacherRecordDataByThread(String courseUuid, CpCourseService cpCourseService, ClassRoomService classRoomService, ClassMateService classMateService, File recordLogsDir, MessageService messageService, ClassRecordDataService classRecordDataService, String recordDir, YmlMyConfig ymlMyConfig, CoursewareService coursewareService, Integer comTotalTime) {
        LOGGER.info("***** 统计老师和学生的回放数据(音频和轨迹数据)");
        ClassRoom room = classRoomService.findByCourseUuid(courseUuid);
        if (room != null) {
            List<ClassMate> classMateList = classMateService.findByClassRoomUuid(room.getClassRoomUuid());
            if (classMateList != null && classMateList.size() > 1) {
                //aac文件转成m4a文件和flac，并获取m4a文件时长，并保存到数据库中
                List<ClassRecordData> classRecordDataList = classRecordDataService.findByClassRoomUuid(room.getClassRoomUuid());

                if (classRecordDataList == null || classRecordDataList.size() == 0) {
                    LOGGER.info("*****课程{}保存音频和解析轨迹文件线程开始！*****", courseUuid);
                    ClassRoom classRoom = new ClassRoom();
                    classRoom.setCourseUuid(courseUuid);
                    try {
                        LOGGER.info("调statisticsTeacherVoiceData.");
                        statisticsTeacherVoiceData(courseUuid, recordLogsDir, recordDir, ymlMyConfig, classRoomService, classRecordDataService, comTotalTime);
                        List<ClassRecordData> classRecordDataList1 = classRecordDataService.findByClassRoomUuidAndRecordRole(room.getClassRoomUuid(), 0);
                        if (classRecordDataList1 != null && classRecordDataList1.size() != 0) {
                            LOGGER.info("调uniteRecord.");
                            uniteRecord(classRecordDataList1, courseUuid, recordDir, ymlMyConfig, classRoomService, coursewareService);
                        }
                        classRoom.setStatisticsStatus(2);
                    } catch (Exception e) {
                        classRoom.setStatisticsStatus(1);
                        LOGGER.info("*****课程{}保存音频和解析轨迹文件异常！*****", courseUuid, e);
                        try {
                            messageService.sendExceptionMessage(SENDPHONE, courseUuid, e.getMessage());
                        } catch (Exception e1) {
                            LOGGER.error(e.toString(), e);
                        }
                        LOGGER.error(e.toString(), e);
                    }
                    classRoomService.updateByCourseUuid(classRoom);
                }
            }
        }
    }

    /**
     * 统计学生和老师的音频和轨迹数据并入库
     *
     * @param courseUuid
     * @param recordLogsDir
     * @param recordDir
     * @param ymlMyConfig
     * @param classRoomService
     * @param classRecordDataService
     * @throws Exception
     */
    private static void statisticsTeacherVoiceData(String courseUuid, File recordLogsDir, String recordDir, YmlMyConfig ymlMyConfig, ClassRoomService classRoomService, ClassRecordDataService classRecordDataService, Integer comTotalTime) throws Exception {
        LOGGER.info("统计学生和老师的音频和轨迹数据并入库.");
        ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
        if (classRoom == null) {
            throw new Exception("进入房间信息为空！");
        }
        String pathFileName = courseUuid + ".path";
        File[] listFiles = recordLogsDir.listFiles();
        String content = readFileToString(listFiles, pathFileName);
        //异常前提：老师学生共同在线的（上过课的）没有录制文件才提示；没有共同上过课的不再提示：
        //LOGGER.info("comTotalTime:" + comTotalTime);
        if (comTotalTime > 0 && StringUtils.isBlank(content)) {
            ///www/hktRecord/mylogs/20180806 没有课程id.path文件，都来上课了，
            //可能是其它异常导致的未正常生成课程id.path文件,/www/hktRecord/recordDir/20180806 也没有录制原始文件
            throw new Exception("老师或学生共同在线，音频录制目录不存在！" + courseUuid);
        }
        if (StringUtils.isBlank(content)) {
            ///www/hktRecord/mylogs/20180806 没有课程id.path文件
            throw new Exception("老师或学生未共同在线！" + courseUuid);
        }
        String dateDir = recordLogsDir.getPath().substring(recordLogsDir.getParentFile().getPath().length() + 1);
        String webSaveDir = recordDir + FILESEPARATOR + dateDir;
        String saveDir = BASESAVEPATH + dateDir;
        List<ClassRecordData> classRecordDataList = new ArrayList<>();
        String[] contents = content.split("\\n");
        for (String str : contents) {
            String[] strs = str.split(":");
            if (strs.length != 4) {
                throw new Exception("path文件数据异常！");
            }
            String recordRole = strs[0];
            if (recordRole.length() != 1) {
                throw new Exception("path文件第一位数据[" + recordRole + "]异常！");
            }
            if ("0".equals(recordRole)) {
                LOGGER.info("只统计学生和老师的音频和轨迹");
                String aacDirPath = strs[2];
                String trackPath = strs[3];
                File trackFile = new File(trackPath);
                String trackSaveFilePath = null;
                if (trackFile.exists()) {
                    String trackFileName = trackFile.getName();
                    String saveFileName = trackFileName.substring(0, trackFileName.lastIndexOf(".db")) + "_server.db";
                    File trackSaveFile = new File(saveDir, saveFileName);
                    //文件copy到新目录，copy不可少；从/www/hktRecord/recordDir/20180810/f712c489-5f25-4cd1-bc36-6621411067ec10_005909到/www/recordDir/20180810
                    copyFile(trackFile, trackSaveFile);
                    trackSaveFilePath = trackSaveFile.getPath();
                }
                String[] aacAndM4aFilePaths = aac2m4a(aacDirPath);
                String aacFilePath = aacAndM4aFilePaths[0];
                String m4aFilePath = aacAndM4aFilePaths[1];

                File aacFile = new File(aacFilePath);
                File m4aFile = new File(m4aFilePath);

                if (!aacFile.exists()) {
                    throw new Exception("aacFile is not exist,aacFilePath:" + aacFilePath);
                }
                if (!m4aFile.exists()) {
                    throw new Exception("m4aFile is not exist,m4aFilePath:" + m4aFilePath);
                }
                //将文件另存
                String aacSaveFileName = courseUuid + "_" + aacFile.getName();
                File aacSaveFile = new File(saveDir, aacSaveFileName);
                //文件copy到新目录，copy不可少
                copyFile(aacFile, aacSaveFile);
                String m4aSaveFileName = courseUuid + "_" + m4aFile.getName();
                //只是指定新文件名所在目录
                File m4aSaveFile = new File(webSaveDir, m4aSaveFileName);
                //在新目录生成m4a文件，copy不可少
                copyFile(m4aFile, m4aSaveFile);


                //获取m4a文件时长
                Integer m4aVoiceDuration = getM4aVoiceDuration(m4aSaveFile.getPath());
                //文件url
                String fileBaseUrl = ymlMyConfig.getUploadAddress() + UPLOAD_ROOT + FILESEPARATOR + RECORD_DIR + FILESEPARATOR + dateDir + FILESEPARATOR;
                String m4aFileUrl = fileBaseUrl + m4aSaveFileName;
                //入库
                ClassRecordData classRecordData = new ClassRecordData();
                classRecordData.setClassRecordDataUuid(UUIDUtil.randomUUID2());
                classRecordData.setClassRoomUuid(classRoom.getClassRoomUuid());
                classRecordData.setRecordRole(Integer.parseInt(recordRole));
                classRecordData.setVoiceUrl(m4aFileUrl);
                classRecordData.setVoiceDuration(m4aVoiceDuration);
                classRecordData.setTrackPath(trackSaveFilePath);
                classRecordData.setAacVoicePath(aacSaveFile.getPath());
                classRecordDataList.add(classRecordData);
                LOGGER.info("存classRecordData入参: " + classRoom.getClassRoomUuid() + "," + Integer.parseInt(recordRole) + "," + m4aFileUrl + "," + m4aVoiceDuration + "," + aacSaveFile.getPath());
            }
        }
        if (classRecordDataList.size() > 0) {
            classRecordDataService.batchSave(classRecordDataList);
        }
    }

    /**
     * 将aac文件转为m4a文件
     *
     * @param aacDirPath aac文件所在目录
     * @return String[] 0:aac文件路径 1:m4a文件路径
     * @throws Exception
     */
    private static String[] aac2m4a(String aacDirPath) throws Exception {
        String[] aacAndM4aFilePaths = new String[2];
        File aacDir = new File(aacDirPath);
        if (!aacDir.exists()) {
            throw new Exception("aacDir is not exist,aacDirPath:" + aacDirPath);
        }
        String aac2m4aCmd = "/usr/bin/python /www/hktRecord/tools/video_convert.py " + aacDirPath;
        exec(aac2m4aCmd, true);
        File[] listFiles = aacDir.listFiles();
        for (File file : listFiles) {
            String filePath = file.getPath();
            if (filePath.endsWith(".aac")) {
                aacAndM4aFilePaths[0] = filePath;
            }
            if (filePath.endsWith(".m4a")) {
                aacAndM4aFilePaths[1] = filePath;
            }
        }
        return aacAndM4aFilePaths;
    }

    /**
     * 获取m4a或mp4音频文件时长
     *
     * @param m4aFilePath m4a文件路径
     * @return 时长(单位 毫秒ms)
     * @throws Exception
     */
    private static Integer getM4aVoiceDuration(String m4aFilePath) throws Exception {
        File m4aFile = new File(m4aFilePath);
        if (!m4aFile.exists()) {
            throw new Exception("m4aFile is not exist,m4aFilePath:" + m4aFilePath);
        }
        String ffmpegCmd = "/www/hktRecord/tools/ffmpeg -i " + m4aFilePath;
        CmdExecResult execResult = exec(ffmpegCmd, true);
        if (!execResult.isExecStatus()) { //时长在异常输出中出现
            String errorInfo = execResult.getErrorInfo();
            String[] infos = errorInfo.split(System.lineSeparator());
            for (String info : infos) {
                if (info.contains("Duration:")) {
                    String durationStr = info.split(",")[0].substring(" Duration: ".length() + 1);
                    return str2ms(durationStr);
                }
            }
        }
        return null;
    }

    /**
     * @param courseUuid
     * @param
     * @return
     * @throws Exception
     * @author wqz 2018.5.31
     */
    public static void uniteRecord(List<ClassRecordData> classRecordDataList, String courseUuid, String recordDir, YmlMyConfig ymlMyConfig, ClassRoomService classRoomService, CoursewareService coursewareService) throws Exception {
        int size = classRecordDataList.size();
        StringBuilder uniteCmd = new StringBuilder();
        uniteCmd.append("concat:");
        File aacParentFile = null;
        int totalVoiceDuration = 0;
        for (int i = 0; i < size; i++) {
            ClassRecordData classRecordData = classRecordDataList.get(i);
            String aacVoicePath = classRecordData.getAacVoicePath();
            totalVoiceDuration += classRecordData.getVoiceDuration();
            if (aacParentFile == null) {
                aacParentFile = new File(aacVoicePath).getParentFile();
            }
            uniteCmd.append(aacVoicePath);
            if (i != size - 1) {
                uniteCmd.append("|");
            }
        }
        if (aacParentFile == null) {
            throw new Exception("此课程没有aac文件！");
        }
        String aacUnitePath = aacParentFile.getPath() + FILESEPARATOR;
        String aacUniteName = courseUuid + ".aac";
        String aacPath = aacUnitePath + aacUniteName;
        LOGGER.info("aacPath={}", aacPath);
        String[] uniteCmds = new String[]{"/www/hktRecord/tools/ffmpeg", "-i", uniteCmd.toString(), "-y", "-acodec", "copy", aacPath};
        exec(uniteCmds, true);
        File aacUniteFile = new File(aacPath);
        String dateDir = aacParentFile.getPath().substring(aacParentFile.getParentFile().getPath().length() + 1);
        String webSaveDir = recordDir + FILESEPARATOR + dateDir + FILESEPARATOR;
        String m4aVoiceUrl = null;
        if (aacUniteFile.exists()) {
            //转成m4a文件
            StringBuilder aac2m4aCmd = new StringBuilder();
            aac2m4aCmd.append("/www/hktRecord/tools/ffmpeg -i ");
            aac2m4aCmd.append(aacPath);
            aac2m4aCmd.append(" -acodec copy ");
            String m4aFileName = courseUuid + ".m4a";
            aac2m4aCmd.append(webSaveDir);
            aac2m4aCmd.append(m4aFileName);
            exec(aac2m4aCmd.toString(), true);
            File m4aSaveFile = new File(webSaveDir, m4aFileName);
            if (m4aSaveFile.exists()) {
                String voiceBasePath = ymlMyConfig.getUploadAddress() + UPLOAD_ROOT + FILESEPARATOR + RECORD_DIR + FILESEPARATOR + dateDir + FILESEPARATOR;
                m4aVoiceUrl = voiceBasePath + m4aFileName;
                StringBuilder m4a2flacCmd = new StringBuilder();
                m4a2flacCmd.append("/www/hktRecord/tools/ffmpeg -i ");
                m4a2flacCmd.append(m4aSaveFile.getPath());
                m4a2flacCmd.append(" -y ");

            } else {
                LOGGER.info("m4aSaveFile={}不存在！", m4aSaveFile.getPath());
            }
        } else {
            LOGGER.info("aacUniteFile={}不存在！", aacUniteFile.getPath());
        }

        //合并轨迹db文件
        String trackComposeDBFileName = courseUuid + "_compose.db";
        String trackComposeDBFilePath = BASEPATH + dateDir + FILESEPARATOR + trackComposeDBFileName;
        File trackComposeDBFile = new File(trackComposeDBFilePath);
        String trackComposeUrl = null;
        if (trackComposeDBFile.exists()) {
            LOGGER.info("合并轨迹db文件" + courseUuid);
            TrackData trackData = CourseUtil.parseComposeDBFile(trackComposeDBFilePath, coursewareService);

            //序列化后保存到数据库中
            byte[] serialize = SerializationUtils.serialize(trackData);
            String serializeDataFileName = trackComposeDBFileName.substring(0, trackComposeDBFileName.lastIndexOf("."));
            File serializeDataFile = new File(webSaveDir, serializeDataFileName);
            org.apache.commons.io.FileUtils.writeByteArrayToFile(serializeDataFile, serialize);
            trackComposeUrl = ymlMyConfig.getUploadAddress() + UPLOAD_ROOT + FILESEPARATOR + RECORD_DIR + FILESEPARATOR + dateDir + FILESEPARATOR + serializeDataFileName;
        } else {
            LOGGER.info("trackComposeDBFile={}不存在！", trackComposeDBFile.getPath());
        }

        ClassRoom classRoom = new ClassRoom();
        classRoom.setCourseUuid(courseUuid);
        classRoom.setVoiceUrl(m4aVoiceUrl);
        classRoom.setVoiceDuration(totalVoiceDuration);
        classRoom.setTrackUrl(trackComposeUrl);
        int i = classRoomService.updateByCourseUuid(classRoom);
        if (i == 0) {
            throw new Exception("合并文件URL入库失败！");
        }
    }

    /**
     * 解析客户端进出房间记录，统计课时(通过线程执行)
     *
     * @param courseUuid
     * @param recordLogsDir
     * @param classMateService
     * @param classRecordService
     * @param classRoomService
     * @param cpCourseService
     * @throws Exception
     */
    public static void parseAndStatisticsClassRecordDBDataByThread(String courseUuid, File recordLogsDir, ClassMateService classMateService, ClassRecordService classRecordService, ClassRoomService classRoomService, CpCourseService cpCourseService, MessageService messageService,Integer curRecordId) {
        try {
            boolean verifyFlag = verifyRecordEnd(courseUuid, recordLogsDir);
            if (verifyFlag) {
                parseAndStatisticsClassRecordDBData(courseUuid, recordLogsDir, classMateService, classRecordService, classRoomService, cpCourseService,curRecordId);
            } else {
                LOGGER.error("没有录制结束标识！");
                //throw new Exception("courseUuid=" + courseUuid + " 没有录制结束标识！");
            }
        } catch (Exception e) {
            LOGGER.info("*****courseUuid=" + courseUuid + "解析客户端进出房间记录，统计课时线程异常！*****", e);
            try {
                messageService.sendExceptionMessage(SENDPHONE, courseUuid, e.getMessage());
            } catch (Exception e1) {
                LOGGER.error(e.toString(), e);
            }
            LOGGER.error(e.toString(), e);
        }
    }

    /**
     * 录制是否结束
     *
     * @param courseUuid
     * @param recordLogsDir
     * @return
     * @throws InterruptedException
     */
    private static boolean verifyRecordEnd(String courseUuid, File recordLogsDir) throws InterruptedException {
        boolean flag = false;
        int count = 0;
        while (!flag) {
            if (count > 3) {
                LOGGER.info("courseUuid={}没有录制结束标识文件！", courseUuid);
                return false;
            }
            count++;
            LOGGER.info("第{}次检查录制结束标识文件！", count);
            String endFlagPath = recordLogsDir.getPath() + FILESEPARATOR + courseUuid + ".end";
            File endFlagFile = new File(endFlagPath);
            if (endFlagFile.exists()) {
                return true;
            } else {
                Thread.sleep(30 * 1000);
            }
        }
        return false;
    }

    /**
     * 解析客户端进出房间记录，统计课时
     *
     * @param courseUuid
     * @param recordLogsDir
     * @throws Exception
     */
    private static void parseAndStatisticsClassRecordDBData(String courseUuid, File recordLogsDir, ClassMateService classMateService, ClassRecordService classRecordService, ClassRoomService classRoomService, CpCourseService cpCourseService, Integer curRecordId) throws Exception {
        //解析记录数据
        List<ClassRecordDBData> dataList = parseClassRecordDBFile(courseUuid, recordLogsDir);
        if (dataList == null || dataList.size() == 0) {
            throw new Exception("courseUuid=" + courseUuid + " :客户端进出房间原记录数据不存在！");
        }
        //入库
        int i = saveClassRecordDBData(dataList, classMateService, classRecordService,curRecordId);
        if (i == 0) {
            throw new Exception("courseUuid=" + courseUuid + " :saveClassRecordDBData失败！");
        }
        ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
        if (classRoom == null) {
            throw new Exception("courseUuid=" + courseUuid + " :classRoom不存在！");
        }
        CpCourse cpCourse = cpCourseService.findByUuid(courseUuid);
        String courseDate = cpCourse.getCourseDate();
        String startTime = cpCourse.getStartTime();
        String endTime = cpCourse.getEndTime();
        List<ClassRecord> classRecordList = classRecordService.findByClassRoomUuid(classRoom.getClassRoomUuid());
        StatisticsClassTimeResult statisticsClassTimeResult = CourseUtil.statisticsClassTime(courseDate, startTime, endTime, classRecordList);
        cpCourse.setRealLength(statisticsClassTimeResult.getComTotalTime());
        cpCourse.setLeadsLength(statisticsClassTimeResult.getStudentTotalTime());
        cpCourse.setTeacherLength(statisticsClassTimeResult.getTeacherTotalTime());
        cpCourse.setIsSuccess(statisticsClassTimeResult.getIsSuccess());
        int j = cpCourseService.update(cpCourse);
        if (j == 0) {
            throw new Exception("courseUuid=" + courseUuid + " :cpCourse更新时长失败！");
        }
        ClassRoom classRoom1 = new ClassRoom();
        classRoom1.setCourseUuid(courseUuid);
        classRoom1.setStatisticsStatus(2);
        classRoomService.updateByCourseUuid(classRoom1);
        LOGGER.info("*****课时统计成功,courseUuid={}*****", courseUuid);
    }

    /**
     * 解析上课记录数据
     * /www/hktRecord/mylogs/20180804 复制到/www/recordDir/20180804（恢复时此目录已移到oss所以）
     * 直接用/www/hktRecord/mylogs/20180804 目录文件
     * （两个目录的_user_course.db一样）
     *
     * @param courseUuid
     * @param recordLogsDir
     * @return
     * @throws Exception
     */
    private static List<ClassRecordDBData> parseClassRecordDBFile(String courseUuid, File recordLogsDir) throws Exception {
        String classRecordDBFileName = courseUuid + "_user_course.db";
        String classRecordDBPath = recordLogsDir.getPath() + FILESEPARATOR + classRecordDBFileName;
        if (recordLogsDir == null || !recordLogsDir.exists()) {
            throw new Exception("classRecordDBFileName=" + classRecordDBFileName + "不存在！");
        }
        //String sql = "jdbc:sqlite://" + recordLogsDir.getPath();
        String sql = "jdbc:sqlite://".concat(recordLogsDir.getPath().concat("/").concat(classRecordDBFileName));
        LOGGER.info("解析当前进入房间记录文件:" + recordLogsDir.getPath().concat("/").concat(classRecordDBFileName));
        Connection conn = null;
        ResultSet rs = null;
        int row = 0;
        List<ClassRecordDBData> dataList = new ArrayList<>();
        Statement stat = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(sql);
            stat = conn.createStatement();
            rs = stat.executeQuery("select * from user_course"); // 查询数据
            while (rs.next()) {
                row++;
                int userid = rs.getInt("userid");
                int status = rs.getInt("status");
                long timestamp = rs.getLong("timestamp");
                LOGGER.info("userid = " + userid + "  status = " + status + "timestamp=" + timestamp);
                ClassRecordDBData data = new ClassRecordDBData();
                data.setUserid(userid);
                data.setStatus(status);
                data.setTimestamp(timestamp);
                dataList.add(data);
            }
        } catch (Exception e) {
            LOGGER.info("正在解析{}第【{}】行记录", classRecordDBPath, row);
            LOGGER.error(e.toString(), e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    LOGGER.error(e.toString(), e);
                }
            }
            if (stat != null) {
                try {
                    stat.close();
                } catch (SQLException e) {
                    LOGGER.error(e.toString(), e);
                }
            }
            if (conn != null) {
                try {
                    //结束数据库的连接
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.error(e.toString(), e);
                }
            }
        }
        return dataList;
    }

    /**
     * 将记录数据保存到数据库中
     *
     * @param dataList
     * @return
     * @throws Exception
     */
    private static int saveClassRecordDBData(List<ClassRecordDBData> dataList, ClassMateService classMateService, ClassRecordService classRecordService,Integer curRecordId) throws Exception {
        int size = dataList.size();
        Map<Integer, ClassMate> classMateMap = new ConcurrentHashMap<>();
        List<ClassRecord> classRecordList = new ArrayList<>();
        ClassRecord classRecord;
        int recordId = 0;
        Map<Integer, Integer> useridStatusMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            ClassRecordDBData classRecordDBData = dataList.get(i);
            int userid = classRecordDBData.getUserid();
            int status = classRecordDBData.getStatus();
            long timestamp = classRecordDBData.getTimestamp();

            if (i == 0) {  //|| i == size - 1
                //recordId = userid;
                recordId = curRecordId;
                continue;
            }

            if (i == size - 1) {
                Collection<ClassMate> values = classMateService.findByRecordId(userid);
                Iterator<ClassMate> iterator = values.iterator();
                while (iterator.hasNext()) {
                    ClassMate classMate = iterator.next();
                    Integer agoraUid = classMate.getAgoraUid();
                    Integer agoraStatus = useridStatusMap.get(agoraUid);
                    if (agoraStatus != null && agoraStatus == 0) { //录制退出之前已经退出
                        continue;
                    }
                    classRecord = new ClassRecord();
                    classRecord.setClassRecordUuid(UUIDUtil.randomUUID2());
                    classRecord.setClassRoomUuid(classMate.getClassRoomUuid());
                    classRecord.setRecordTime(timestamp);
                    classRecord.setRecordType(status);
                    classRecord.setUserType(classMate.getUserType());
                    classRecord.setUserUuid(classMate.getUserUuid());
                    classRecord.setUserName(classMate.getUserName());
                    classRecord.setCreateUid(classMate.getUserUuid());
                    classRecordList.add(classRecord);
                }
                continue;
            }
            ClassMate classMate = classMateMap.get(userid);
            if (classMate == null) {
                LOGGER.info("userid={}", userid);
                LOGGER.info("recordId={}", recordId);
                classMate = classMateService.findByAgoraUidAndRecordId(userid, recordId);
                if(userid>0 && classMate!=null){
                    classMateMap.put(userid, classMate);
                }
                if (classMate == null) {
                    LOGGER.info("无效的userid={}", userid);
                    continue;
                }
            }
            classRecord = new ClassRecord();
            classRecord.setClassRecordUuid(UUIDUtil.randomUUID2());
            classRecord.setClassRoomUuid(classMate.getClassRoomUuid());
            classRecord.setRecordTime(timestamp);
            classRecord.setRecordType(status);
            classRecord.setUserType(classMate.getUserType());
            classRecord.setUserUuid(classMate.getUserUuid());
            classRecord.setUserName(classMate.getUserName());
            classRecord.setCreateUid(classMate.getUserUuid());
            classRecordList.add(classRecord);

            useridStatusMap.put(userid, status);
        }
        if (classRecordList.size() == 0) {
            throw new Exception("classRecordList.size() = 0！！！");
        }
        return classRecordService.batchSave(classRecordList);
    }

    /**
     * 统计课时失败的，手动恢复
     *
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/resotreClassTime")
    public ResponseEntity<Response> resotreClassTime(UploadTrackFileRequest request) throws Exception {
        LOGGER.info("手动恢复统计课时.");
        CpCourse cpCourse = cpCourseService.findByUuid(request.getCourseUuid());
        String courseDate = cpCourse.getCourseDate();
        String startTime = cpCourse.getStartTime();
        String endTime = cpCourse.getEndTime();

        ClassRoom classRoom = classRoomService.findByCourseUuid(request.getCourseUuid());
        if (classRoom == null) {
            throw new Exception("courseUuid=" + request.getCourseUuid() + " :classRoom不存在！");
        }
        List<ClassRecord> classRecordList = classRecordService.findByClassRoomUuid(classRoom.getClassRoomUuid());
        StatisticsClassTimeResult statisticsClassTimeResult = CourseUtil.statisticsClassTime(courseDate, startTime, endTime, classRecordList);
        cpCourse.setRealLength(statisticsClassTimeResult.getComTotalTime());
        cpCourse.setLeadsLength(statisticsClassTimeResult.getStudentTotalTime());
        cpCourse.setTeacherLength(statisticsClassTimeResult.getTeacherTotalTime());
        cpCourse.setIsSuccess(statisticsClassTimeResult.getIsSuccess());
        int j = cpCourseService.update(cpCourse);
        if (j == 0) {
            throw new Exception("courseUuid=" + request.getCourseUuid() + " :cpCourse更新时长失败！");
        }
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 根据讲师中心上传后的图片url（无过期时间的）
     * 重新序列化持久存到compose.db文件中
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping("/newComposeDB")
    public ResponseEntity<Response> newComposeDB(UploadTrackFileRequest request) throws Exception {
        LOGGER.info("newComposeDB.");
        String webSaveDir="/www/clientfile/uploadPath/recordDir";
        String trackComposeDBFileName = request.getCourseUuid() + "_compose.db";
        String trackComposeDBFilePath = BASEPATH.concat(request.getRestoreDate()).concat(FILESEPARATOR).concat(trackComposeDBFileName);
        TrackData trackData = CourseUtil.parseComposeDBFile(trackComposeDBFilePath, coursewareService);
        //序列化后保存到数据库中
        byte[] serialize = SerializationUtils.serialize(trackData);
        String serializeDataFileName = trackComposeDBFileName.substring(0, trackComposeDBFileName.lastIndexOf("."));
        //webSaveDir 生成课程id_compose.db
        File serializeDataFile = new File(webSaveDir, serializeDataFileName);
        org.apache.commons.io.FileUtils.writeByteArrayToFile(serializeDataFile, serialize);
        return ResponseEntity.ok(Response.success());
    }
}
