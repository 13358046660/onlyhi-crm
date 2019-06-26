package cn.onlyhi.file.util;

import cn.onlyhi.client.dto.ClassRecordDBData;
import cn.onlyhi.client.dto.TrackData;
import cn.onlyhi.client.po.*;
import cn.onlyhi.client.service.*;
import cn.onlyhi.common.enums.ClientEnum;
import cn.onlyhi.common.util.CmdExecResult;
import cn.onlyhi.common.util.OssTeacherUtils;
import cn.onlyhi.common.util.OssUtils;
import cn.onlyhi.common.util.UUIDUtil;
import cn.onlyhi.file.config.YmlMyConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static cn.onlyhi.common.constants.Constants.*;
import static cn.onlyhi.common.enums.ClientEnum.UserType.STUDENT;
import static cn.onlyhi.common.enums.ClientEnum.UserType.TEACHER;
import static cn.onlyhi.common.util.ClientUtil.*;
import static cn.onlyhi.common.util.CmdExecuteUtil.exec;
import static cn.onlyhi.common.util.FileUtils.readFileToString;
import static cn.onlyhi.common.util.TrackUtil.wrapTrackData;
import static cn.onlyhi.common.util.TrackUtil.wrapTrackDataNew2;
import static org.apache.commons.io.FileUtils.copyFile;

/**
 * 课程有关工具类
 *
 * @Author wqz
 * <p>
 * Created by wqz on 2018/09/29.
 */
