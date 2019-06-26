package cn.onlyhi.file.util;

import cn.onlyhi.client.dto.ClassRecordDBData;
import cn.onlyhi.client.dto.TrackData;
import cn.onlyhi.client.po.*;
import cn.onlyhi.client.service.*;
import cn.onlyhi.common.enums.ClientEnum;
import cn.onlyhi.common.util.CmdExecResult;
import cn.onlyhi.common.util.OssUtils;
import cn.onlyhi.common.util.UUIDUtil;
import cn.onlyhi.file.config.AgoraConfig;
import cn.onlyhi.file.config.YmlMyConfig;
import com.aliyun.oss.OSSClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static cn.onlyhi.common.constants.Constants.*;
import static cn.onlyhi.common.enums.ClientEnum.UserType.STUDENT;
import static cn.onlyhi.common.enums.ClientEnum.UserType.TEACHER;
import static cn.onlyhi.common.util.ClientUtil.*;
import static cn.onlyhi.common.util.CmdExecuteUtil.exec;
import static cn.onlyhi.common.util.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.copyFile;

/**
 * 音频文件上传到oss 服务器，新开方法
 * 1、服务器地址，新oss地址/www/clientfile/uploadPath/recordDir/20180630，
 * 所有方法来源CourseUtil，在CourseUtil基础上需要修改的在当前类重写
 *
 * @Author wqz
 * <p>
 * Created by wqz on 2018/07/01.
 */
