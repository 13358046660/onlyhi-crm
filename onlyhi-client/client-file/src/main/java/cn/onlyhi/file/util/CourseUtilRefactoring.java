package cn.onlyhi.file.util;

import cn.onlyhi.client.dto.ClassRecordDBData;
import cn.onlyhi.client.dto.TrackData;
import cn.onlyhi.client.po.*;
import cn.onlyhi.client.service.*;
import cn.onlyhi.common.enums.ClientEnum;
import cn.onlyhi.common.util.CmdExecResult;
import cn.onlyhi.common.util.UUIDUtil;
import cn.onlyhi.file.config.YmlMyConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;
import static org.apache.commons.io.FileUtils.copyFile;

/**
 * 课程录制、课时计算
 * 此类在原CourseUtil重构，验证后可替换CourseUtil
 *
 * @Author WQZ
 * <p>
 * Created by WQZ on 2018/7/28.
 */
public class CourseUtilRefactoring {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseUtilRefactoring.class);

    /**
     * 解析客户端进出房间记录，统计课时(通过线程执行)
     * 没有录制结束标识的，恢复不了record记录了
     *
     * @param courseUuid
     * @param recordLogsDir
     * @param classMateService
     * @param classRecordService
     * @param classRoomService
     * @param cpCourseService
     * @throws Exception
     */
    public static void parseAndStatisticsClassRecordDBDataByThread(String courseUuid, File recordLogsDir, ClassMateService classMateService, ClassRecordService classRecordService, ClassRoomService classRoomService, CpCourseService cpCourseService, MessageService messageService, Integer curRecordId) {
        try {
            ///www/hktRecord/mylogs/恢复发现从20171229到20180816共2335个“没有录制结束标识！”的，其中可能有一部分是真正没有录制结束的
            //去掉此校验，全部再恢复
            //boolean verifyFlag = verifyRecordEnd(courseUuid, recordLogsDir);
            //if (verifyFlag) {
            parseAndStatisticsClassRecordDBData(courseUuid, recordLogsDir, classMateService, classRecordService, classRoomService, cpCourseService, curRecordId);
            //} else {
            //LOGGER.error("courseUuid=" + courseUuid + " 没有录制结束标识！");
            //}
        } catch (Exception e) {
            LOGGER.error("*****courseUuid=" + courseUuid + "解析客户端进出房间记录，统计课时线程异常！*****", e);
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
     * 此方法主要恢复现有存在.end文件等，所以不用次数重试文件是否存在
     *
     * @param courseUuid
     * @param recordLogsDir
     * @return
     * @throws InterruptedException
     */
    private static boolean verifyRecordEnd(String courseUuid, File recordLogsDir) throws InterruptedException {
        LOGGER.info("检查录制结束标识文件！");
        String endFlagPath = recordLogsDir.getPath() + FILESEPARATOR + courseUuid + ".end";
        LOGGER.info("endFlagPath=" + endFlagPath);
        File endFlagFile = new File(endFlagPath);
        if (endFlagFile.exists()) {
            LOGGER.info("true");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 白板数据
     *
     * @param points
     * @param drawMode
     * @param time0
     * @param color
     * @param lineWidth
     * @param boardNo
     * @param jsonObject
     * @param drawList
     * @param flag       false:正常画    true：一瞬间画
     * @param timeList
     */
    private static void drawData(String points, String drawMode, String time0, String color, String lineWidth, int boardNo, JSONObject jsonObject, List<String[]> drawList, boolean flag, List<Integer> timeList) {
        String[] ps = points.split("\\|");
        if (DRAWMODE_03.equals(drawMode)) { //画矩形
            String point1 = ps[0];
            String point2 = ps[1];
            String[] p = point1.split(",");
            String[] p1 = point2.split(",");

            String x1 = p[0];
            String x2 = p1[0];

            String y1 = p[1];
            String y2 = p1[1];

            String tempX = "";
            String tempY = "";
            //判断反着正着画矩形
            if (Integer.parseInt(x2) - Integer.parseInt(x1) > 0) {//顺着画
                if (Integer.parseInt(y2) - Integer.parseInt(y1) > 0) {
                    tempX = x1;
                    tempY = y1;
                } else if (Integer.parseInt(y2) - Integer.parseInt(y1) < 0) {
                    tempX = x1;
                    tempY = y2;
                }
            } else if (Integer.parseInt(x2) - Integer.parseInt(x1) < 0) {//反着画
                if (Integer.parseInt(y2) - Integer.parseInt(y1) > 0) {
                    tempX = x2;
                    tempY = y1;
                } else if (Integer.parseInt(y2) - Integer.parseInt(y1) < 0) {
                    tempX = x2;
                    tempY = y2;
                }
            }
            String[] rectWH = getRectWH(point1, point2);
            drawList.add(wrapTrackData(tempX, tempY, time0, color, lineWidth, drawMode, String.valueOf(boardNo), rectWH[0], rectWH[1]));
        } else if (DRAWMODE_04.equals(drawMode)) {   //画圆
            String point1 = ps[0];
            String point2 = ps[1];
            String[][] circleInfo = getCircleInfo(point1, point2);
            drawList.add(wrapTrackData(circleInfo[1][0], circleInfo[1][1], time0, color, lineWidth, drawMode, String.valueOf(boardNo), String.valueOf(circleInfo[0][0]), String.valueOf(circleInfo[0][1])));
        } else if (DRAWMODE_05.equals(drawMode)) {    //写文字
            String point1 = ps[0];  //文字起点坐标
            String[] p = point1.split(",");
            String x = p[0];
            String y = p[1];
            String size = jsonObject.getString("size"); //文字大小
            String text = jsonObject.getString("text"); //文字内容
            String textColor = jsonObject.getString("textColor");   //文字颜色
            drawList.add(wrapTrackData(x, y, time0, textColor, size, drawMode, String.valueOf(boardNo), text));
        } else {
            int length = ps.length;
            for (int i = 0; i < length; i++) {
                String[] p = ps[i].split(",");
                String x = p[0];
                String y = p[1];
                String time = p[2];
                if (timeList != null) {
                    time = String.valueOf(Integer.parseInt(time) + getDurationTime(timeList));
                }
                if (flag) {
                    time = time0;
                }
                if (i == 0) {
                    //画线起点做标识"="
                    drawList.add(wrapTrackData("=" + x, y, time, color, lineWidth, drawMode, String.valueOf(boardNo)));
                } else {
                    drawList.add(wrapTrackData(x, y, time, color, lineWidth, drawMode, String.valueOf(boardNo)));
                }
            }
        }
    }

    //保留drawData新增此方法用于判断画实心图
    private static void drawDataNew(String points, String drawMode, String time0, String color, String lineWidth, int boardNo, JSONObject jsonObject, List<String[]> drawList, boolean flag, List<Integer> timeList, String rectangularSolid, String roundSolid) {
        String[] ps = points.split("\\|");
        if (DRAWMODE_03.equals(drawMode)) { //画矩形
            String point1 = ps[0];
            String point2 = ps[1];
            String[] p = point1.split(",");
            String[] p1 = point2.split(",");

            String x1 = p[0];
            String x2 = p1[0];

            String y1 = p[1];
            String y2 = p1[1];

            String tempX = "";
            String tempY = "";
            //判断反着正着画矩形
            if (Integer.parseInt(x2) - Integer.parseInt(x1) > 0) {//顺着画
                if (Integer.parseInt(y2) - Integer.parseInt(y1) > 0) {
                    tempX = x1;
                    tempY = y1;
                } else if (Integer.parseInt(y2) - Integer.parseInt(y1) < 0) {
                    tempX = x1;
                    tempY = y2;
                }
            } else if (Integer.parseInt(x2) - Integer.parseInt(x1) < 0) {//反着画
                if (Integer.parseInt(y2) - Integer.parseInt(y1) > 0) {
                    tempX = x2;
                    tempY = y1;
                } else if (Integer.parseInt(y2) - Integer.parseInt(y1) < 0) {
                    tempX = x2;
                    tempY = y2;
                }
            }
            String[] rectWH = getRectWH(point1, point2);
            drawList.add(wrapTrackDataNew2(tempX, tempY, time0, color, lineWidth, drawMode, String.valueOf(boardNo), rectWH[0], rectWH[1], rectangularSolid, null));
        } else if (DRAWMODE_04.equals(drawMode)) {   //画圆
            String point1 = ps[0];
            String point2 = ps[1];
            String[][] circleInfo = getCircleInfo(point1, point2);
            drawList.add(wrapTrackDataNew2(circleInfo[1][0], circleInfo[1][1], time0, color, lineWidth, drawMode, String.valueOf(boardNo), String.valueOf(circleInfo[0][0]), String.valueOf(circleInfo[0][1]), null, roundSolid));
        } else if (DRAWMODE_05.equals(drawMode)) {    //写文字
            String point1 = ps[0];  //文字起点坐标
            String[] p = point1.split(",");
            String x = p[0];
            String y = p[1];
            String size = jsonObject.getString("size"); //文字大小
            String text = jsonObject.getString("text"); //文字内容
            String textColor = jsonObject.getString("textColor");   //文字颜色
            drawList.add(wrapTrackData(x, y, time0, textColor, size, drawMode, String.valueOf(boardNo), text));
        } else {
            int length = ps.length;
            for (int i = 0; i < length; i++) {
                String[] p = ps[i].split(",");
                String x = p[0];
                String y = p[1];
                String time = p[2];
                if (timeList != null) {
                    time = String.valueOf(Integer.parseInt(time) + getDurationTime(timeList));
                }
                if (flag) {
                    time = time0;
                }
                if (i == 0) {
                    //画线起点做标识"="
                    drawList.add(wrapTrackData("=" + x, y, time, color, lineWidth, drawMode, String.valueOf(boardNo)));
                } else {
                    drawList.add(wrapTrackData(x, y, time, color, lineWidth, drawMode, String.valueOf(boardNo)));
                }
            }
        }
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
            LOGGER.info("recordLogsDir=" + recordLogsDir);
            LOGGER.info("courseUuid=" + courseUuid + " :客户端进出房间记录数据不存在！");
        }
        //入库
        int i = saveClassRecordDBData(dataList, classMateService, classRecordService, curRecordId);
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
        StatisticsClassTimeResult statisticsClassTimeResult = statisticsClassTime(courseDate, startTime, endTime, classRecordList);
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
     * 统计课时
     *
     * @param courseDate      课程日期
     * @param startTime       开始时间
     * @param endTime         结束时间
     * @param classRecordList 课程记录
     * @return
     * @throws Exception
     */
    public static StatisticsClassTimeResult statisticsClassTime(String courseDate, String startTime, String endTime, List<ClassRecord> classRecordList) throws Exception {
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
        //学生上课时长
        long stuTotalTime = getTotalTime(stuTimeMap);

        //教师上课进出时间 key:进入时间 value:退出时间
        Map<Long, Long> teaTimeMap = getEnterAndOutTimeMap(teaClassRecordList, courseStartTime, courseEndTime);
        //教师上课时长
        long teaTotalTime = getTotalTime(teaTimeMap);

        //学生和教师上课共同时长（同时在线时间累加）
        long totalTime = getComTotalTime(stuTimeMap, teaTimeMap);

        LOGGER.info("stuTotalTime={}ms,teaTotalTime={}ms,totalTime={}ms", stuTotalTime, teaTotalTime, totalTime);
        StatisticsClassTimeResult result = new StatisticsClassTimeResult();
        double totalTemp = totalTime / 60000.0;

        //共同时长四舍五入更准确
        result.setComTotalTime((int) Math.round(totalTemp));
        result.setStudentTotalTime((int) stuTotalTime / 60000);
        result.setTeacherTotalTime((int) teaTotalTime / 60000);
        long differTime = Math.abs(courseEndTime - courseStartTime - totalTime) / 60000;
        if (differTime > 4) {
            //误差四分钟，上课有异常
            LOGGER.info("排课时间内，少上课时长超过4分钟.");
            result.setIsSuccess(3);
        } else {
            result.setIsSuccess(1);
        }
        return result;
    }

    /**
     * 获取学生和老师公共的在线时长
     *
     * @param stuTimeMap
     * @param teaTimeMap
     * @return
     */
    private static long getComTotalTime(Map<Long, Long> stuTimeMap, Map<Long, Long> teaTimeMap) {
        long totalTime = 0;
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
                        if (stuOutTime > stuEnterTime) {
                            totalTime += stuOutTime - stuEnterTime;
                        }
                    } else {  //教师退出时间在学生进入和退出时间之间，此时公共时间为学生的进入时间到教师的退出时间
                        if (teaOutTime > stuEnterTime) {
                            totalTime += teaOutTime - stuEnterTime;
                        }
                    }
                } else if (teaEnterTime > stuOutTime) {   //教师进入时间大于学生退出时间，即教师和学生的在线时间段没有交集
                    continue;
                } else { //教师进入时间在学生进入时间和退出时间之间
                    if (teaOutTime > stuOutTime) {  //教师退出时间大于学生退出时间，此时公共时间为教师的进入时间到学生的退出时间之间时间段
                        if (stuOutTime > teaEnterTime) {
                            totalTime += stuOutTime - teaEnterTime;
                        }
                    } else if (teaOutTime < stuEnterTime) {   //教师退出时间小于学生进入时间（这种情况理论上是不可能发生的）
                        continue;
                    } else {  //教师退出时间在学生进入时间和退出时间之间，此时公共时间为教师的在线时间
                        if (teaOutTime > teaEnterTime) {
                            totalTime += teaOutTime - teaEnterTime;
                        }
                    }
                }
            }
        }
        return totalTime;
    }

    /**
     * 获取总在线时间
     *
     * @param timeMap
     * @return
     */
    private static long getTotalTime(Map<Long, Long> timeMap) {
        long totalTime = 0;
        Set<Map.Entry<Long, Long>> entrySet = timeMap.entrySet();
        for (Map.Entry<Long, Long> entry : entrySet) {
            //无退出时间的,时间指定的是0，不累加，否则totalTime会有负数
            if (entry.getValue() > 0) {
                totalTime += entry.getValue() - entry.getKey();
            }
        }
        return totalTime;
    }