public class CourseUtilExtend {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseUtilExtend.class);
    /**
     * 解析客户端进出房间记录，统计共同在线时间段
     *
     * @param courseUuid
     * @param recordLogsDir
     * @throws Exception
     */
    public static Map<Long,Long> statisticsClassTime(String courseUuid, File recordLogsDir, ClassMateService classMateService, ClassRecordService classRecordService, ClassRoomService classRoomService, CpCourseService cpCourseService) throws Exception {
        LOGGER.info("解析客户端进出记录,课程id.db");
        List<ClassRecordDBData> dataList = parseClassRecordDBFile(courseUuid, recordLogsDir);
        if (dataList == null || dataList.size() == 0) {
            throw new Exception("courseUuid=" + courseUuid + " :客户端进出房间记录数据不存在！");
        }
        //入库class_record
        int i = saveClassRecordDBData(dataList, classMateService, classRecordService);
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

        LOGGER.info("统计有效时间段.");
        Map<Long,Long> totalTimeMap = statisticsClassTime(courseDate, startTime, endTime, classRecordList);
        return  totalTimeMap;
    }
    /**
     * 统计有效时间段
     *
     * @param courseDate      课程日期
     * @param startTime       开始时间
     * @param endTime         结束时间
     * @param classRecordList 课程记录
     * @return
     * @throws Exception
     */
    public static Map<Long, Long> statisticsClassTime(String courseDate, String startTime, String endTime, List<ClassRecord> classRecordList) throws Exception {
        long courseStartTime = DateUtils.parseDate(courseDate + startTime, "yyyy-MM-ddHH:mm").getTime();
        long courseEndTime = DateUtils.parseDate(courseDate + endTime, "yyyy-MM-ddHH:mm").getTime();
        List<ClassRecord> stuClassRecordList = new ArrayList<>();
        List<ClassRecord> teaClassRecordList = new ArrayList<>();
        if (classRecordList.size() > 0) {
            for (ClassRecord classRecord : classRecordList) {
                String userType = classRecord.getUserType();
                if (STUDENT.name().equals(userType)) {
                    stuClassRecordList.add(classRecord);
                } else if (TEACHER.name().equals(userType)) {
                    teaClassRecordList.add(classRecord);
                }
            }
        }
        //学生上课进出时间 key:进入时间 value:退出时间
        Map<Long, Long> stuTimeMap = getEnterAndOutTimeMap(stuClassRecordList, courseStartTime, courseEndTime);

        //教师上课进出时间 key:进入时间 value:退出时间
        Map<Long, Long> teaTimeMap = getEnterAndOutTimeMap(teaClassRecordList, courseStartTime, courseEndTime);

        //学生和教师上课共同时长段
        Map<Long,Long> totalTimeMap = getTotalTime(stuTimeMap, teaTimeMap);

        return totalTimeMap;
    }
    /**
     * 获取学生和老师公共的在线时长
     *
     * @param stuTimeMap
     * @param teaTimeMap
     * @return
     */
    private static Map<Long, Long> getTotalTime(Map<Long, Long> stuTimeMap, Map<Long, Long> teaTimeMap) {
        //long totalTime = 0;
        //共同时长段
        Map<Long, Long> totalTimeMap=new HashMap<>();
        Set<Map.Entry<Long, Long>> stuTimeSet = stuTimeMap.entrySet();
        Set<Map.Entry<Long, Long>> teaTimeSet = teaTimeMap.entrySet();
        for (Map.Entry<Long, Long> stuTime : stuTimeSet) {
            long stuEnterTime = stuTime.getKey();
            long stuOutTime = stuTime.getValue();
            for (Map.Entry<Long, Long> teaTime : teaTimeSet) {
                long teaEnterTime = teaTime.getKey();
                long teaOutTime = teaTime.getValue();
                if (teaEnterTime < stuEnterTime) {  //教师进入时间小于学生进入时间，即教师比学生早进入房间
                    if (teaOutTime < stuEnterTime) { //教师退出时间小于学生进入时间，此时没有公共时间
                        continue;
                    } else if (teaOutTime > stuOutTime) {   //教师退出时间大于学生退出时间，此时公共时间为学生的在线时间
                        //totalTime += stuOutTime - stuEnterTime;
                        totalTimeMap.put(stuEnterTime,stuOutTime);
                    } else {  //教师退出时间在学生进入和退出时间之间，此时公共时间为学生的进入时间到教师的退出时间
                        //totalTime += teaOutTime - stuEnterTime;
                        totalTimeMap.put(stuEnterTime,stuOutTime);
                    }
                } else if (teaEnterTime > stuOutTime) {   //教师进入时间大于学生退出时间，即教师和学生的在线时间段没有交集
                    continue;
                } else { //教师进入时间在学生进入时间和退出时间之间
                    if (teaOutTime > stuOutTime) {  //教师退出时间大于学生退出时间，此时公共时间为教师的进入时间到学生的退出时间之间时间段
                        //totalTime += stuOutTime - teaEnterTime;
                        totalTimeMap.put(stuEnterTime,stuOutTime);
                    } else if (teaOutTime < stuEnterTime) {   //教师退出时间小于学生进入时间（这种情况理论上是不可能发生的）
                        continue;
                    } else {  //教师退出时间在学生进入时间和退出时间之间，此时公共时间为教师的在线时间
                        //totalTime += teaOutTime - teaEnterTime;
                        totalTimeMap.put(stuEnterTime,stuOutTime);
                    }
                }
            }
        }
        return totalTimeMap;
    }
    /**
     * 获取有效的进出时间
     *
     * @param classRecordList
     * @param courseStartTime
     * @param courseEndTime
     * @return Map<Long, Long>  key:进入时间    value:退出时间
     * @throws Exception
     */
    private static Map<Long, Long> getEnterAndOutTimeMap(List<ClassRecord> classRecordList, long courseStartTime, long courseEndTime) throws Exception {
        LOGGER.info("获取有效的进出时间.");
        List<Long> enterTimeList = new ArrayList<>();
        List<Long> outTimeList = new ArrayList<>();
        for (ClassRecord classRecord : classRecordList) {
            Integer recordType = classRecord.getRecordType();
            Long recordTime = classRecord.getRecordTime();
            if (Objects.equals(recordType, ClientEnum.RecordType.ENTER.key)) {
                enterTimeList.add(recordTime);
            } else if (Objects.equals(recordType, ClientEnum.RecordType.OUT.key)) {
                outTimeList.add(recordTime);
            } else {
                LOGGER.info("异常recordType={}", recordType);
            }
        }
        LOGGER.info("enterTimeList={},outTimeList={}", enterTimeList, outTimeList);
        int size = enterTimeList.size();
        if (size != outTimeList.size()) {
            //进时间次数与出时间次数不一致
            LOGGER.error("进出时间不匹配，或无录制文件！");
        }
        //对进出时间过滤
        ListIterator<Long> enterTimeIterator = enterTimeList.listIterator();
        ListIterator<Long> outTimeIterator = outTimeList.listIterator();
        while (enterTimeIterator.hasNext() && outTimeIterator.hasNext()) {
            long enterTime = enterTimeIterator.next();
            long outTime = outTimeIterator.next();
            if (enterTime > courseEndTime || outTime < courseStartTime) {
                //LOGGER.info("进入时间大于课程结束时间，不做统计,或者退出时间小于课程开始时间，不做统计.");
                enterTimeIterator.remove();
                outTimeIterator.remove();
            }
        }
        LOGGER.info("过滤后：enterTimeList={},outTimeList={}", enterTimeList, outTimeList);
        Map<Long, Long> timeMap = new HashMap<>();
        size = enterTimeList.size();
        Long outTime;
        for (int i = 0; i < size; i++) {
            Long enterTime = enterTimeList.get(i);
            if (outTimeList.size() > 0 && outTimeList.size() <= enterTimeList.size()) {
                if(i<=outTimeList.size()){
                    outTime = outTimeList.get(i);
                }else{
                    outTime = Long.valueOf(0);
                }
            } else {
                outTime = Long.valueOf(0);
            }
            //第一次进入时间小于课程开始时间，按课程开始时间算
            if (i == 0 && enterTime < courseStartTime) {
                enterTime = courseStartTime;
            }
            //最后一次退出时间大于课程结束时间，按课程结束时间算
            if (i == size - 1 && outTime > courseEndTime) {
                outTime = courseEndTime;
            }
            //没有正常退出房间时间，按课程结束时间算；无正常退出房间情况有直接关闭了客户端，客户端倒计时没有触发成功下课或其它不正常情况，
            //鉴于之前没有正常退出时间，无法准确统计出两者有效进出时间算共同时长，所以按课程结束时间算
            if(Objects.equals(outTime,0)){
                outTime = courseEndTime;
            }
            timeMap.put(enterTime, outTime);
        }
        return timeMap;
    }
    /**
     * 将记录数据保存到数据库中
     *
     * @param dataList
     * @return
     * @throws Exception
     */
    private static int saveClassRecordDBData(List<ClassRecordDBData> dataList, ClassMateService classMateService, ClassRecordService classRecordService) throws Exception {
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
                recordId = userid;
                continue;
            }

            if (i == size - 1) {
                Collection<ClassMate> values = classMateService.findByRecordId(userid);
                Iterator<ClassMate> iterator = values.iterator();
                while (iterator.hasNext()) {
                    ClassMate classMate = iterator.next();
                    Integer agoraUid = classMate.getAgoraUid();
                    Integer agoraStatus = useridStatusMap.get(agoraUid);
                    if (agoraStatus != null && agoraStatus == 0) {
                        LOGGER.info("录制退出之前已经退出");
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
                classMate = classMateService.findByAgoraUidAndRecordId(userid, recordId);
                if (userid > 0 && classMate != null) {
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
     * 解析上课记录数据
     *
     * @param courseUuid
     * @param recordLogsDir
     * @return
     * @throws Exception
     */
    private static List<ClassRecordDBData> parseClassRecordDBFile(String courseUuid, File recordLogsDir) throws Exception {
        String classRecordDBFileName = courseUuid + "_user_course.db";
        String classRecordDBPath = recordLogsDir.getPath() + FILESEPARATOR + classRecordDBFileName;
        File destFile = null;
        try {
            String dateDir = recordLogsDir.getPath().substring(recordLogsDir.getParentFile().getPath().length() + 1);
            String saveDir = BASESAVEPATH + dateDir;
            destFile = new File(saveDir, classRecordDBFileName);
            copyFile(new File(classRecordDBPath), destFile);
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
        }
        if (destFile == null || !destFile.exists()) {
            throw new Exception("classRecordDBFileName=" + classRecordDBFileName + "不存在！");
        }
        String sql = "jdbc:sqlite://" + destFile.getPath();
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
                    conn.close(); // 结束数据库的连接
                } catch (SQLException e) {
                    LOGGER.error(e.toString(), e);
                }
            }
        }
        return dataList;
    }
}