public class CourseOssUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseOssUtil.class);
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

    /**
     * 统计学生和老师的音频和轨迹数据并入库
     * 新增用于录制的mp4视频，统计学生和老师的音频和轨迹数据并入库
     * 及测试环境(/www/clientfile/uploadPath/recordDir/20180703)上传mp4原文件、合并后的_av.mp4文件、合并的轨迹文件到阿里云
     *
     * @param courseUuid
     * @param recordLogsDir
     * @param recordDir
     * @param ymlMyConfig
     * @param classRoomService
     * @param classRecordDataService
     * @throws Exception
     */
    private static void statisticsTeacherVoiceDataNew(String courseUuid, File recordLogsDir, String recordDir, YmlMyConfig ymlMyConfig, ClassRoomService classRoomService, ClassRecordDataService classRecordDataService) throws Exception {
        LOGGER.info("统计学生和老师的音频和轨迹数据并入库.");
        /**点击下课到mp4源文件写入完成大概需要5秒，然后检验文件是否写完整，标志是recording2-done.txt是否存在*/
        Thread.sleep(5 * 1000);
        ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
        if (classRoom == null) {
            throw new Exception("classRoom is null");
        }
        String pathFileName = courseUuid + ".path";
        File[] listFiles = recordLogsDir.listFiles();
        String content = readFileToString(listFiles, pathFileName);
        if (StringUtils.isBlank(content)) {
            throw new Exception("音频录制目录不存在！");
        }
        String dateDir = recordLogsDir.getPath().substring(recordLogsDir.getParentFile().getPath().length() + 1);
        String webSaveDir = recordDir + FILESEPARATOR + dateDir;
        ///www/recordDir/
        String saveDir = BASESAVEPATH + dateDir;
        List<ClassRecordData> classRecordDataList = new ArrayList<>();
        String[] contents = content.split("\\n");
        // uploadPath/recordDir/20180702/
        String urlDir = UPLOAD_ROOT.concat("/").concat(RECORD_DIR).concat("/").concat(dateDir).concat("/");
        String mp4FilePathUnion = "";
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
                LOGGER.info("只统计学生和老师的音频和轨迹.");
                String aacDirPath = strs[2];
                LOGGER.info("aacDirPath" + aacDirPath);
                String trackPath = strs[3];
                File trackFile = new File(trackPath);
                String trackSaveFilePath = null;
                /**检查轨迹文件是否存在*/
                if (trackFile.exists()) {
                    LOGGER.info("轨迹文件存在：" + trackFile);
                    String trackFileName = trackFile.getName();
                    String saveFileName = trackFileName.substring(0, trackFileName.lastIndexOf(".db")) + "_server.db";
                    File trackSaveFile = new File(saveDir, saveFileName);
                    //复制到/www/recordDir/20180702 解析用
                    copyFile(trackFile, trackSaveFile);
                    trackSaveFilePath = trackSaveFile.getPath();
                }

                /**********只有当录制文件夹内有recording2-done.txt表示mp4文件写入完整*****/
                File aacDirTemp = new File(aacDirPath);
                File[] listFileTemp = aacDirTemp.listFiles();
                for (File file : listFileTemp) {
                    String filePath = file.getPath();
                    if (filePath.endsWith("recording2-done.txt")) {
                        LOGGER.info("mp4源文件写入完成，启动脚本转换_av.mp4文件...");
                        aac2m4aNew(aacDirPath);
                    }
                }
                /*************检查aac/mp4/_av.mp4文件是否存在*******************/
                String[] aacAndM4aFilePaths = checkAvMp4IsExist(aacDirPath);
                //取.mp4路径
                String mp4FilePath = aacAndM4aFilePaths[1];
                //取_av.mp4路径 /www/hktRecord/recordDir/20190307/0e1a2ec1-7927-4c3d-8ecf-c3875b5234d810_091945/0_20190307091945408_av.mp4
                String avMp4FilePath = aacAndM4aFilePaths[2];
                LOGGER.info("_av.mp4FilePath:" + avMp4FilePath);
                File mp4File = new File(mp4FilePath);
                if (StringUtils.isEmpty(mp4FilePath)) {
                    throw new Exception("mp4FilePath is null,mp4FilePath:" + mp4FilePath);
                }
                if (StringUtils.isEmpty(avMp4FilePath)) {
                    throw new Exception("avMp4FilePath is null,avMp4FilePath:" + avMp4FilePath);
                }
                /**将aac(每个时间段的及合并的)/user_course.db文件另存到/www/recordDir/20180702**/
                String aacFilePath = aacAndM4aFilePaths[0];
                File aacFile = new File(aacFilePath);
                if (!aacFile.exists()) {
                    throw new Exception("aacFile is not exist,aacFilePath:" + aacFilePath);
                }
                String aacSaveFileName = courseUuid + "_" + aacFile.getName();
                File aacSaveFile = new File(saveDir, aacSaveFileName);
                copyFile(aacFile, aacSaveFile);
                /*****************************************************/
                /**start wqz 将mp4文件另存为*/
                String mp4SaveFileName = courseUuid + "_" + mp4File.getName();
                File mp4SaveFile = new File(webSaveDir, mp4SaveFileName);
                copyFile(mp4File, mp4SaveFile);
                /**end*/
                Integer mp4VoiceDuration = getM4aVoiceDuration(mp4SaveFile.getPath());
                LOGGER.info("mp4VoiceDuration:" + mp4VoiceDuration);
                /***********************上传_av.mp4到oss*******/
                OSSClient client1 = OssUtils.generateOssClient();
                File avMp4File = new File(avMp4FilePath);
                // /www/hktRecord/recordDir/20180630/86baf0c1d1134b909f5126cacb1b625910_064952/0_20180630064952869_av.mp4
                LOGGER.info("_av.mp4File:" + avMp4File);
                if (!avMp4File.exists()) {
                    throw new Exception("avMp4File is not exist,avMp4File:" + avMp4File);
                } else {
                    mp4FilePathUnion = mp4FilePathUnion.concat("file " + avMp4File).concat("\r\n");
                }
                String avMp4Key = urlDir.concat(courseUuid).concat("_").concat(avMp4File.getName());
                LOGGER.info("上传_av.mp4到oss webSaveDir" + webSaveDir);
                InputStream in2 = new FileInputStream(avMp4FilePath);
                OssUtils.uploadFileInputStream(client1, in2, webSaveDir, avMp4File.length(), avMp4Key,avMp4File);
                client1.shutdown();
                /*****************************************************/
                //入库
                ClassRecordData classRecordData = new ClassRecordData();
                classRecordData.setClassRecordDataUuid(UUIDUtil.randomUUID2());
                classRecordData.setClassRoomUuid(classRoom.getClassRoomUuid());
                classRecordData.setRecordRole(Integer.parseInt(recordRole));
                classRecordData.setVoiceDuration(0);
                classRecordData.setTrackPath(trackSaveFilePath);
                classRecordData.setAacVoicePath(aacSaveFile.getPath());
                //由于oss url 有过期时间，不再存url,存oss key ，取url时，根据key,页面没有用到classRecordData的Mp4VideoUrl，暂不存
                classRecordData.setVideoDuration(mp4VoiceDuration);
                classRecordDataList.add(classRecordData);
            }
        }
        int count = classRecordDataService.batchSave(classRecordDataList);
        if (count > 0) {
            LOGGER.info("写classRecordData表完成.");
        }
        /**建一个课程id.txt存_av.mp4文件生成的路径*/
        boolean flag = CourseUtil.creatTxtFile(webSaveDir, courseUuid);
        if (flag) {
            CourseUtil.writeTxtFile(mp4FilePathUnion, webSaveDir.concat("/").concat(courseUuid).concat(".txt"));
            String txtPath = ("/www/clientfile/").concat(urlDir).concat(courseUuid).concat(".txt");
            // /www/clientfile/uploadPath/recordDir/20190305/a78c8471-744b-4393-9349-3891e43ac23f.txt
            uploadTxt(txtPath, urlDir);
        }
    }

    /**
     * 上传课程id.txt文件
     * /www/clientfile/uploadPath/recordDir/20180702/39656eb670bf400fa079c5064cf14fec.txt
     *
     * @param txtPath txt文件所在目录
     * @param urlDir  uploadPath/recordDir/20180702/
     * @return String[] 0:aac文件路径 1:m4a文件路径
     * @throws Exception
     */
    private static void uploadTxt(String txtPath, String urlDir) throws IOException {
        try {
            OSSClient client = OssUtils.generateOssClient();
            //创建OSSClient客户端
            File txtFile = new File(txtPath);
            // oss key 字符第一位不能有"/"
            String txtKey = urlDir.concat(txtFile.getName());
            InputStream in = new FileInputStream(txtPath);
        /*    if (StringUtils.isNotEmpty(txtPath) && txtPath.substring(0, 1).contains("/")) {
                txtPath = txtPath.substring(1, txtPath.length());
            }*/
            OssUtils.uploadFileInputStream(client, in, txtPath, txtFile.length(), txtKey,txtFile);
            client.shutdown();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * 将aac文件转为m4a或mp4文件
     *
     * @param aacDirPath aac文件所在目录
     * @return String[] 0:aac文件路径 1:m4a文件路径
     * @throws Exception
     */
    private static String[] aac2m4a(String aacDirPath) throws Exception {
        String[] aacAndM4aFilePaths = new String[4];
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
            if (filePath.endsWith("_av.mp4")) {
                aacAndM4aFilePaths[2] = filePath;
            }
            if (filePath.endsWith(".mp4")) {
                LOGGER.info("/www/hktRecord/recordDir/当前日期/课程uuid/目录中含有mp4：" + filePath);
                aacAndM4aFilePaths[3] = filePath;
            }
        }
        return aacAndM4aFilePaths;
    }

    /**
     * 将aac文件转为m4a或mp4文件
     *
     * @param aacDirPath aac文件所在目录
     * @return String[] 0:aac文件路径 1:m4a文件路径
     * @throws Exception
     */
    private static void aac2m4aNew(String aacDirPath) throws Exception {
        LOGGER.info("aac2m4a()...");
        File aacDir = new File(aacDirPath);
        if (!aacDir.exists()) {
            throw new Exception("aacDir is not exist,aacDirPath:" + aacDirPath);
        }
        String aac2m4aCmd = "/usr/bin/python /www/hktRecord/tools/video_convert.py " + aacDirPath;
        LOGGER.info("执行脚本生成_av.mp4文件");
        exec(aac2m4aCmd, true);
    }

    /**
     * 获取音频或视频文件
     *
     * @param filePath 音频或视频文件路径
     * @return 时长(单位 毫秒ms)
     * @throws Exception
     */
    private static Integer getM4aVoiceDuration(String filePath) throws Exception {
        File m4aFile = new File(filePath);
        if (!m4aFile.exists()) {
            throw new Exception("m4aFile or mp4File  is not exist,m4aFilePath or mp4File:" + filePath);
        }
        String ffmpegCmd = "/www/hktRecord/tools/ffmpeg -i " + filePath;
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
     * 统计学生和cc或cr的音频并入库
     * 暂时注释statisticsSCVoiceData，新增statisticsSCVoiceDataNew用于上传音频文件到阿里云
     * 上传阿里云,由于客户单1.0版本使用了m4a（xp系统不支持m4a所以使用了flac）与flac文件，2.0（客户端改了xp支持m4a，所以可以不使用flac）只使用了m4a文件，所以2.0去掉转存flac的转换过程，
     * 本方法对应的上层方法对应的url应该新增一个供2.0使用,旧的url兼容1.0不变
     *
     * @param courseUuid
     * @param recordLogsDir
     * @param recordDir
     * @param ymlMyConfig
     * @param classRoomService
     * @param classRecordDataService
     * @throws Exception
     */
    private static void statisticsSCVoiceDataNew(String courseUuid, File recordLogsDir, String recordDir, YmlMyConfig ymlMyConfig, ClassRoomService classRoomService, ClassRecordDataService classRecordDataService) throws Exception {
        ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
        if (classRoom == null) {
            throw new Exception("classRoom is null");
        }
        String pathFileName = courseUuid + ".path";
        File[] listFiles = recordLogsDir.listFiles();
        String content = readFileToString(listFiles, pathFileName);
        if (StringUtils.isBlank(content)) {
            throw new Exception("音频录制目录不存在！");
        }
        //recordLogsDir录制轨迹文件根路径/www/hktRecord/mylogs/
        String dateDir = recordLogsDir.getPath().substring(recordLogsDir.getParentFile().getPath().length() + 1);
        String saveDir = BASESAVEPATH + dateDir;
        List<ClassRecordData> classRecordDataList = new ArrayList<>();
        String[] contents = content.split("\\n");
        for (String str : contents) {
            String[] strs = str.split(":");
            if (strs.length != 4) {
                throw new Exception("path文件数据异常！");
            }
            String recordRole = strs[0];
            if (!"0".equals(recordRole)) {    //统计学生和cc或cr的音频
                String aacDirPath = strs[2];
                String[] aacAndM4aFilePaths = aac2m4a(aacDirPath);
                String aacFilePath = aacAndM4aFilePaths[0];
                //String m4aFilePath = aacAndM4aFilePaths[1];
                String avMp4FilePath = aacAndM4aFilePaths[2];
                File aacFile = new File(aacFilePath);
                //File m4aFile = new File(m4aFilePath);
                File avMp4File = new File(avMp4FilePath);
                if (!aacFile.exists()) {
                    throw new Exception("aacFile is not exist,aacFilePath:" + aacFilePath);
                }
                if (!avMp4File.exists()) {
                    throw new Exception("avMp4File is not exist,avMp4FilePath:" + avMp4File);
                }
                //aa将文件另存到目录/www/recordDir/20180702
                String aacSaveFileName = courseUuid + "_" + aacFile.getName();
                File aacSaveFile = new File(saveDir, aacSaveFileName);
                copyFile(aacFile, aacSaveFile);
                //获取转换后文件的时长
                Integer avMp4VoiceDuration = getM4aVoiceDuration(avMp4FilePath);
                //入库
                ClassRecordData classRecordData = new ClassRecordData();
                classRecordData.setClassRecordDataUuid(UUIDUtil.randomUUID2());
                classRecordData.setClassRoomUuid(classRoom.getClassRoomUuid());
                classRecordData.setRecordRole(Integer.parseInt(recordRole));
                //m4a音频文件完整路径（m4aFileUrl）,上传到阿里云后，存m4aOssUrl
                classRecordData.setVideoDuration(avMp4VoiceDuration);
                //aac原始文件相对路径（aacSaveFile.getPath()）,上传到阿里云后，存aacOssUrl
                classRecordDataList.add(classRecordData);
            }
        }
        classRecordDataService.batchSave(classRecordDataList);
    }

    /**
     * 检查生成的_av.mp4与.txt文件是否存在
     *
     * @param
     * @return
     * @throws Exception
     */
    private static String[] checkAvMp4IsExist(String aacDirPath) throws Exception {
        String[] aacAndM4aFilePaths = new String[4];
        boolean accFlag = false;
        boolean mp4Flag = false;
        boolean flag = true;
        boolean avMp4Flag = false;
        int count = 0;
        while (flag) {
            count++;
            if (count <= 3) {
                aacAndM4aFilePaths = new String[3];
                File aacDir = new File(aacDirPath);
                File[] listFileseee = aacDir.listFiles();
                for (File file : listFileseee) {
                    String filePath = file.getPath();
                    LOGGER.info("filePath" + filePath);
                    if (filePath.endsWith(".aac")) {
                        aacAndM4aFilePaths[0] = filePath;
                        accFlag = true;
                    }
                    if (filePath.endsWith(".mp4")) {
                        aacAndM4aFilePaths[1] = filePath;
                        mp4Flag = true;
                    }
                    if (filePath.endsWith("_av.mp4")) {
                        ///www/hktRecord/recordDir/20180702/69bda80c9a9c4c67a824a8d5105e011510_034748/0_20180702034749003.aac
                        aacAndM4aFilePaths[2] = filePath;
                        avMp4Flag = true;
                    }
                 /*   if (filePath.endsWith(".txt")) {
                        // filePath=/www/hktRecord/recordDir/20180702/69bda80c9a9c4c67a824a8d5105e011510_025352/metadata.txt
                        LOGGER.info("/www/hktRecord/recordDir/当前日期/课程uuid/目录中含有.txt：" + filePath);
                        aacAndM4aFilePaths[3] = filePath;
                        mp4Flag = true;
                    }*/
                    if (accFlag && mp4Flag && avMp4Flag) {
                        flag = false;
                        break;
                    }
                }
            } else {
                Thread.sleep(5 * 1000);
            }
        }
        return aacAndM4aFilePaths;
    }


    /**
     * 统计老师和学生的回放数据(音频和轨迹数据)
     * 新增此方法，变动：调用了statisticsTeacherVoiceDataNew
     * 用于录制的mp4视频，统计学生和老师的音频和轨迹数据并入库
     * 及测试环境(/www/clientfile/uploadPath/recordDir/20180703)上传mp4原文件、合并后的_av.mp4文件、合并的轨迹文件到阿里云
     * 迁移目录：/www/clientfile/uploadPath/recordDir/20180711；迁移结尾文件：_compose与.mp4与_av.mp4与.txt
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
    public static void statisticsTeacherRecordDataByThreadNew(String courseUuid, CpCourseService cpCourseService, ClassRoomService classRoomService, ClassMateService classMateService, File recordLogsDir, MessageService messageService, ClassRecordDataService classRecordDataService, String recordDir, YmlMyConfig ymlMyConfig, CoursewareService coursewareService) {
        LOGGER.info("***** 统计老师和学生的回放数据(音频和轨迹数据)");
        ClassRoom room = classRoomService.findByCourseUuid(courseUuid);
        if (room != null) {
            LOGGER.info("room != null");
            List<ClassMate> classMateList = classMateService.findByClassRoomUuid(room.getClassRoomUuid());
            if (classMateList != null && classMateList.size() > 1) {
                //aac文件转成m4a文件和flac，并获取m4a文件时长，并保存到数据库中
                List<ClassRecordData> classRecordDataList = classRecordDataService.findByClassRoomUuid(room.getClassRoomUuid());
                if (classRecordDataList == null || classRecordDataList.size() == 0) {
                    LOGGER.info("*****课程{}保存音频和解析轨迹文件线程开始！*****", courseUuid);
                    ClassRoom classRoom = new ClassRoom();
                    classRoom.setCourseUuid(courseUuid);
                    try {
                        statisticsTeacherVoiceDataNew(courseUuid, recordLogsDir, recordDir, ymlMyConfig, classRoomService, classRecordDataService);
                        List<ClassRecordData> classRecordDataList1 = classRecordDataService.findByClassRoomUuidAndRecordRole(room.getClassRoomUuid(), 0);
                        if (classRecordDataList1 != null && classRecordDataList1.size() != 0) {
                            uniteRecordNew(classRecordDataList1, courseUuid, recordDir, ymlMyConfig, classRoomService, coursewareService);
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
     * 录制加入了视频，需要再生成mp4文件
     * 上传阿里云,由于客户单1.0版本使用了m4a与flac文件，2.0只使用了m4a文件，所以2.0去掉转存flac的Url，
     * 本方法对应的上层方法对应的url应该新增一个供2.0使用,旧的url兼容1.0不变
     * 测试环境上传用mp4测，线上需要在原uniteRecord修改上传
     * 测试用音频acc及mp4视频源文件；acc合成.mp4加上mp4合成_av.mp4,最后提供给页面,课程id.mp4
     *
     * @param courseUuid
     * @param
     * @return
     * @throws Exception
     * @author wqz 2018.5.31
     */
    public static void uniteRecordNew(List<ClassRecordData> classRecordDataList, String courseUuid, String recordDir, YmlMyConfig ymlMyConfig, ClassRoomService classRoomService, CoursewareService coursewareService) throws Exception {
        LOGGER.info("uniteRecordNew录制加入了视频，再生成mp4文件");
        int size = classRecordDataList.size();
        StringBuilder uniteCmd = new StringBuilder();
        uniteCmd.append("concat:");
        File aacParentFile = null;
        int totalVoiceDuration = 0;
        //mp4视频总时长
        int totalVideoDuration = 0;
        for (int i = 0; i < size; i++) {
            ClassRecordData classRecordData = classRecordDataList.get(i);
            String aacVoicePath = classRecordData.getAacVoicePath();
            if (Objects.equals(classRecordData.getVoiceDuration(), null)) {
                totalVoiceDuration += 0;
            } else {
                totalVoiceDuration += classRecordData.getVoiceDuration();
            }
            if (Objects.equals(classRecordData.getVideoDuration(), null)) {
                totalVideoDuration += 0;
            } else {
                totalVideoDuration += classRecordData.getVideoDuration();
            }
            if (aacParentFile == null && !StringUtils.isBlank(aacVoicePath)) {
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
        // recordDir /www/clientfile/uploadPath/recordDir
        String webSaveDir = recordDir + FILESEPARATOR + dateDir + FILESEPARATOR;
        //存表的url 路径uploadPath/recordDir/20180630/
        String urlDir = UPLOAD_ROOT.concat("/").concat(RECORD_DIR).concat("/").concat(dateDir).concat("/");
        //mp4文件所在目录完整路径
        String mp4Path = null;
        String mp4OssKey = "";
        if (aacUniteFile.exists()) {
            LOGGER.info("aac文件及课程id.txt存在，开始转mp4文件...");
            StringBuilder aac2mp4Cmd = new StringBuilder();
            aac2mp4Cmd.append("/www/hktRecord/tools/ffmpeg -f concat -safe 0 -i ");
            //.txt文件存的是多个_av.mp4路径，此命令：把acc源文件及通过mp4转换成的_av.mp4源文件转换合并成，课程id.mp4文件（客户端播放的）
            aac2mp4Cmd.append(webSaveDir + courseUuid + ".txt");
            aac2mp4Cmd.append(" -c copy ");
            String mp4FileName = courseUuid + ".mp4";
            aac2mp4Cmd.append(webSaveDir);
            aac2mp4Cmd.append(mp4FileName);
            LOGGER.info("转mp4文件开始执行cmd命令...");
            LOGGER.info("aac2mp4Cmd.toString()..." + aac2mp4Cmd.toString());
            exec(aac2mp4Cmd.toString(), true);
            //File mp4SaveFile = new File(webSaveDir, mp4FileName);

            /*************上传合并的mp4文件到OSS*********************/

            //创建OSS key 生成规则：文件目录名+文件名称 如：/uploadPath/recordDir/20180627/1593bde7-77c0-4a7f-8c3e-46f60166a5f0.mp4
            mp4OssKey = urlDir.concat(mp4FileName);
            LOGGER.info("uniteRecordNew的mp4OssKey:" + mp4OssKey);
            if (StringUtils.isNotEmpty(mp4OssKey) && mp4OssKey.substring(0, 1).contains("/")) {
                mp4OssKey = mp4OssKey.substring(1, mp4OssKey.length());
                LOGGER.info("截取后mp4OssKey:" + mp4OssKey);
            }
            if (StringUtils.isNotEmpty(mp4FileName)) {
                //磁盘生成的mp4Path /www/clientfile/uploadPath/recordDir/20180702/0_20180702094726187.mp4
                mp4Path = webSaveDir.concat(mp4FileName);
                File file = new File(mp4Path);
                //创建OSSClient客户端
                OSSClient client = OssUtils.generateOssClient();
                InputStream in = new FileInputStream(mp4Path);
             /*   if (StringUtils.isNotEmpty(mp4Path) && mp4Path.substring(0, 1).contains("/")) {
                    mp4Path = mp4Path.substring(1, mp4Path.length());
                }*/
                LOGGER.info("上传mp4到OSS...webSaveDir" + webSaveDir);
                OssUtils.uploadFileInputStream(client, in, webSaveDir, file.length(), mp4OssKey,file);
                client.shutdown();
            } else {
                LOGGER.info("mp4FileName={}不存在！", mp4FileName + "course_uuid={}" + courseUuid);
                throw new Exception("mp4FileName={}不存在！");
            }
        } else {
            LOGGER.info("aacUniteFile={}不存在！", aacUniteFile.getPath());
        }
        //合并轨迹db文件
        String trackComposeDBFileName = courseUuid + "_compose.db";
        //trackComposeDBFilePath=/www/hktRecord/mylogs/20180702/aa1610e3-f7dd-43ef-867d-facb6d9f4d71_compose.db
        String trackComposeDBFilePath = BASEPATH + dateDir + FILESEPARATOR + trackComposeDBFileName;
        File trackComposeDBFile = new File(trackComposeDBFilePath);
        String trackOssKey = null;
        if (trackComposeDBFile.exists()) {
            LOGGER.info("合并轨迹db文件");
            TrackData trackData = CourseUtil.parseComposeDBFile(trackComposeDBFilePath, coursewareService);
            //序列化后保存到数据库中
            byte[] serialize = SerializationUtils.serialize(trackData);
            String serializeDataFileName = trackComposeDBFileName.substring(0, trackComposeDBFileName.lastIndexOf("."));
            //webSaveDir /www/clientfile/uploadPath/recordDir
            File serializeDataFile = new File(webSaveDir, serializeDataFileName);
            FileUtils.writeByteArrayToFile(serializeDataFile, serialize);
            //http://clienttest.haiketang.net/uploadPath/recordDir/20180119/e99fc2d343f84b67a4088c557d9c5462_compose
            //trackComposeUrl = ymlMyConfig.getUploadAddress() + UPLOAD_ROOT + FILESEPARATOR + RECORD_DIR + FILESEPARATOR + dateDir + FILESEPARATOR + serializeDataFileName;
            trackOssKey = urlDir.concat(serializeDataFileName);
            if (StringUtils.isNotEmpty(trackOssKey) && trackOssKey.substring(0, 1).contains("/")) {
                trackOssKey = trackOssKey.substring(1, trackOssKey.length());
                LOGGER.info("截取后trackOssKey:" + trackOssKey);
            }
            /************上传_compose到OSS************************/
            //File file = new File(trackComposeDBFilePath);
            //创建OSSClient客户端
            OSSClient client = OssUtils.generateOssClient();
            InputStream in = new FileInputStream(serializeDataFile);
            OssUtils.uploadFileInputStream(client, in, webSaveDir, serializeDataFile.length(), trackOssKey,serializeDataFile);
            client.shutdown();
        } else {
            LOGGER.info("trackComposeDBFile={}不存在！", trackComposeDBFile.getPath());
        }
        ClassRoom classRoom = new ClassRoom();
        classRoom.setCourseUuid(courseUuid);
        classRoom.setVoiceDuration(totalVoiceDuration);
        // oss url 24小时过期，暂不存，取url 根据oss key
        classRoom.setVideoDuration(totalVideoDuration);
        classRoom.setMp4OssKey(mp4OssKey);
        classRoom.setTrackOssKey(trackOssKey);
        int i = classRoomService.updateByCourseUuid(classRoom);
        if (i == 0) {
            throw new Exception("合并文件URL入库失败！");
        }
    }

    /**
     * 统计学生和cc或cr的音频数据
     * 新增此方法，变动：调用了statisticsSCVoiceDataNew
     *
     * @param courseUuid
     * @param classRoomService
     * @param classMateService
     * @param recordLogsDir
     * @param messageService
     * @param classRecordDataService
     * @param recordDir
     * @param ymlMyConfig
     */
    public static void statisticsSCRecordDataByThreadNew(String courseUuid, ClassRoomService classRoomService, ClassMateService classMateService, File recordLogsDir, MessageService messageService, ClassRecordDataService classRecordDataService, String recordDir, YmlMyConfig ymlMyConfig) {
        ClassRoom room = classRoomService.findByCourseUuid(courseUuid);
        if (room != null) {
            List<ClassMate> classMateList = classMateService.findByClassRoomUuid(room.getClassRoomUuid());
            if (classMateList != null && classMateList.size() > 1) {
                //aac文件转成m4a文件和flac，并获取m4a文件时长，并保存到数据库中
                List<ClassRecordData> classRecordDataList = classRecordDataService.findByClassRoomUuid(room.getClassRoomUuid());
                if (classRecordDataList == null || classRecordDataList.size() == 0) {
                    LOGGER.info("*****课程{}保存SC音频文件线程开始！*****", courseUuid);
                    ClassRoom classRoom = new ClassRoom();
                    classRoom.setCourseUuid(courseUuid);
                    try {
                        statisticsSCVoiceDataNew(courseUuid, recordLogsDir, recordDir, ymlMyConfig, classRoomService, classRecordDataService);
                        classRoom.setStatisticsStatus(2);
                        classRoomService.updateByCourseUuid(classRoom);
                    } catch (Exception e) {
                        LOGGER.info("*****保存SC音频文件异常！*****", e);
                        try {
                            messageService.sendExceptionMessage(SENDPHONE, courseUuid, e.getMessage());
                        } catch (Exception e1) {
                            LOGGER.error(e.toString(), e);
                        }
                        LOGGER.error(e.toString(), e);
                    }
                }
            }
        }
    }

    /**
     * 统计老师和学生的回放数据(音频和轨迹数据)
     *
     * @param courseUuid
     * @param classRoomService
     * @param classMateService
     * @param recordLogsDir
     * @param messageService
     * @param classRecordDataService
     * @param recordDir
     * @param ymlMyConfig
     * @param coursewareService
     */
    public static void statisticsTeacherRecordDataByThread(String courseUuid, ClassRoomService classRoomService, ClassMateService classMateService, File recordLogsDir, MessageService messageService, ClassRecordDataService classRecordDataService, String recordDir, YmlMyConfig ymlMyConfig, CoursewareService coursewareService, Integer comTotalTime) {
        LOGGER.info("statisticsTeacherRecordDataByThread");
        ClassRoom room = classRoomService.findByCourseUuid(courseUuid);
        if (room != null) {
            List<ClassMate> classMateList = classMateService.findByClassRoomUuid(room.getClassRoomUuid());
            if (classMateList != null && classMateList.size() > 1) {
                //aac文件转成m4a文件和flac，并获取m4a文件时长，并保存到数据库中
                List<ClassRecordData> classRecordDataList = classRecordDataService.findByClassRoomUuid(room.getClassRoomUuid());
                if (classRecordDataList == null || classRecordDataList.size() == 0) {
                    new Thread(() -> {
                        LOGGER.info("*****课程{}保存音频和解析轨迹文件线程开始！*****", courseUuid);
                        ClassRoom classRoom = new ClassRoom();
                        classRoom.setCourseUuid(courseUuid);
                        try {
                            statisticsTeacherVoiceData(courseUuid, recordLogsDir, recordDir, ymlMyConfig, classRoomService, classRecordDataService, comTotalTime);
                            List<ClassRecordData> classRecordDataList1 = classRecordDataService.findByClassRoomUuidAndRecordRole(room.getClassRoomUuid(), 0);
                            if (classRecordDataList1 != null && classRecordDataList1.size() != 0) {
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
                    }).start();
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
        ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
        if (classRoom == null) {
            throw new Exception("classRoom is null");
        }
        String pathFileName = courseUuid + ".path";
        File[] listFiles = recordLogsDir.listFiles();
        String content = readFileToString(listFiles, pathFileName);
        LOGGER.info("content={}",content);
        //异常前提：老师学生共同在线的（上过课的）没有录制文件才提示；没有共同上过课的不再提示：
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
        LOGGER.info("contents={}",contents);
        // uploadPath/recordDir/20180702/
        String urlDir = UPLOAD_ROOT.concat("/").concat(RECORD_DIR).concat("/").concat(dateDir).concat("/");
        for (String str : contents) {
            LOGGER.info("str={}",str);
            String[] strs = str.split(":");
            if (strs.length != 4) {
                throw new Exception("path文件数据异常！");
            }
            String recordRole = strs[0];
            if (recordRole.length() != 1) {
                throw new Exception("path文件第一位数据[" + recordRole + "]异常！");
            }
            //只统计学生和老师的音频和轨迹
            if ("0".equals(recordRole)) {
                String aacDirPath = strs[2];
                String aacFilePath = "";
                String m4aFilePath = "";
                String[] aacAndM4aFilePaths = null;

                if (StringUtils.isNotEmpty(aacDirPath)) {
                    aacAndM4aFilePaths = aac2m4a(aacDirPath);
                }
                //如果path文件第二位指向的目录不存在aac文件，不执行转换脚本
                if (StringUtils.isNotEmpty(aacAndM4aFilePaths[0])) {
                    aacFilePath = aacAndM4aFilePaths[0];
                }
                if (StringUtils.isNotEmpty(aacAndM4aFilePaths[1])) {
                    m4aFilePath = aacAndM4aFilePaths[1];
                }

                File aacFile = new File(aacFilePath);
                File m4aFile = new File(m4aFilePath);
                if (!aacFile.exists()) {
                    throw new Exception("aacFile is not exist,aacFilePath:" + aacFilePath);
                }
                if (!m4aFile.exists()) {
                    throw new Exception("m4aFile is not exist,m4aFilePath:" + m4aFilePath);
                }
                //将文件另存
                //复制到saveDir
                String aacSaveFileName = courseUuid + "_" + aacFile.getName();
                File aacSaveFile = new File(saveDir, aacSaveFileName);
                copyFile(aacFile, aacSaveFile);
                //String m4aSaveFileName = courseUuid + "_" + m4aFile.getName();
                //复制到webSaveDir
                //File m4aSaveFile = new File(webSaveDir, m4aSaveFileName);
                //copyFile(m4aFile, m4aSaveFile);

                //获取m4a文件时长
                LOGGER.info("m4aFile.getPath():" + m4aFile.getPath());
                Integer m4aVoiceDuration = getM4aVoiceDuration(m4aFile.getPath());

                /*****(节约OSS空间不再)上传转换后的m4a（多段）到oss****/
                //取磁盘生成的m4a（多段）完整路径
            /*    File m4aFile1 = new File(m4aFilePath);
                OSSClient client1 = OssUtils.generateOssClient();
                if (!m4aFile1.exists()) {
                    throw new Exception("m4aFile1 is not exist,m4aFile1:" + m4aFile1);
                }
                String m4aKey = urlDir.concat(courseUuid).concat("_").concat(m4aFile1.getName());
                //InputStream in2 = new FileInputStream(m4aFilePath);
                //OssUtils.uploadFileInputStream(client1, in2, webSaveDir, m4aFile1.length(), m4aKey,m4aSaveFile);
                OssUtils.uploadFileNew(client1,m4aFile1.getName(),m4aFile1.length(),m4aKey,m4aSaveFile);
                client1.shutdown();*/

                ClassRecordData classRecordData = new ClassRecordData();
                classRecordData.setClassRecordDataUuid(UUIDUtil.randomUUID2());
                classRecordData.setClassRoomUuid(classRoom.getClassRoomUuid());
                classRecordData.setRecordRole(Integer.parseInt(recordRole));
                classRecordData.setVoiceDuration(m4aVoiceDuration);
                classRecordData.setAacVoicePath(aacSaveFile.getPath());
                classRecordDataList.add(classRecordData);
            }
        }
        if (classRecordDataList.size() > 0) {
            classRecordDataService.batchSave(classRecordDataList);
        }
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
        // /www/clientfile/uploadPath/recordDir
        String webSaveDir = recordDir + FILESEPARATOR + dateDir + FILESEPARATOR;
        //存表的url 路径uploadPath/recordDir/20180630/
        String urlDir = UPLOAD_ROOT.concat("/").concat(RECORD_DIR).concat("/").concat(dateDir).concat("/");
        String m4aOssKey = "";
        //m4a文件所在目录完整路径
        String m4aPath;
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
            // webSaveDir=/www/clientfile/uploadPath/recordDir/20190307
            //File m4aSaveFile = new File(webSaveDir, m4aFileName);
            /*******上传合并的m4a文件到OSS**********/
            //创建OSS key 生成规则：文件目录名+文件名称 如：/uploadPath/recordDir/20180627/1593bde7-77c0-4a7f-8c3e-46f60166a5f0.mp4
            m4aOssKey = urlDir.concat(m4aFileName);
          /*  if (StringUtils.isNotEmpty(m4aOssKey) && m4aOssKey.substring(0, 1).contains("/")) {
                m4aOssKey = m4aOssKey.substring(1, m4aOssKey.length());
                LOGGER.info("截取后m4aOssKey:" + m4aOssKey);
            }*/
            //取磁盘生成的m4a文件完整路径 /www/clientfile/uploadPath/recordDir/20180702/课程id.m4a
            m4aPath = webSaveDir.concat(m4aFileName);
            File file = new File(m4aPath);
            OSSClient client = OssUtils.generateOssClient();
            //InputStream in = new FileInputStream(m4aPath);
            //OssUtils.uploadFileInputStream(client, in, urlDir, file.length(), m4aOssKey,file);
            OssUtils.uploadFileNew(client,m4aFileName,file.length(),m4aOssKey,file);
            client.shutdown();
            /***************************************/
         /*   if (m4aSaveFile.exists()) {
                StringBuilder m4a2flacCmd = new StringBuilder();
                m4a2flacCmd.append("/www/hktRecord/tools/ffmpeg -i ");
                m4a2flacCmd.append(m4aSaveFile.getPath());
                m4a2flacCmd.append(" -y ");
            } else {
                LOGGER.info("m4aSaveFile={}不存在！", m4aSaveFile.getPath());
            }*/
        } else {
            LOGGER.info("aacUniteFile={}不存在！", aacUniteFile.getPath());
        }
        //合并轨迹db文件
        String trackComposeDBFileName = courseUuid + "_compose.db";
        String trackComposeDBFilePath = BASEPATH + dateDir + FILESEPARATOR + trackComposeDBFileName;
        File trackComposeDBFile = new File(trackComposeDBFilePath);
        String trackOssKey = "";
        if (trackComposeDBFile.exists()) {
            LOGGER.info("合并轨迹db文件");
            TrackData trackData = CourseUtil.parseComposeDBFile(trackComposeDBFilePath, coursewareService);
            //序列化后保存到数据库中
            byte[] serialize = SerializationUtils.serialize(trackData);
            String serializeDataFileName = trackComposeDBFileName.substring(0, trackComposeDBFileName.lastIndexOf("."));
            //取磁盘生成的compose完整路径
            File serializeDataFile = new File(webSaveDir, serializeDataFileName);
            FileUtils.writeByteArrayToFile(serializeDataFile, serialize);
            trackOssKey = urlDir.concat(serializeDataFileName);
         /*   if (StringUtils.isNotEmpty(trackOssKey) && trackOssKey.substring(0, 1).contains("/")) {
                trackOssKey = trackOssKey.substring(1, trackOssKey.length());
                LOGGER.info("截取后trackOssKey:" + trackOssKey);
            }*/
            /************上传_compose到OSS*******/
            OSSClient client = OssUtils.generateOssClient();
            //InputStream in = new FileInputStream(serializeDataFile);
            //OssUtils.uploadFileInputStream(client, in, webSaveDir, serializeDataFile.length(), trackOssKey,serializeDataFile);
            OssUtils.uploadFileNew(client,serializeDataFileName,serializeDataFile.length(),trackOssKey,serializeDataFile);
            client.shutdown();
        } else {
            LOGGER.info("trackComposeDBFile={}不存在！", trackComposeDBFile.getPath());
        }
        ClassRoom classRoom = new ClassRoom();
        classRoom.setCourseUuid(courseUuid);
        classRoom.setVoiceDuration(totalVoiceDuration);
        classRoom.setM4aOssKey(m4aOssKey);
        classRoom.setTrackOssKey(trackOssKey);
        int i = classRoomService.updateByCourseUuid(classRoom);
        if (i == 0) {
            throw new Exception("合并文件URL入库失败！");
        }
    }

    /**
     * 统计学生和cc或cr的音频数据
     *
     * @param courseUuid
     * @param classRoomService
     * @param classMateService
     * @param recordLogsDir
     * @param messageService
     * @param classRecordDataService
     * @param recordDir
     * @param ymlMyConfig
     */
    public static void statisticsSCRecordDataByThread(String courseUuid, ClassRoomService classRoomService, ClassMateService classMateService, File recordLogsDir, MessageService messageService, ClassRecordDataService classRecordDataService, String recordDir, YmlMyConfig ymlMyConfig, Integer comTotalTime) {
        LOGGER.info("statisticsSCRecordDataByThread");
        ClassRoom room = classRoomService.findByCourseUuid(courseUuid);
        if (room != null) {
            List<ClassMate> classMateList = classMateService.findByClassRoomUuid(room.getClassRoomUuid());
            if (classMateList != null && classMateList.size() > 1) {
                List<ClassRecordData> classRecordDataList = classRecordDataService.findByClassRoomUuid(room.getClassRoomUuid());
                if (classRecordDataList == null || classRecordDataList.size() == 0) {
                    new Thread(() -> {
                        LOGGER.info("*****课程{}保存SC音频文件线程开始！*****", courseUuid);
                        ClassRoom classRoom = new ClassRoom();
                        classRoom.setCourseUuid(courseUuid);
                        try {
                            LOGGER.info("statisticsSCVoiceData");
                            statisticsSCVoiceData(courseUuid, recordLogsDir, recordDir, ymlMyConfig, classRoomService, classRecordDataService, comTotalTime);
                            classRoom.setStatisticsStatus(2);
                            classRoomService.updateByCourseUuid(classRoom);
                        } catch (Exception e) {
                            LOGGER.info("*****保存SC音频文件异常！*****", e);
                            try {
                                messageService.sendExceptionMessage(SENDPHONE, courseUuid, e.getMessage());
                            } catch (Exception e1) {
                                LOGGER.error(e.toString(), e);
                            }
                            LOGGER.error(e.toString(), e);
                        }
                    }).start();
                }
            }
        }
    }

    /**
     * 统计学生和cc或cr的音频并入库
     * 此方法是兼容性修改
     *
     * @param courseUuid
     * @param recordLogsDir
     * @param recordDir
     * @param ymlMyConfig
     * @param classRoomService
     * @param classRecordDataService
     * @throws Exception
     */
    private static void statisticsSCVoiceData(String courseUuid, File recordLogsDir, String recordDir, YmlMyConfig ymlMyConfig, ClassRoomService classRoomService, ClassRecordDataService classRecordDataService, Integer comTotalTime) throws Exception {
        ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
        if (classRoom == null) {
            throw new Exception("classRoom is null");
        }
        String pathFileName = courseUuid + ".path";
        File[] listFiles = recordLogsDir.listFiles();
        String content = readFileToString(listFiles, pathFileName);
        //异常前提：老师学生共同在线的（上过课的）没有录制文件才提示；没有共同上过课的不再提示：
        if (comTotalTime > 0 && StringUtils.isBlank(content)) {
            throw new Exception("老师或学生共同在线，音频录制目录不存在！" + courseUuid);
        }
        if (StringUtils.isBlank(content)) {
            throw new Exception("老师或学生未共同在线！" + courseUuid);
        }
        String dateDir = recordLogsDir.getPath().substring(recordLogsDir.getParentFile().getPath().length() + 1);
        String webSaveDir = recordDir + FILESEPARATOR + dateDir;
        String saveDir = BASESAVEPATH + dateDir;
        List<ClassRecordData> classRecordDataList = new ArrayList<>();
        String[] contents = content.split("\\n");
        // uploadPath/recordDir/20180702/
        String urlDir = UPLOAD_ROOT.concat("/").concat(RECORD_DIR).concat("/").concat(dateDir).concat("/");
        for (String str : contents) {
            String[] strs = str.split(":");
            if (strs.length != 4) {
                throw new Exception("path文件数据异常！");
            }
            String recordRole = strs[0];
            //统计学生和cc或cr的音频
            if (!"0".equals(recordRole)) {
                String aacDirPath = strs[2];
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
                copyFile(aacFile, aacSaveFile);
                String m4aSaveFileName = courseUuid + "_" + m4aFile.getName();
                File m4aSaveFile = new File(webSaveDir, m4aSaveFileName);
                copyFile(m4aFile, m4aSaveFile);

                //获取m4a文件时长
                Integer m4aVoiceDuration = 0;
                if (StringUtils.isNotEmpty(m4aSaveFile.getPath())) {
                    m4aVoiceDuration = getM4aVoiceDuration(m4aSaveFile.getPath());
                }
                /***********************上传m4a到oss*******/
                OSSClient client1 = OssUtils.generateOssClient();
                String m4aKey = urlDir.concat(courseUuid).concat("_").concat(m4aFile.getName());
                //InputStream in2 = new FileInputStream(m4aFilePath);
                //OssUtils.uploadFileInputStream(client1, in2, webSaveDir, m4aFile.length(), m4aKey,m4aFile);
                OssUtils.uploadFileNew(client1,m4aFile.getName(),m4aFile.length(),m4aKey,m4aFile);
                client1.shutdown();
                /*****************************************/

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
                classRecordData.setAacVoicePath(aacSaveFile.getPath());
                classRecordDataList.add(classRecordData);
                LOGGER.info("存classRecordData入参:" + UUIDUtil.randomUUID2() + "," + classRoom.getClassRoomUuid() + "," + Integer.parseInt(recordRole) + "," + m4aVoiceDuration + "," + aacSaveFile.getPath());
            }
        }

        if (classRecordDataList.size() > 0) {
            classRecordDataService.batchSave(classRecordDataList);
        }
    }
}