/*
    public static void main(String[] args) {
        List<Long> enterTimeList = new ArrayList<>();
        List<Long> outTimeList = new ArrayList<>();
        List<ClassRecord> classRecordList=new ArrayList<>();
        ClassRecord classRecord=new ClassRecord();
        classRecord.setUserUuid("1077007735");
        classRecord.setStatus(1);
        classRecord.setRecordTime(Long.valueOf("1548208862514"));
        classRecord.setRecordType(1);
        classRecordList.add(classRecord);

        classRecord.setUserUuid("175860422");
        classRecord.setStatus(1);
        classRecord.setRecordTime(Long.valueOf("1548208864609"));
        classRecord.setRecordType(1);

        classRecordList.add(classRecord);

        classRecord.setUserUuid("424633216");
        classRecord.setStatus(1);
        classRecord.setRecordTime(Long.valueOf("1548208927877"));
        classRecord.setRecordType(1);

        classRecordList.add(classRecord);

        for (ClassRecord classRecord1: classRecordList) {
            Integer recordType = classRecord1.getRecordType();
            Long recordTime = classRecord1.getRecordTime();
            if (Objects.equals(recordType, ClientEnum.RecordType.ENTER.key)) {
                enterTimeList.add(recordTime);
            } else if (Objects.equals(recordType, ClientEnum.RecordType.OUT.key)) {
                outTimeList.add(recordTime);
            } else {
                LOGGER.info("异常recordType={}", recordType);
            }
        }
        LOGGER.info("enterTimeList={},outTimeList={}", enterTimeList, outTimeList);
    }*/

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
            LOGGER.error("进出时间不匹配，或无录制文件！");
        }
        //对进出时间过滤
        ListIterator<Long> enterTimeIterator = enterTimeList.listIterator();
        ListIterator<Long> outTimeIterator = outTimeList.listIterator();
        while (enterTimeIterator.hasNext() && outTimeIterator.hasNext()) {
            long enterTime = enterTimeIterator.next();
            long outTime = outTimeIterator.next();
            if (enterTime > courseEndTime || outTime < courseStartTime) {
                LOGGER.info("进入时间大于课程结束时间,或者退出时间小于课程开始时间，不做统计");
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
            //如果enterTimeList.size()>outTimeList.size(),避免java.lang.IndexOutOfBoundsException: Index: 1, Size: 1
            if (outTimeList.size() > 0 && outTimeList.size() <= enterTimeList.size()) {
                if (i < outTimeList.size()) {
                    outTime = outTimeList.get(i);
                } else {
                    outTime = Long.valueOf(0);
                }
            } else {
                outTime = Long.valueOf(0);
            }
            if (i == 0 && enterTime < courseStartTime) {
                //第一次进入时间小于课程开始时间，按课程开始时间算
                enterTime = courseStartTime;
            }
            if (i == size - 1 && outTime > courseEndTime) {   //最后一次退出时间大于课程结束时间，按课程结束时间算
                outTime = courseEndTime;
            }
            //没有正常退出房间时间，按课程结束时间算；无正常退出房间情况有直接关闭了客户端，客户端倒计时没有触发成功下课或其它不正常情况，
            //鉴于之前没有正常退出时间，无法准确统计出两者有效进出时间算共同时长，所以按课程结束时间算
            if (Objects.equals(outTime, 0)) {
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
    private static int saveClassRecordDBData(List<ClassRecordDBData> dataList, ClassMateService classMateService, ClassRecordService classRecordService, Integer curRecordId) throws Exception {
        LOGGER.info("saveClassRecordDBData.");
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
            if (i == 0) {
                LOGGER.info("user_course.db 录制id" + userid);
            } else {
                LOGGER.info("user_course.db userid" + userid);
            }
            LOGGER.info("status" + status);
            LOGGER.info("timestamp" + timestamp);
            if (i == 0) {
                //curRecordId第一次生成的是录制id（user_course.db第一条生成的是录制id，第二条以后是学生或老师或监课角色）
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
                    continue;
                }
            }
            ClassMate classMate = classMateMap.get(userid);
            if (classMate == null) {
                LOGGER.info("recordId={}", recordId);
                classMate = classMateService.findByAgoraUidAndRecordId(userid, recordId);
                LOGGER.info("classMate={}", classMate);
                LOGGER.info("userid={}", userid);
                // if (key == null || value == null) throw new NullPointerException()避免异常
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
            LOGGER.error("学生与老师回放数据未统计成功，可能没生成path或end文件");
            throw new Exception("classRecordList.size() = 0！！！");
        }
        return classRecordService.batchSave(classRecordList);
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
        LOGGER.info("classRecordDBFileName." + classRecordDBFileName);
        String classRecordDBPath = recordLogsDir.getPath() + FILESEPARATOR + classRecordDBFileName;
        LOGGER.info("recordLogsDir.getPath():" + recordLogsDir.getPath());
        if (recordLogsDir == null || !recordLogsDir.exists()) {
            LOGGER.error("classRecordDBFileName=" + classRecordDBFileName + "不存在！");
        }
        String sql = "jdbc:sqlite://" + recordLogsDir.getPath().concat("/").concat(classRecordDBFileName);
        //String sql = "jdbc:sqlite://" + "D:/线上轨迹文件/4EEC9534-ECAF-4EB1-9723-306B25E8EBAE_user_course.db";
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
            if (rs != null) {
                while (rs.next()) {
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
            } else {
                LOGGER.info("没有共同在线所以客户端无进出房间记录且无录制文件.");
            }

        } catch (Exception e) {
            LOGGER.info("正在解析{}第【{}】行记录", classRecordDBPath, row);
            LOGGER.error(e.toString(), e);
        } finally {
            //java中Connection，Statement，ResultSet的关闭顺序是从后往前的
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
                    LOGGER.info("conn.close() 3");
                } catch (SQLException e) {
                    LOGGER.error(e.toString(), e);
                }
            }
        }
        LOGGER.info("ClassRecordDBData dataList.size():" + dataList.size());
        return dataList;
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
        String flacVoiceUrl = null;
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
                //String flacVoiceName = m4aSaveFile.getName().replace(".m4a", ".flac");
                //String flacFilePath = webSaveDir + flacVoiceName;
                //m4a2flacCmd.append(flacFilePath);
                //exec(m4a2flacCmd.toString(), true);
                //flacVoiceUrl = voiceBasePath + flacVoiceName;
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
            LOGGER.info("合并轨迹db文件");
            TrackData trackData = parseComposeDBFile(trackComposeDBFilePath, coursewareService);

            //序列化后保存到数据库中
            byte[] serialize = SerializationUtils.serialize(trackData);
            String serializeDataFileName = trackComposeDBFileName.substring(0, trackComposeDBFileName.lastIndexOf("."));
            File serializeDataFile = new File(webSaveDir, serializeDataFileName);
            FileUtils.writeByteArrayToFile(serializeDataFile, serialize);
            trackComposeUrl = ymlMyConfig.getUploadAddress() + UPLOAD_ROOT + FILESEPARATOR + RECORD_DIR + FILESEPARATOR + dateDir + FILESEPARATOR + serializeDataFileName;
        } else {
            LOGGER.info("trackComposeDBFile={}不存在！", trackComposeDBFile.getPath());
        }

        ClassRoom classRoom = new ClassRoom();
        classRoom.setCourseUuid(courseUuid);
        classRoom.setVoiceUrl(m4aVoiceUrl);
        classRoom.setFlacVoiceUrl(flacVoiceUrl);
        classRoom.setVoiceDuration(totalVoiceDuration);
        classRoom.setTrackUrl(trackComposeUrl);
        int i = classRoomService.updateByCourseUuid(classRoom);
        if (i == 0) {
            throw new Exception("合并文件URL入库失败！");
        }
    }

    /**
     * 录制加入了视频，需要再生成mp4文件
     *
     * @param courseUuid
     * @param
     * @return
     * @throws Exception
     * @author wqz 2018.5.31
     */
    public static void uniteRecordNew(List<ClassRecordData> classRecordDataList, String courseUuid, String recordDir, YmlMyConfig ymlMyConfig, ClassRoomService classRoomService, CoursewareService coursewareService) throws Exception {
        LOGGER.info("开始调用uniteRecordNew录制加入了视频，需要再生成mp4文件");
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
        String webSaveDir = recordDir + FILESEPARATOR + dateDir + FILESEPARATOR;
        String m4aVoiceUrl = null;
        String flacVoiceUrl = null;
        String mp4VideoUrl = null;
        if (aacUniteFile.exists()) {
            //转成m4a文件
            /*LOGGER.info("aac文件存在，开始转m4a文件...");
            StringBuilder aac2m4aCmd = new StringBuilder();
            aac2m4aCmd.append("/www/hktRecord/tools/ffmpeg -i ");
            aac2m4aCmd.append(aacPath);
            aac2m4aCmd.append(" -acodec copy ");
            String m4aFileName = courseUuid + ".m4a";
            aac2m4aCmd.append(webSaveDir);
            aac2m4aCmd.append(m4aFileName);
            exec(aac2m4aCmd.toString(), true);*/
            /**start wqz 转成mp4文件*/
            LOGGER.info("aac文件存在，开始转mp4文件...");
            StringBuilder aac2mp4Cmd = new StringBuilder();

            aac2mp4Cmd.append("/www/hktRecord/tools/ffmpeg -f concat -safe 0 -i ");
            aac2mp4Cmd.append(webSaveDir + courseUuid + ".txt");
            aac2mp4Cmd.append(" -c copy ");
            String mp4FileName = courseUuid + ".mp4";
            aac2mp4Cmd.append(webSaveDir);
            aac2mp4Cmd.append(mp4FileName);

            LOGGER.info("转mp4文件开始执行cmd命令...");
            exec(aac2mp4Cmd.toString(), true);
            /**end*/
            //File m4aSaveFile = new File(webSaveDir, m4aFileName);
            File mp4SaveFile = new File(webSaveDir, mp4FileName);
            if (mp4SaveFile.exists()) {
                String voiceBasePath = ymlMyConfig.getUploadAddress() + UPLOAD_ROOT + FILESEPARATOR + RECORD_DIR + FILESEPARATOR + dateDir + FILESEPARATOR;
                // m4aVoiceUrl = voiceBasePath + m4aFileName;
                // StringBuilder m4a2flacCmd = new StringBuilder();
              /*  m4a2flacCmd.append("/www/hktRecord/tools/ffmpeg -i ");
                m4a2flacCmd.append(mp4SaveFile.getPath());
                m4a2flacCmd.append(" -y ");*/
                //  String flacVoiceName = mp4SaveFile.getName().replace(".m4a", ".flac");
                //  String flacFilePath = webSaveDir + flacVoiceName;
               /* m4a2flacCmd.append(flacFilePath);
                exec(m4a2flacCmd.toString(), true);*/
                //    flacVoiceUrl = voiceBasePath + flacVoiceName;
                /**start wqz 取mp4 URL*/
                mp4VideoUrl = voiceBasePath + mp4FileName;
                /**end*/
            } else {
                LOGGER.info("m4aSaveFile={}不存在！", mp4SaveFile.getPath() + "couse_uui={}" + courseUuid);
                throw new Exception("m4aSaveFile={}不存在！");
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
            LOGGER.info("合并轨迹db文件");
            TrackData trackData = parseComposeDBFile(trackComposeDBFilePath, coursewareService);

            //序列化后保存到数据库中
            byte[] serialize = SerializationUtils.serialize(trackData);
            String serializeDataFileName = trackComposeDBFileName.substring(0, trackComposeDBFileName.lastIndexOf("."));
            //webSaveDir /www/clientfile/uploadPath/recordDir
            File serializeDataFile = new File(webSaveDir, serializeDataFileName);
            FileUtils.writeByteArrayToFile(serializeDataFile, serialize);
            trackComposeUrl = ymlMyConfig.getUploadAddress() + UPLOAD_ROOT + FILESEPARATOR + RECORD_DIR + FILESEPARATOR + dateDir + FILESEPARATOR + serializeDataFileName;
        } else {
            LOGGER.info("trackComposeDBFile={}不存在！", trackComposeDBFile.getPath());
        }

        ClassRoom classRoom = new ClassRoom();
        classRoom.setCourseUuid(courseUuid);
        classRoom.setVoiceUrl(m4aVoiceUrl);
        classRoom.setFlacVoiceUrl(flacVoiceUrl);
        classRoom.setVoiceDuration(totalVoiceDuration);
        classRoom.setTrackUrl(trackComposeUrl);
        /**start wqz 存入mp4 URL*/
        classRoom.setMp4VideoUrl(mp4VideoUrl);
        classRoom.setVideoDuration(totalVideoDuration);
        /**end*/
        int i = classRoomService.updateByCourseUuid(classRoom);
        if (i == 0) {
            throw new Exception("合并文件URL入库失败！");
        }
    }

/*    public static void main(String[] args) throws Exception {
        parseComposeDBFile(null, null);
    }*/

    //此方法兼容性修改，不需再New
    public static TrackData parseComposeDBFile(String dbFilePath, CoursewareService coursewareService) throws Exception {
        TrackData trackData = new TrackData();
        //String sql = "jdbc:sqlite://" + dbFilePath;
        String sql = "jdbc:sqlite://" + "D:/9月8号源文件/d4778aa0-b7c4-49e8-b8a7-c2381432206b_compose.db";
        Connection conn = null;
        ResultSet rs = null;
        int row = 0;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(sql);
            Statement stat = conn.createStatement();
            rs = stat.executeQuery("select * from board_message"); // 查询数据

            int duration = 0;
            List<String[]> drawList = new ArrayList<>();    //保存画画数据列表
            List<String> imageUrlList = new ArrayList<>();  //课件图片url列表
            List<String[]> boardWHList = new ArrayList<>(); //白板的宽高列表

            List<String> imageUrlAllList;  //课件图片url全部列表
            Map<String, List<String>> imageUrlAllMap = new HashMap<>(); //key:coursewareUuid，value：imageUrlAllList
            Map<String, String[]> imageWHMap = new HashMap<>();  //key:coursewareUuid    value:课件图片的宽高
            Map<Integer, String> boardImageUrlMap = new HashMap<>();  //key:boardNo   value:imageUrl
            String[] boardWH;   //白板的宽高
            Map<Integer, String[]> boardWHMap = new HashMap<>(); //key:boardNo   value:boardWH
            Map<String, Integer> uuidPageNumBoardMap = new HashMap<>(); //key：coursewareUuid+pageNum，value：boardNo
            String boardId;  //白板Id
            int pageNo = -1;   //课件页数编号
            int boardNo = -1;  //h5白板编号
            String whiteBoardHeight = null; //当前可画高范围
            String whiteBoardWidth = null;
            boolean recordFlag = false;
            List<Integer> timeList = new ArrayList<>();

            while (rs.next()) {
                row++;

                String data = rs.getString("data");
                if ("start-course".equals(data)) {
                    LOGGER.info("指令start-course");
                    continue;
                }

                String msg_type = rs.getString("msg_type");
                if ("MasterChanged".equals(msg_type)) {
                    LOGGER.info("指令MasterChanged");
                    duration = Integer.parseInt(data);
                    timeList.add(duration);
                    continue;
                }

                JSONObject dataObj = JSON.parseObject(data);
                String methodtype = dataObj.getString("methodtype");
                if (!recordFlag) {
                    if ("17".equals(methodtype)) {
                        // drawList=new ArrayList<>();
                        LOGGER.info("指令17");
                        recordFlag = true;
                        continue;
                    }
                }

                if ("15".equals(methodtype) || "16".equals(methodtype) || "17".equals(methodtype)
                        || "IM".equals(methodtype) || "13".equals(methodtype)
                        || "18".equals(methodtype) || "19".equals(methodtype)) {   //允许(不允许)学生书写白板、开始录制、消息、白板Id
                    continue;
                }

                //使用白板（白板大小）
                if ("12".equals(methodtype)) {
                    LOGGER.info("指令12");
                    String methodparam = dataObj.getString("methodparam");
                    JSONObject methodparamObj = JSON.parseObject(methodparam);
                    //boardHeight 白板高 boardWidth 白板宽
                    String boardHeight = methodparamObj.getString("boardHeight");
                    String boardWidth = methodparamObj.getString("boardWidth");
                    String time = methodparamObj.getString("time");
                    time = String.valueOf(Integer.parseInt(time) + getDurationTime(timeList));
                    boardId = methodparamObj.getString("boardId");
                    if (BOARD_1.equalsIgnoreCase(boardId)) {
                        boardNo = 1;
                    }
                    boardWH = new String[]{boardWidth, boardHeight};
                    //key:白板 value:白板宽高
                    boardWHMap.put(boardNo, boardWH);
                    drawList.add(wrapTrackData(time, String.valueOf(boardNo)));

                } else if ("14".equals(methodtype)) {
                    LOGGER.info("指令14");
                    //切换白板和房间轨迹所有信息(房间全部信息)
                    String methodparam = dataObj.getString("methodparam");
                    JSONObject methodparamObj = JSON.parseObject(methodparam);
                    String whiteBoardHeight_1 = methodparamObj.getString("WhiteBoardHeight");
                    String whiteBoardWidth_1 = methodparamObj.getString("WhiteBoardWidth");
                    if (StringUtils.isNotBlank(whiteBoardHeight_1) && StringUtils.isNotBlank(whiteBoardWidth_1)) {
                        //当前可画宽范围
                        whiteBoardWidth = whiteBoardWidth_1;
                        whiteBoardHeight = whiteBoardHeight_1;
                    }
                    String boardHeight = methodparamObj.getString("boardHeight");
                    String boardWidth = methodparamObj.getString("boardWidth");
                    String time = methodparamObj.getString("time");
                    time = String.valueOf(Integer.parseInt(time) + getDurationTime(timeList));
                    String coursewareUuid = methodparamObj.getString("coursewareId");
                    boardId = methodparamObj.getString("boardId");

                    //课件
                    if (BOARD_0.equalsIgnoreCase(boardId)) {
                        boardNo = 0;
                        //兼容旧版本 无whiteBoardWidth
                        //modify 课件不存在纯白板宽高
                        //当前可画范围
                        whiteBoardWidth = boardWidth;
                        whiteBoardHeight = boardHeight;
                    } else if (BOARD_1.equalsIgnoreCase(boardId)) {
                        boardNo = 1;
                        whiteBoardWidth = whiteBoardWidth_1;
                        whiteBoardHeight = whiteBoardHeight_1;
                    }

                    List<String> list = imageUrlAllMap.get(coursewareUuid);
                    //无课件
                    if (StringUtils.isBlank(coursewareUuid)) {
                        drawList.add(wrapTrackData(time, String.valueOf(boardNo)));
                    } else {
                        int pageNum = Integer.parseInt(methodparamObj.getString("pageNum"));
                        Integer boardNoM = uuidPageNumBoardMap.get(coursewareUuid + pageNum);
                        //白板与课件最多只能打开3个（打开了课件，只能新增2个白板，没打开课件，可新增3个白板）
                        if (boardNoM == null) {
                            //课件页数编号
                            pageNo++;
                            boardNo = pageNo + 2;
                            //key：课件id+课件页码，value：boardNo
                            uuidPageNumBoardMap.put(coursewareUuid + pageNum, boardNo);
                        } else {
                            boardNo = boardNoM;
                        }
                        //第一次发现课件
                        if (imageUrlAllMap.get(coursewareUuid) == null) {
                            List<CoursewareImage> coursewareImageList = coursewareService.findImageByCoursewareId(coursewareUuid);
                            imageUrlAllList = new ArrayList<>();
                            String imageWidth = null;
                            String imageHeight = null;
                            if (coursewareImageList.size() > 0) {
                                for (CoursewareImage coursewareImage : coursewareImageList) {
                                    imageUrlAllList.add(coursewareImage.getImageUrl());
                                    if (StringUtils.isBlank(imageWidth)) {
                                        imageWidth = String.valueOf(coursewareImage.getWidth());
                                    }
                                    if (StringUtils.isBlank(imageHeight)) {
                                        imageHeight = String.valueOf(coursewareImage.getHeight());
                                    }
                                }
                            }
                            String[] imageWH = new String[]{imageWidth, imageHeight};
                            imageWHMap.put(coursewareUuid, imageWH);
                            imageUrlAllMap.put(coursewareUuid, imageUrlAllList);
                        }
                        //modify 如果是否清除白板判断不需要 注释
                        drawList.add(wrapTrackData(time, "0", String.valueOf(boardNo)));

                        String imageUrl = imageUrlAllMap.get(coursewareUuid).get(pageNum);
                        boardImageUrlMap.put(boardNo, imageUrl);
                        boardWHMap.put(boardNo, imageWHMap.get(coursewareUuid));
                    }
                    if (list == null || boardWHMap.get(boardNo) == null) {
                        String drawData = methodparamObj.getString("drawData");
                        if (StringUtils.isBlank(drawData)) {
                            drawData = "[]";
                        }
                        JSONArray jsonArray = JSONArray.parseArray(drawData);
                        int size = jsonArray.size();

                        for (int i = 0; i < size; i++) {
                            Object o = jsonArray.get(i);
                            JSONObject jsonObject = JSONObject.parseObject(o.toString());
                            String color = jsonObject.getString("color");
                            String drawMode = jsonObject.getString("drawMode");
                            String lineWidth = jsonObject.getString("lineWidth");
                            String points = jsonObject.getString("points");
                            if (StringUtils.isBlank(points)) {
                                continue;
                            }
                            /**start wqz画实心矩形、实心圆*/
                            String rectangularSolid = "";
                            String roundSolid = "";
                            if (Objects.equals(drawMode, "03")) {
                                rectangularSolid = jsonObject.getString("solid");
                            }//画实心圆
                            else if (Objects.equals(drawMode, "04")) {
                                roundSolid = jsonObject.getString("solid");
                            }
                            /**end*/
                            /**原方法drawData，新调用drawDataNew*/
                            drawDataNew(points, drawMode, time, color, lineWidth, boardNo, jsonObject, drawList, true, timeList, rectangularSolid, roundSolid);
                        }
                    }
                } else if ("01".equals(methodtype)) {   //进行画板操作
                    LOGGER.info("指令01");
                    if (boardNo == -1) {
                        boardNo = 0;
                    }
                    String methodparam = dataObj.getString("methodparam");
                    JSONObject methodparamObj = JSON.parseObject(methodparam);
                    String color = methodparamObj.getString("color");
                    String time0 = methodparamObj.getString("time");
                    time0 = String.valueOf(Integer.parseInt(time0) + getDurationTime(timeList));
                    String drawMode = methodparamObj.getString("drawMode");
                    //画实心矩形
                    String rectangularSolid = "";
                    String roundSolid = "";
                    if (Objects.equals(drawMode, "03")) {
                        rectangularSolid = methodparamObj.getString("solid");
                    }//画实心圆
                    else if (Objects.equals(drawMode, "04")) {
                        roundSolid = methodparamObj.getString("solid");
                    }
                    String lineWidth = methodparamObj.getString("lineWidth");
                    String points = methodparamObj.getString("points");
                    if (StringUtils.isBlank(points)) {
                        continue;
                    }
                    drawDataNew(points, drawMode, time0, color, lineWidth, boardNo, methodparamObj, drawList, false, timeList, rectangularSolid, roundSolid);
                } else if ("08".equals(methodtype)) { //清除画板
                    LOGGER.info("指令08");
                    //清除画板，如果-1，传0
                    if (boardNo == -1) {
                        boardNo = 0;
                    }
                    String methodparam = dataObj.getString("methodparam");
                    JSONObject methodparamObj = JSON.parseObject(methodparam);
                    String time = methodparamObj.getString("time");
                    time = String.valueOf(Integer.parseInt(time) + getDurationTime(timeList));

                    drawList.add(wrapTrackData(time, "1", String.valueOf(boardNo)));

                } else if ("10".equals(methodtype)) {
                    LOGGER.info("指令10");
                    //切换课件页面
                    String methodparam = dataObj.getString("methodparam");
                    JSONObject methodparamObj = JSON.parseObject(methodparam);
                    String time = methodparamObj.getString("time");
                    time = String.valueOf(Integer.parseInt(time) + getDurationTime(timeList));
                    String coursewareUuid = methodparamObj.getString("courseId");
                    int pageNum = Integer.parseInt(methodparamObj.getString("pageNum"));
                    Integer boardNoM = uuidPageNumBoardMap.get(coursewareUuid + pageNum);
                    if (boardNoM == null) {
                        pageNo++;
                        boardNo = pageNo + 2;
                        uuidPageNumBoardMap.put(coursewareUuid + pageNum, boardNo);
                    } else {
                        boardNo = boardNoM;
                    }
                    if (imageUrlAllMap.get(coursewareUuid) == null) {
                        List<CoursewareImage> coursewareImageList = coursewareService.findImageByCoursewareId(coursewareUuid);
                        imageUrlAllList = new ArrayList<>();
                        String imageWidth = null;
                        String imageHeight = null;
                        if (coursewareImageList.size() > 0) {
                            for (CoursewareImage coursewareImage : coursewareImageList) {
                                imageUrlAllList.add(coursewareImage.getImageUrl());
                                if (StringUtils.isBlank(imageWidth)) {
                                    imageWidth = String.valueOf(coursewareImage.getWidth());
                                }
                                if (StringUtils.isBlank(imageHeight)) {
                                    imageHeight = String.valueOf(coursewareImage.getHeight());
                                }
                            }
                        }
                        String[] imageWH = new String[]{imageWidth, imageHeight};
                        imageWHMap.put(coursewareUuid, imageWH);
                        imageUrlAllMap.put(coursewareUuid, imageUrlAllList);
                    }
                    String imageUrl = imageUrlAllMap.get(coursewareUuid).get(pageNum);
                    boardImageUrlMap.put(boardNo, imageUrl);
                    boardWHMap.put(boardNo, imageWHMap.get(coursewareUuid));

                    drawList.add(wrapTrackData(time, "0", String.valueOf(boardNo)));
                }
            }
            if (!recordFlag) {
                LOGGER.error("dbFilePath=" + dbFilePath + " :没有methodtype=17 ！！！");
            }
            trackData.setDuration(duration);
            imageUrlList.addAll(boardImageUrlMap.values());

            int pages = imageUrlList.size();
            trackData.setPages(pages);
            trackData.setImageUrl(imageUrlList);

            String[] str0 = boardWHMap.get(0);
            String[] str1 = boardWHMap.get(1);
            if (str0 == null && str1 == null) {
                String[] str = new String[]{whiteBoardWidth, whiteBoardHeight};
                boardWHMap.put(0, str);
                boardWHMap.put(1, str);
            }
            if (str0 == null && str1 != null) {
                boardWHMap.put(0, str1);
            }
            if (str0 != null && str1 == null) {
                boardWHMap.put(1, str0);
            }
            boardWHList.addAll(boardWHMap.values());

            trackData.setBoardWHList(boardWHList);
            trackData.setDrawList(drawList);

            for (int i = 0; i < drawList.size(); i++) {
                String[] one = drawList.get(i);
                JSONArray json = JSONArray.parseArray(one.toString());
                System.out.println(json);
                for (int j = 0; j < one.length; j++) {
                    System.out.println(one[j]);
                }
            }
        } catch (Exception e) {
            LOGGER.info("正在解析{}第【{}】行记录", dbFilePath, row);
            LOGGER.error(e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
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
        return trackData;
    }


    private static Map<String, List<String>> getImages(String coursewareUuid, CoursewareService coursewareService) {
        Map<String, List<String>> imageUrlAllMap = new HashMap<>(); //key:coursewareUuid，value：imageUrlAllList
        List<String> imageUrlAllList = new ArrayList<>();  //课件图片url全部列表
        if (imageUrlAllMap.get(coursewareUuid) == null) {
            List<CoursewareImage> coursewareImageList = coursewareService.findImageByCoursewareId(coursewareUuid);
            imageUrlAllList = new ArrayList<>();
            if (coursewareImageList.size() > 0) {
                for (CoursewareImage coursewareImage : coursewareImageList) {
                    imageUrlAllList.add(coursewareImage.getImageUrl());
                }
            }
            imageUrlAllMap.put(coursewareUuid, imageUrlAllList);
        }
        return imageUrlAllMap;
    }

    private static List<String> getImages3(String coursewareUuid, CoursewareService coursewareService) {
        Map<String, List<String>> imageUrlAllMap = new HashMap<>(); //key:coursewareUuid，value：imageUrlAllList
        List<String> imageUrlAllList = new ArrayList<>();  //课件图片url全部列表
        if (imageUrlAllMap.get(coursewareUuid) == null) {
            List<CoursewareImage> coursewareImageList = coursewareService.findImageByCoursewareId(coursewareUuid);
            imageUrlAllList = new ArrayList<>();
            if (coursewareImageList.size() > 0) {
                for (CoursewareImage coursewareImage : coursewareImageList) {
                    imageUrlAllList.add(coursewareImage.getImageUrl());
                }
            }
        }
        return imageUrlAllList;
    }

    private static Map<String, String[]> getImages2(String coursewareUuid, CoursewareService coursewareService) {
        Map<String, List<String>> imageUrlAllMap = new HashMap<>(); //key:coursewareUuid，value：imageUrlAllList
        Map<String, String[]> imageWHMap = new HashMap<>();  //key:coursewareUuid    value:课件图片的宽高
        if (imageUrlAllMap.get(coursewareUuid) == null) {
            List<CoursewareImage> coursewareImageList = coursewareService.findImageByCoursewareId(coursewareUuid);
            String imageWidth = null;
            String imageHeight = null;
            if (coursewareImageList.size() > 0) {
                for (CoursewareImage coursewareImage : coursewareImageList) {
                    if (StringUtils.isBlank(imageWidth)) {
                        imageWidth = String.valueOf(coursewareImage.getWidth());
                    }
                    if (StringUtils.isBlank(imageHeight)) {
                        imageHeight = String.valueOf(coursewareImage.getHeight());
                    }
                }
            }
            String[] imageWH = new String[]{imageWidth, imageHeight};
            imageWHMap.put(coursewareUuid, imageWH);
        }
        return imageWHMap;
    }

    private static int getDurationTime(List<Integer> timeList) {
        int durationTime = 0;
        if (timeList == null) {
            return durationTime;
        }
        for (int time : timeList) {
            durationTime += time;
        }
        return durationTime;
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
    public static void statisticsTeacherRecordDataByThread(String courseUuid, CpCourseService cpCourseService, ClassRoomService classRoomService, ClassMateService classMateService, File recordLogsDir, MessageService messageService, ClassRecordDataService classRecordDataService, String recordDir, YmlMyConfig ymlMyConfig, CoursewareService coursewareService) {
        LOGGER.info("***** 统计老师和学生的回放数据(音频和轨迹数据)");
        CpCourse cpCourse = cpCourseService.findByUuid(courseUuid);
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
                        statisticsTeacherVoiceData(courseUuid, recordLogsDir, recordDir, ymlMyConfig, classRoomService, classRecordDataService);
                        if (cpCourse.getCourseType() != 2) {
                            //parseTrackDataAndSave(courseUuid, coursewareService, ymlMyConfig, classRoomService, classRecordDataService, recordDir);
                        }
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
                }
            }
        }
    }

    /**
     * 统计老师和学生的回放数据(音频和轨迹数据)
     * 新增此方法，变动：调用了statisticsTeacherVoiceDataNew
     * 用于录制的mp4视频，统计学生和老师的音频和轨迹数据并入库
     * 及测试环境(/www/clientfile/uploadPath/recordDir/20180703)上传mp4原文件、合并后的_av.mp4文件、合并的轨迹文件到阿里云
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
        CpCourse cpCourse = cpCourseService.findByUuid(courseUuid);
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
                        statisticsTeacherVoiceDataNew(courseUuid, recordLogsDir, recordDir, ymlMyConfig, classRoomService, classRecordDataService);
                        if (cpCourse.getCourseType() != 2) {
                            //parseTrackDataAndSave(courseUuid, coursewareService, ymlMyConfig, classRoomService, classRecordDataService, recordDir);
                        }
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
    private static void statisticsTeacherVoiceData(String courseUuid, File recordLogsDir, String recordDir, YmlMyConfig ymlMyConfig, ClassRoomService classRoomService, ClassRecordDataService classRecordDataService) throws Exception {
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
            if ("0".equals(recordRole)) {    //只统计学生和老师的音频和轨迹
                String aacDirPath = strs[2];
                String trackPath = strs[3];
                File trackFile = new File(trackPath);
                String trackSaveFilePath = null;
                if (trackFile.exists()) {
                    String trackFileName = trackFile.getName();
                    String saveFileName = trackFileName.substring(0, trackFileName.lastIndexOf(".db")) + "_server.db";
                    File trackSaveFile = new File(saveDir, saveFileName);
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
                copyFile(aacFile, aacSaveFile);
                String m4aSaveFileName = courseUuid + "_" + m4aFile.getName();
                File m4aSaveFile = new File(webSaveDir, m4aSaveFileName);
                copyFile(m4aFile, m4aSaveFile);
                //将m4a -> flac
                //String flacSaveFileName = m4aSaveFile.getName().replace(".m4a", ".flac");
                //String flacSaveFilePath = webSaveDir + FILESEPARATOR + flacSaveFileName;
                //m4a2flac(m4aSaveFile.getPath(), flacSaveFilePath);
                //获取m4a文件时长
                Integer m4aVoiceDuration = getM4aVoiceDuration(m4aSaveFile.getPath());
                //文件url
                String fileBaseUrl = ymlMyConfig.getUploadAddress() + UPLOAD_ROOT + FILESEPARATOR + RECORD_DIR + FILESEPARATOR + dateDir + FILESEPARATOR;
                String m4aFileUrl = fileBaseUrl + m4aSaveFileName;
                //String flacFileUrl = fileBaseUrl + flacSaveFileName;
                //入库
                ClassRecordData classRecordData = new ClassRecordData();
                classRecordData.setClassRecordDataUuid(UUIDUtil.randomUUID2());
                classRecordData.setClassRoomUuid(classRoom.getClassRoomUuid());
                classRecordData.setRecordRole(Integer.parseInt(recordRole));
                classRecordData.setVoiceUrl(m4aFileUrl);
                //classRecordData.setFlacVoiceUrl(flacFileUrl);
                classRecordData.setVoiceDuration(m4aVoiceDuration);
                classRecordData.setTrackPath(trackSaveFilePath);
                classRecordData.setAacVoicePath(aacSaveFile.getPath());
                classRecordDataList.add(classRecordData);
            }
        }
        if (classRecordDataList.size() > 0) {
            classRecordDataService.batchSave(classRecordDataList);
        }
    }

    /**
     * 统计学生和老师的音频和轨迹数据并入库
     * 新增用于录制的mp4视频，统计学生和老师的音频和轨迹数据并入库
     * 及测试环境(/www/clientfile/uploadPath/recordDir/20180703)上传mp4原文件、合并后的_av.mp4文件
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
        String saveDir = BASESAVEPATH + dateDir;
        List<ClassRecordData> classRecordDataList = new ArrayList<>();
        String[] contents = content.split("\\n");
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
                if (trackFile.exists()) {
                    String trackFileName = trackFile.getName();
                    String saveFileName = trackFileName.substring(0, trackFileName.lastIndexOf(".db")) + "_server.db";
                    File trackSaveFile = new File(saveDir, saveFileName);
                    copyFile(trackFile, trackSaveFile);
                    trackSaveFilePath = trackSaveFile.getPath();
                }
                /**只有当录制文件夹内有recording2-done.txt表示mp4文件写入完整*/
                ///www/hktRecord/recordDir/20180622/e2706f6c4e8048ffaa2d5e82f0a4265c10_090934
                LOGGER.info("aacDirPath===" + aacDirPath);
                File aacDirTemp = new File(aacDirPath);
                File[] listFileTemp = aacDirTemp.listFiles();
                LOGGER.info("listFileTemp===" + listFileTemp);
                for (File file : listFileTemp) {
                    String filePath = file.getPath();
                    if (filePath.endsWith("recording2-done.txt")) {
                        LOGGER.info("mp4源文件写入完成，开始启动脚本转换_av.mp4文件...");
                        aac2m4aNew(aacDirPath);
                    }
                }
                String[] aacAndM4aFilePaths = checkAvMp4IsExist(aacDirPath);

                LOGGER.info("调用aac2m4a方法后返回aacAndM4aFilePaths." + aacAndM4aFilePaths[2]);
                String mp4FilePath = aacAndM4aFilePaths[2];
                LOGGER.info("mp4FilePath:" + mp4FilePath);
                File mp4File = new File(mp4FilePath);
                LOGGER.info("mp4File:" + mp4File);
                if (!mp4File.exists()) {
                    throw new Exception("mp4File is not exist,mp4FilePath:" + mp4FilePath);
                } else {
                    mp4FilePathUnion = mp4FilePathUnion.concat("file " + mp4FilePath).concat("\r\n");
                }
                String aacFilePath = aacAndM4aFilePaths[0];
                File aacFile = new File(aacFilePath);
                if (!aacFile.exists()) {
                    throw new Exception("aacFile is not exist,aacFilePath:" + aacFilePath);
                }
                //将文件另存
                String aacSaveFileName = courseUuid + "_" + aacFile.getName();
                File aacSaveFile = new File(saveDir, aacSaveFileName);
                copyFile(aacFile, aacSaveFile);
                /**start wqz 将mp4文件另存为*/
                String mp4SaveFileName = courseUuid + "_" + mp4File.getName();
                File mp4SaveFile = new File(webSaveDir, mp4SaveFileName);
                copyFile(mp4File, mp4SaveFile);
                /**end*/
                /**start wqz 获取mp4文件时长*/
                Integer mp4VoiceDuration = getM4aVoiceDuration(mp4SaveFile.getPath());
                /**end*/
                //文件url
                String fileBaseUrl = ymlMyConfig.getUploadAddress() + UPLOAD_ROOT + FILESEPARATOR + RECORD_DIR + FILESEPARATOR + dateDir + FILESEPARATOR;
                /**start wqz 将mp4文件另存为*/
                String mp4FileUrl = fileBaseUrl + mp4SaveFileName;
                /**end*/
                //入库
                ClassRecordData classRecordData = new ClassRecordData();
                classRecordData.setClassRecordDataUuid(UUIDUtil.randomUUID2());
                classRecordData.setClassRoomUuid(classRoom.getClassRoomUuid());
                classRecordData.setRecordRole(Integer.parseInt(recordRole));
                classRecordData.setVoiceDuration(0);
                classRecordData.setTrackPath(trackSaveFilePath);
                classRecordData.setAacVoicePath(aacSaveFile.getPath());
                /**start wqz */
                classRecordData.setMp4VideoUrl(mp4FileUrl);
                classRecordData.setVideoDuration(mp4VoiceDuration);
                /**end*/
                classRecordDataList.add(classRecordData);
            }
        }
        int count = classRecordDataService.batchSave(classRecordDataList);
        if (count > 0) {
            LOGGER.info("批量写入classRecordData表完成.");
        } else {
            LOGGER.info("批量写入classRecordData表失败.");
        }
        /**start wqz 新建一个mp4Video.txt存mp4文件生成的路径*/
        creatTxtFile(webSaveDir, courseUuid);
        writeTxtFile(mp4FilePathUnion, webSaveDir.concat("/").concat(courseUuid).concat(".txt"));
    }

    /**
     * 检查生成的_av.mp4文件是否存在
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
        int count = 0;
        while (flag) {
            count++;
            if (count <= 3) {
                aacAndM4aFilePaths = new String[3];
                File aacDir = new File(aacDirPath);
                File[] listFileseee = aacDir.listFiles();
                for (File file : listFileseee) {
                    String filePath = file.getPath();
                    if (filePath.endsWith(".aac")) {
                        aacAndM4aFilePaths[0] = filePath;
                        accFlag = true;
                    }
                    if (filePath.endsWith("_av.mp4")) {
                        aacAndM4aFilePaths[2] = filePath;
                        mp4Flag = true;
                    }
                    if (accFlag && mp4Flag) {
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
     * 将aac文件转为m4a或mp4文件
     *
     * @param aacDirPath aac文件所在目录
     * @return String[] 0:aac文件路径 1:m4a文件路径
     * @throws Exception
     */
    private static void aac2m4aNew(String aacDirPath) throws Exception {
        File aacDir = new File(aacDirPath);
        if (!aacDir.exists()) {
            throw new Exception("aacDir is not exist,aacDirPath:" + aacDirPath);
        }
        String aac2m4aCmd = "/usr/bin/python /www/hktRecord/tools/video_convert.py " + aacDirPath;
        LOGGER.info("执行脚本生成_av.mp4文件");
        exec(aac2m4aCmd, true);
    }

    /**
     * 将m4a文件转为flac文件
     *
     * @param m4aFilePath  m4a文件路径
     * @param flacFilePath flac文件路径
     * @throws Exception
     */
    private static void m4a2flac(String m4aFilePath, String flacFilePath) throws Exception {
        File m4aFile = new File(m4aFilePath);
        if (!m4aFile.exists()) {
            throw new Exception("m4aFile is not exist,m4aFilePath:" + m4aFilePath);
        }
        String m4a2flacCmd = "/www/hktRecord/tools/ffmpeg -i " + m4aFilePath + " -y " + flacFilePath;
        exec(m4a2flacCmd, true);
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
    public static void statisticsSCRecordDataByThread(String courseUuid, ClassRoomService classRoomService, ClassMateService classMateService, File recordLogsDir, MessageService messageService, ClassRecordDataService classRecordDataService, String recordDir, YmlMyConfig ymlMyConfig) {
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
                        statisticsSCVoiceData(courseUuid, recordLogsDir, recordDir, ymlMyConfig, classRoomService, classRecordDataService);
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
    private static void statisticsSCVoiceData(String courseUuid, File recordLogsDir, String recordDir, YmlMyConfig ymlMyConfig, ClassRoomService classRoomService, ClassRecordDataService classRecordDataService) throws Exception {
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
                //将m4a -> flac
                String flacSaveFileName = m4aSaveFile.getName().replace(".m4a", ".flac");
                String flacSaveFilePath = webSaveDir + FILESEPARATOR + flacSaveFileName;
                m4a2flac(m4aSaveFile.getPath(), flacSaveFilePath);
                //获取m4a文件时长
                Integer m4aVoiceDuration = 0;
                if (StringUtils.isNotEmpty(m4aSaveFile.getPath())) {
                    m4aVoiceDuration = getM4aVoiceDuration(m4aSaveFile.getPath());
                }
                //文件url
                String fileBaseUrl = ymlMyConfig.getUploadAddress() + UPLOAD_ROOT + FILESEPARATOR + RECORD_DIR + FILESEPARATOR + dateDir + FILESEPARATOR;
                String m4aFileUrl = fileBaseUrl + m4aSaveFileName;
                String flacFileUrl = fileBaseUrl + flacSaveFileName;
                //入库
                ClassRecordData classRecordData = new ClassRecordData();
                classRecordData.setClassRecordDataUuid(UUIDUtil.randomUUID2());
                classRecordData.setClassRoomUuid(classRoom.getClassRoomUuid());
                classRecordData.setRecordRole(Integer.parseInt(recordRole));
                classRecordData.setVoiceUrl(m4aFileUrl);
                classRecordData.setFlacVoiceUrl(flacFileUrl);
                classRecordData.setVoiceDuration(m4aVoiceDuration);
                classRecordData.setAacVoicePath(aacSaveFile.getPath());
                classRecordDataList.add(classRecordData);
                LOGGER.info("存classRecordData入参:" + UUIDUtil.randomUUID2() + "," + classRoom.getClassRoomUuid() + "," + Integer.parseInt(recordRole) + "," + m4aFileUrl + "," + flacFileUrl + "," + m4aVoiceDuration + "," + aacSaveFile.getPath());
            }
        }

        if (classRecordDataList.size() > 0) {
            classRecordDataService.batchSave(classRecordDataList);
        }
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
            if (!"0".equals(recordRole)) {    //统计学生和cc或cr的音频
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
                //将m4a -> flac
                String flacSaveFileName = m4aSaveFile.getName().replace(".m4a", ".flac");
                String flacSaveFilePath = webSaveDir + FILESEPARATOR + flacSaveFileName;
                m4a2flac(m4aSaveFile.getPath(), flacSaveFilePath);
                //获取m4a文件时长
                //Integer m4aVoiceDuration = getM4aVoiceDuration(m4aSaveFile.getPath());
                //文件url
                String fileBaseUrl = ymlMyConfig.getUploadAddress() + UPLOAD_ROOT + FILESEPARATOR + RECORD_DIR + FILESEPARATOR + dateDir + FILESEPARATOR;
                String m4aFileUrl = fileBaseUrl + m4aSaveFileName;
                String flacFileUrl = fileBaseUrl + flacSaveFileName;
                //入库
                ClassRecordData classRecordData = new ClassRecordData();
                classRecordData.setClassRecordDataUuid(UUIDUtil.randomUUID2());
                classRecordData.setClassRoomUuid(classRoom.getClassRoomUuid());
                classRecordData.setRecordRole(Integer.parseInt(recordRole));
                classRecordData.setVoiceUrl(m4aFileUrl);
                classRecordData.setFlacVoiceUrl(flacFileUrl);
                // classRecordData.setVoiceDuration(m4aVoiceDuration);
                classRecordData.setAacVoicePath(aacSaveFile.getPath());
                classRecordDataList.add(classRecordData);
            }
        }
        classRecordDataService.batchSave(classRecordDataList);
    }

    /**
     * 创建文件
     *
     * @throws IOException
     * @author wqz
     */
    public static boolean creatTxtFile(String webSaveDir, String txtName) throws IOException {
        boolean flag = false;
        webSaveDir = webSaveDir.concat("/");
        LOGGER.info("creatTxtFile 的webSaveDir:" + webSaveDir);
        txtName = txtName.concat(".txt");
        // path = path + ".txt";
        //File filename = new File(path);
        File mp4SaveFile = new File(webSaveDir, txtName);
        LOGGER.info("creatTxtFile 的_av.mp4 SaveFile:" + mp4SaveFile);
        if (!mp4SaveFile.exists()) {
            mp4SaveFile.createNewFile();
            flag = true;
        }
        return flag;
    }

    /**
     * 写文件
     *
     * @param newStr 新内容
     * @throws IOException
     * @author wqz
     */
    public static boolean writeTxtFile(String newStr, String webSaveDir) throws IOException {
        LOGGER.info("writeTxtFile");
        // 先读取原有文件内容，然后进行写入操作
        boolean flag = false;
        String filein = newStr + "\r\n";

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            // 文件路径
            File file = new File(webSaveDir);
            // 将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buf = new StringBuffer();
            // 保存该文件原有的内容
       /*     for (int j = 1; (temp = br.readLine()) != null; j++) {
                buf = buf.append(temp);
                // System.getProperty("line.separator")
                // 行与行之间的分隔符 相当于“\n”
                buf = buf.append(System.getProperty("line.separator"));
            }*/
            buf.append(filein);

            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buf.toString().toCharArray());
            pw.flush();
            flag = true;
        } catch (IOException e1) {
            // TODO 自动生成 catch 块
            throw e1;
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
        return flag;
    }
}

