package cn.onlyhi.file.util;

import cn.onlyhi.client.dto.ClassRecordDBData;
import cn.onlyhi.client.dto.TrackData;
import cn.onlyhi.client.po.ClassMate;
import cn.onlyhi.client.po.ClassRecord;
import cn.onlyhi.client.po.ClassRecordData;
import cn.onlyhi.client.po.ClassRoom;
import cn.onlyhi.client.po.CoursewareImage;
import cn.onlyhi.client.po.CpCourse;
import cn.onlyhi.client.service.ClassMateService;
import cn.onlyhi.client.service.ClassRecordDataService;
import cn.onlyhi.client.service.ClassRecordService;
import cn.onlyhi.client.service.ClassRoomService;
import cn.onlyhi.client.service.CoursewareService;
import cn.onlyhi.client.service.CpCourseService;
import cn.onlyhi.client.service.MessageService;
import cn.onlyhi.common.enums.ClientEnum;
import cn.onlyhi.common.enums.CodeEnum;
import cn.onlyhi.common.util.*;
import cn.onlyhi.file.config.YmlMyConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static cn.onlyhi.common.constants.Constants.*;
import static cn.onlyhi.common.enums.ClientEnum.UserType.STUDENT;
import static cn.onlyhi.common.enums.ClientEnum.UserType.TEACHER;
import static cn.onlyhi.common.util.ClientUtil.getCircleInfo;
import static cn.onlyhi.common.util.ClientUtil.getRectWH;
import static cn.onlyhi.common.util.ClientUtil.str2ms;
import static cn.onlyhi.common.util.CmdExecuteUtil.exec;
import static cn.onlyhi.common.util.FileUtils.readFileToString;
import static cn.onlyhi.common.util.TrackUtil.wrapTrackData;
import static cn.onlyhi.common.util.TrackUtil.wrapTrackDataNew2;
import static org.apache.commons.io.FileUtils.copyFile;

/**
 * 课程录制、课时计算
 *
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/11/21.
 */
@Component
public class CourseUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseUtil.class);
    //切到测试 http://apiclienttest.haiketang.net/php/
    public static  String NO_ACCESS_URL="http://172.19.204.201/php/";
/*
    public static String getNO_ACCESS_URL() {
        return NO_ACCESS_URL;
    }

    @Value("${phpStaging.url}")
    public void setNO_ACCESS_URL(String NO_ACCESS_URL) {
        CourseUtil.NO_ACCESS_URL = NO_ACCESS_URL;
    }
*/
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
    public static void parseAndStatisticsClassRecordDBDataByThread(String courseUuid, File recordLogsDir, ClassMateService classMateService, ClassRecordService classRecordService, ClassRoomService classRoomService, CpCourseService cpCourseService, MessageService messageService) {
        new Thread(() -> {
        try {
            boolean verifyFlag = verifyRecordEnd(courseUuid, recordLogsDir);
            //if (verifyFlag) {
            //if (verifyFlag) {
                parseAndStatisticsClassRecordDBData(courseUuid, recordLogsDir, classMateService, classRecordService, classRoomService, cpCourseService);
            //} else {
                //没有录制结束标识（程序正常退出有结束标识），但是进出记录还是有的，可能会包括有共同时长的或没有共同时长的
                //LOGGER.error("没有录制结束标识！");
           // }
        } catch (Exception e) {
            LOGGER.info("*****courseUuid=" + courseUuid + "解析客户端进出房间记录，统计课时线程异常！*****", e);
            try {
                messageService.sendExceptionMessage(SENDPHONE, courseUuid, e.getMessage());
            } catch (Exception e1) {
                LOGGER.error(e.toString(), e);
            }
            LOGGER.error(e.toString(), e);
        }
        }).start();
    }
    public static void parseAndStatisticsClassRecordDBDataByThreadPhp(String courseUuid, File recordLogsDir, ClassMateService classMateService, ClassRecordService classRecordService, ClassRoomService classRoomService, CpCourseService cpCourseService, MessageService messageService) {
        new Thread(() -> {
            try {
                verifyRecordEnd(courseUuid, recordLogsDir);
                parseAndStatisticsClassRecordDBDataPhp(courseUuid, recordLogsDir, classMateService, classRecordService, classRoomService, cpCourseService);
            } catch (Exception e) {
                    LOGGER.info("*****courseUuid=" + courseUuid + "解析客户端进出房间记录，统计课时线程异常！*****", e);
                try {
                    messageService.sendExceptionMessage(SENDPHONE, courseUuid, e.getMessage());
                } catch (Exception e1) {
                    LOGGER.error(e.toString(), e);
                }
                LOGGER.error(e.toString(), e);
            }
        }).start();
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
            }
        }
        return false;
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
    private static void parseAndStatisticsClassRecordDBData(String courseUuid, File recordLogsDir, ClassMateService classMateService, ClassRecordService classRecordService, ClassRoomService classRoomService, CpCourseService cpCourseService) throws Exception {
        //解析记录数据
        List<ClassRecordDBData> dataList = parseClassRecordDBFile(courseUuid, recordLogsDir);
        if (dataList == null || dataList.size() == 0) {
            throw new Exception("courseUuid=" + courseUuid + " :客户端进出房间记录数据不存在！");
        }
        //入库
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
        LOGGER.info("开始统计课时.courseUuid:" + courseUuid);
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
    private static void parseAndStatisticsClassRecordDBDataPhp(String courseUuid, File recordLogsDir, ClassMateService classMateService, ClassRecordService classRecordService, ClassRoomService classRoomService, CpCourseService cpCourseService) throws Exception {
        ClassRoom classRoom = null;
        try {
            List<ClassRecordDBData> dataList = parseClassRecordDBFile(courseUuid, recordLogsDir);
            if (dataList == null || dataList.size() == 0) {
                throw new Exception("courseUuid=" + courseUuid + " :客户端进出房间记录数据不存在！");
            }
            int i = saveClassRecordDBData(dataList, classMateService, classRecordService);
            if (i == 0) {
                throw new Exception("courseUuid=" + courseUuid + " :saveClassRecordDBData失败！");
            }
            classRoom = classRoomService.findByCourseUuid(courseUuid);
            if (classRoom == null) {
                throw new Exception("courseUuid=" + courseUuid + " :classRoom不存在！");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        int j;
        if(courseUuid!=null && classRoom!=null){

            StatisticsClassTimeResult statisticsClassTimeResult = statisticsClassTimeResult(courseUuid, classRecordService, classRoom);

            if(statisticsClassTimeResult!=null){
                j = classUpdate(courseUuid, statisticsClassTimeResult);
                if (j == 0) {
                    throw new Exception("courseUuid=" + courseUuid + " :cpCourse更新时长失败！");
                }
                roomUpdate(courseUuid,classRoomService);
            }
        }

    }
    private static StatisticsClassTimeResult statisticsClassTimeResult(String courseUuid,ClassRecordService classRecordService,ClassRoom classRoom){
        //统计课耗是直接统计到php库，还是统计到旧库后，php通过db拉取?
        StatisticsClassTimeResult statisticsClassTimeResult=null;
        try{
            String phpUrl =NO_ACCESS_URL.concat("client/course/detail");
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post=new HttpPost(phpUrl);
            post.setHeader("Content-Type","application/json;charset=utf-8");

            JSONObject param= new JSONObject();
            param.put("courseUuid", courseUuid);

            StringEntity stringEntity = new StringEntity(param.toString());
            post.setEntity(stringEntity);
            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity=response.getEntity();


            String result= EntityUtils.toString(entity,"UTF-8");
            LOGGER.info("statisticsClassTimeResult 的client/course/detail 传给php课程id={}",courseUuid);
            LOGGER.info("client/course/detail result ={}",result);
            Response response3=JSON.parseObject(result,Response.class);
            Object object=response3.getData();

            if(object!=null){
                CpCourse cpCourse=JSON.parseObject(object.toString(), CpCourse.class);

                String courseDate = cpCourse.getCourseDate();
                String startTime = cpCourse.getStartTime();
                String endTime = cpCourse.getEndTime();

                if(classRoom!=null){

                    List<ClassRecord> classRecordList = classRecordService.findByClassRoomUuid(classRoom.getClassRoomUuid());

                    LOGGER.info("开始统计课时.courseUuid={}",courseUuid);
                    if(classRecordList.size()>0){
                        statisticsClassTimeResult = statisticsClassTime(courseDate, startTime, endTime, classRecordList);
                    }
                }
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return statisticsClassTimeResult;
    }

    private static int classUpdate(String courseUuid,StatisticsClassTimeResult statisticsClassTimeResult){
        try{
            //PHP更新时长，考虑保留也更新到旧表用于提供教学每天课耗导出的excle?
            String phpUrl2 =NO_ACCESS_URL.concat("client/class/update");
            CloseableHttpClient client2 = HttpClients.createDefault();
            HttpPost post2=new HttpPost(phpUrl2);
            post2.setHeader("Content-Type","application/json;charset=utf-8");
            post2.setHeader("Accept", "application/json");

            JSONObject param2= new JSONObject();
            param2.put("courseUuid", courseUuid);
            param2.put("realLength",statisticsClassTimeResult.getComTotalTime());
            param2.put("leadsLength",statisticsClassTimeResult.getStudentTotalTime());
            param2.put("teacherLength",statisticsClassTimeResult.getTeacherTotalTime());

            LOGGER.info("client/class/update 传给php={}",JSON.toJSONString(param2));
            StringEntity stringEntity1 = new StringEntity(param2.toString());
            post2.setEntity(stringEntity1);

            CloseableHttpResponse response2 = client2.execute(post2);
            HttpEntity entity2=response2.getEntity();
            String result2= EntityUtils.toString(entity2,"UTF-8");
            LOGGER.info("client/class/update result ={}",result2);

            Response phpResponse2 = JSON.parseObject(result2, Response.class);
            if(Objects.equals(CodeEnum.SUCCESS.getCode(),phpResponse2.getCode())){
                LOGGER.info("PHP 更新时长完成.");
                return 1;
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return 0;
    }

    private static void roomUpdate(String courseUuid, ClassRoomService classRoomService){
        LOGGER.info("roomUpdate");
        //调用php
        Response phpResponse=null;
        try {
            //保持原更新
            ClassRoom classRoom1 = new ClassRoom();
            classRoom1.setCourseUuid(courseUuid);
            classRoom1.setStatisticsStatus(2);
            classRoomService.updateByCourseUuid(classRoom1);

            String phpUrl3 =NO_ACCESS_URL.concat("client/room/update");
            CloseableHttpClient client3 = HttpClients.createDefault();
            HttpPost post3=new HttpPost(phpUrl3);
            post3.setHeader("Content-Type","application/json;charset=utf-8");
            post3.setHeader("Accept", "application/json");

            JSONObject param3= new JSONObject();
            param3.put("courseUuid", courseUuid);
            param3.put("statisticsStatus", 2);
            StringEntity stringEntity = new StringEntity(param3.toString());
            post3.setEntity(stringEntity);

            CloseableHttpResponse response = client3.execute(post3);
            HttpEntity entity3=response.getEntity();
            String result3= EntityUtils.toString(entity3,"UTF-8");
            LOGGER.info("client/room/update={}",courseUuid);
            LOGGER.info("client/room/update result ={}",result3);
            phpResponse = JSON.parseObject(result3, Response.class);
            if(!Objects.equals(CodeEnum.SUCCESS.getCode(),phpResponse.getCode())){
                LOGGER.error("PHP 更新房间统计状态失败！");
            }else {
                LOGGER.info("*****PHP 更新房间统计状态完成,courseUuid={}*****", courseUuid);
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
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
        LOGGER.info("statisticsClassTime.");
        long courseStartTime=0;
        long courseEndTime=0;
        if(courseDate!=null && startTime!=null){
            courseStartTime = DateUtils.parseDate(courseDate + startTime, "yyyy-MM-ddHH:mm").getTime();
            courseEndTime = DateUtils.parseDate(courseDate + endTime, "yyyy-MM-ddHH:mm").getTime();
        }

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
        LOGGER.info("获取学生和老师公共的在线时长.");
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
            //throw new Exception("进出时间不匹配，或无录制文件！");
            LOGGER.error("进出时间不匹配，或无录制文件！");
        }
        //对进出时间过滤
        ListIterator<Long> enterTimeIterator = enterTimeList.listIterator();
        ListIterator<Long> outTimeIterator = outTimeList.listIterator();
        while (enterTimeIterator.hasNext() && outTimeIterator.hasNext()) {
            long enterTime = enterTimeIterator.next();
            long outTime = outTimeIterator.next();
            if (enterTime > courseEndTime || outTime < courseStartTime) {  //进入时间大于课程结束时间，不做统计,或者退出时间小于课程开始时间，不做统计
                enterTimeIterator.remove();
                outTimeIterator.remove();
            }
        }
        //LOGGER.info("过滤后：enterTimeList={},outTimeList={}", enterTimeList, outTimeList);
        Map<Long, Long> timeMap = new HashMap<>();
        size = enterTimeList.size();
        Long outTime;
        for (int i = 0; i < size; i++) {
            Long enterTime = enterTimeList.get(i);
            if (outTimeList.size() > 0 && outTimeList.size() <= enterTimeList.size()) {
                //例，进入时间长度=2，退出时间长度=1时，i的值[0,1]；
                if (i < outTimeList.size()) {
                    outTime = outTimeList.get(i);
                } else {
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
            //_user_course.db从/www/hktRecord/mylogs/20190313 复制到/www/recordDir/20190313
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
            //CourseUtilRefactoring courseUtilRefactoring=new CourseUtilRefactoring();
            //TrackData trackData = courseUtilRefactoring.parseComposeDBFile(trackComposeDBFilePath, coursewareService);
            TrackData trackData =parseComposeDBFile(trackComposeDBFilePath, coursewareService);
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
    public static void uniteRecordPhp(List<ClassRecordData> classRecordDataList, String courseUuid, String recordDir, YmlMyConfig ymlMyConfig, ClassRoomService classRoomService, CoursewareService coursewareService) throws Exception {
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
        String[] uniteCmds = new String[]{"/www/hktRecord/tools/ffmpeg", "-i", uniteCmd.toString(), "-y", "-acodec", "copy", aacPath};
        exec(uniteCmds, true);
        File aacUniteFile = new File(aacPath);
        String dateDir = aacParentFile.getPath().substring(aacParentFile.getParentFile().getPath().length() + 1);
        String webSaveDir = recordDir + FILESEPARATOR + dateDir + FILESEPARATOR;
        String m4aVoiceUrl = null;
        if (aacUniteFile.exists()) {
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
            LOGGER.info("合并轨迹db文件");
            TrackData trackData =parseComposeDBFile(trackComposeDBFilePath, coursewareService);
            byte[] serialize = SerializationUtils.serialize(trackData);
            String serializeDataFileName = trackComposeDBFileName.substring(0, trackComposeDBFileName.lastIndexOf("."));
            File serializeDataFile = new File(webSaveDir, serializeDataFileName);
            FileUtils.writeByteArrayToFile(serializeDataFile, serialize);
            trackComposeUrl = ymlMyConfig.getUploadAddress() + UPLOAD_ROOT + FILESEPARATOR + RECORD_DIR + FILESEPARATOR + dateDir + FILESEPARATOR + serializeDataFileName;
        } else {
            LOGGER.info("trackComposeDBFile={}不存在！", trackComposeDBFile.getPath());
        }

        try {
            //保留原更新用于恢复是否有回放资源校验
            ClassRoom classRoom = new ClassRoom();
            classRoom.setCourseUuid(courseUuid);
            classRoom.setVoiceUrl(m4aVoiceUrl);
            classRoom.setVoiceDuration(totalVoiceDuration);
            classRoom.setTrackUrl(trackComposeUrl);
            int i = classRoomService.updateByCourseUuid(classRoom);
            if (i == 0) {
                throw new Exception("合并文件URL入库失败！");
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        //调用php
        roomUpdate(courseUuid,m4aVoiceUrl,totalVoiceDuration,trackComposeUrl,null);

    }
    public static void roomUpdate(String courseUuid,String m4aVoiceUrl,int totalVoiceDuration,String trackComposeUrl,Integer statisticsStatus){
        Response phpResponse=null;
        try{
            String phpUrl =NO_ACCESS_URL.concat("client/room/update");
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post=new HttpPost(phpUrl);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            post.setHeader("Accept", "application/json");

            JSONObject param= new JSONObject();
            if(courseUuid!=null){
                param.put("courseUuid", courseUuid);
            }
            if(m4aVoiceUrl!=null){
                param.put("voiceUrl", m4aVoiceUrl);
            }
            if(totalVoiceDuration>0){
                param.put("voiceDuration", totalVoiceDuration);
            }
            if(trackComposeUrl!=null){
                param.put("trackUrl", trackComposeUrl);
            }
            if(statisticsStatus!=null){
                param.put("statisticsStatus", statisticsStatus);
            }

            StringEntity stringEntity = new StringEntity(param.toString());
            post.setEntity(stringEntity);

            LOGGER.info("client/room/update 传给php ={}",JSON.toJSONString(param));

            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity=response.getEntity();
            String result= EntityUtils.toString(entity,"UTF-8");

            LOGGER.info("client/room/update result ={}",result);

            phpResponse = JSON.parseObject(result, Response.class);
            if(!Objects.equals(CodeEnum.SUCCESS.getCode(),phpResponse.getCode())){
                throw new Exception("PHP合并文件URL入库失败！");
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
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
            //?
            CourseUtilRefactoring courseUtilRefactoring=new CourseUtilRefactoring();
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

    /**
     * 此方法都是兼容性修改，不需再New
     * 解析轨迹文件
     *
     * @param dbFilePath
     * @param coursewareService
     * @return TrackData
     * @throws Exception
     * @modify 讲师中心上传课件转换图片后访问的地址需要生成oss格式
     * @modify 14指令mac端的轨迹返回压缩后的长宽
     */
    public static TrackData parseComposeDBFile(String dbFilePath, CoursewareService coursewareService) throws Exception {
        TrackData trackData = new TrackData();
        String sql = "jdbc:sqlite://" + dbFilePath;
        Connection conn = null;
        ResultSet rs = null;
        Statement stat = null;
        int row = 0;
        try {
            //讲师中心上传课件转换图片后访问的地址需要生成oss格式
            OSSClient ossClient = OssUtils.generateOssClient();
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(sql);
            stat = conn.createStatement();
            rs = stat.executeQuery("select * from board_message"); // 查询数据
            int duration = 0;
            List<String[]> drawList = new ArrayList<>();    //保存画画数据列表
            List<String> imageUrlList = new ArrayList<>();  //课件图片url列表
            List<String[]> boardWHList = new ArrayList<>(); //白板的宽高列表
            List<String> imageUrlAllList = new ArrayList<>();  //课件图片url全部列表
            Map<String, List<String>> imageUrlAllMap = new HashMap<>(); //key:coursewareUuid，value：imageUrlAllList
            Map<String, String[]> imageWHMap = new HashMap<>();  //key:coursewareUuid    value:课件图片的宽高
            Map<Integer, String> boardImageUrlMap = new HashMap<>();  //key:boardNo   value:imageUrl
            String[] boardWH = new String[2];   //白板的宽高
            Map<Integer, String[]> boardWHMap = new HashMap<>(); //key:boardNo   value:boardWH
            Map<String, Integer> uuidPageNumBoardMap = new HashMap<>(); //key：coursewareUuid+pageNum，value：boardNo
            String boardId = null;  //白板Id
            int pageNo = -1;   //课件页数编号
            int boardNo = -1;  //h5白板编号
            String whiteBoardHeight = null; //纯白板高
            String whiteBoardWidth = null;  //纯白板宽
            boolean recordFlag = false;
            List<Integer> timeList = new ArrayList<>();
            while (rs.next()) {
                row++;
                String data = rs.getString("data");
                if ("start-course".equals(data)) {
                    continue;
                }
                String msg_type = rs.getString("msg_type");
                if ("MasterChanged".equals(msg_type)) {
                    duration = Integer.parseInt(data);
                    timeList.add(duration);
                    continue;
                }
                JSONObject dataObj = JSON.parseObject(data);
                String methodtype = dataObj.getString("methodtype");
                if (!recordFlag) {
                    if ("17".equals(methodtype)) {
                        recordFlag = true;
                    }
                    continue;
                }
                if ("15".equals(methodtype) || "16".equals(methodtype) || "17".equals(methodtype)
                        || "IM".equals(methodtype) || "13".equals(methodtype)
                        || "18".equals(methodtype) || "19".equals(methodtype)) {   //允许(不允许)学生书写白板、开始录制、消息、白板Id
                    continue;
                }
                if ("12".equals(methodtype)) {
                    //LOGGER.info("使用白板（白板大小）");
                    String methodparam = dataObj.getString("methodparam");
                    JSONObject methodparamObj = JSON.parseObject(methodparam);
                    String boardHeight = methodparamObj.getString("boardHeight");
                    String boardWidth = methodparamObj.getString("boardWidth");
                    String time = methodparamObj.getString("time");
                    time = String.valueOf(Integer.parseInt(time) + getDurationTime(timeList));
                    boardId = methodparamObj.getString("boardId");
                    if (BOARD_0.equalsIgnoreCase(boardId)) {
                        boardNo = 0;
                        if (StringUtils.isBlank(whiteBoardWidth)) {
                            whiteBoardWidth = boardWidth;
                        }
                        if (StringUtils.isBlank(whiteBoardHeight)) {
                            whiteBoardHeight = boardHeight;
                        }
                    } else if (BOARD_1.equalsIgnoreCase(boardId)) {
                        boardNo = 1;
                        whiteBoardWidth = boardWidth;
                        whiteBoardHeight = boardHeight;
                    }
                    boardWH = new String[]{boardWidth, boardHeight};
                    boardWHMap.put(boardNo, boardWH);
                    drawList.add(wrapTrackData(time, String.valueOf(boardNo)));
                } else if ("14".equals(methodtype)) {
                    //LOGGER.info("切换白板和房间轨迹所有信息(房间全部信息)");
                    String methodparam = dataObj.getString("methodparam");
                    JSONObject methodparamObj = JSON.parseObject(methodparam);
                    String whiteBoardHeight_1 = methodparamObj.getString("WhiteBoardHeight");
                    String whiteBoardWidth_1 = methodparamObj.getString("WhiteBoardWidth");
                    if (StringUtils.isNotBlank(whiteBoardHeight_1) && StringUtils.isNotBlank(whiteBoardWidth_1)) {
                        whiteBoardWidth = whiteBoardWidth_1;
                        whiteBoardHeight = whiteBoardHeight_1;
                    }
                    String boardHeight = methodparamObj.getString("boardHeight");
                    String boardWidth = methodparamObj.getString("boardWidth");
                    String time = methodparamObj.getString("time");
                    time = String.valueOf(Integer.parseInt(time) + getDurationTime(timeList));
                    String coursewareUuid = methodparamObj.getString("coursewareId");
                    boardId = methodparamObj.getString("boardId");
                    if (BOARD_0.equalsIgnoreCase(boardId)) {
                        boardNo = 0;
                        if (StringUtils.isBlank(whiteBoardWidth)) {
                            whiteBoardWidth = boardWidth;
                        }
                        if (StringUtils.isBlank(whiteBoardHeight)) {
                            whiteBoardHeight = boardHeight;
                        }
                    } else if (BOARD_1.equalsIgnoreCase(boardId)) {
                        boardNo = 1;
                        whiteBoardWidth = boardWidth;
                        whiteBoardHeight = boardHeight;
                    }
                    List<String> list = imageUrlAllMap.get(coursewareUuid);
                    if (StringUtils.isBlank(coursewareUuid)) {
                        drawList.add(wrapTrackData(time, String.valueOf(boardNo)));
                    } else {
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
                            LOGGER.info("第一次发现课件");
                            List<CoursewareImage> coursewareImageList = coursewareService.findImageByCoursewareId(coursewareUuid);
                            imageUrlAllList = new ArrayList<>();
                            for (CoursewareImage coursewareImage : coursewareImageList) {
                                if (!Objects.equals(coursewareImage.getImageUrl(), null) && !coursewareImage.getImageUrl().contains("http://")) {
                                    imageUrlAllList.add(OssTeacherUtils.generateFileUrl(ossClient, coursewareImage.getImageUrl()));
                                    String ossUrl=OssTeacherUtils.generateFileUrl(ossClient, coursewareImage.getImageUrl());
                                    //LOGGER.info("ossUrl"+ossUrl);
                                } else {
                                    imageUrlAllList.add(coursewareImage.getImageUrl());
                                }
                            }

                            String[] imageWH = new String[]{boardWidth, boardHeight};
                            imageWHMap.put(coursewareUuid, imageWH);
                            imageUrlAllMap.put(coursewareUuid, imageUrlAllList);

                            //add
                            String imageUrl = imageUrlAllList.get(0);
                            pageNo++;
                            boardNo = pageNo + 2;
                            boardImageUrlMap.put(boardNo, imageUrl);
                            boardWHMap.put(boardNo, imageWH);
                            uuidPageNumBoardMap.put(coursewareUuid + pageNum, boardNo);
                            //add
                        }
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
                    //清除画板，如果-1，传0
                    if (boardNo == -1) {
                        boardNo = 0;
                    }
                    String methodparam = dataObj.getString("methodparam");
                    JSONObject methodparamObj = JSON.parseObject(methodparam);
                    String time = methodparamObj.getString("time");
                    time = String.valueOf(Integer.parseInt(time) + getDurationTime(timeList));
                    drawList.add(wrapTrackData(time, "1", String.valueOf(boardNo)));
                } else if ("09".equals(methodtype)) { //打开课件
                    //String scaling = dataObj.getString("scaling");
                    //JSONObject scalingObj = JSON.parseObject(scaling);
                    //String time = scalingObj.getString("time");
                    //time = String.valueOf(Integer.parseInt(time) + getDurationTime(timeList));
                    //int pageNum = 0;
                    //String coursewareUuid = dataObj.getString("methodparam");  //课件uuid
                    //if (imageUrlAllMap.get(coursewareUuid) == null) {   //首次打开课件
                    //List<CoursewareImage> coursewareImageList = coursewareService.findImageByCoursewareId(coursewareUuid);
                    //imageUrlAllList = new ArrayList<>();
                       /* String imageWidth = null;
                        String imageHeight = null;*/
                  /*      if (coursewareImageList.size() > 0) {
                            for (CoursewareImage coursewareImage : coursewareImageList) {
                                if (!Objects.equals(coursewareImage.getImageUrl(), null) && !coursewareImage.getImageUrl().contains("http://")) {
                                    imageUrlAllList.add(OssTeacherUtils.generateFileUrl(ossClient, coursewareImage.getImageUrl()));
                                } else {
                                    imageUrlAllList.add(coursewareImage.getImageUrl());
                                }
                          *//*      if (StringUtils.isBlank(imageWidth)) {
                                    imageWidth = String.valueOf(coursewareImage.getWidth());
                                }
                                if (StringUtils.isBlank(imageHeight)) {
                                    imageHeight = String.valueOf(coursewareImage.getHeight());
                                }*//*
                            }
                        }*/

                    //String[] imageWH = new String[]{imageWidth, imageHeight};
                    //String[] imageWH = new String[]{whiteBoardWidth, whiteBoardHeight};
                   /*     imageWHMap.put(coursewareUuid, imageWH);
                        imageUrlAllMap.put(coursewareUuid, imageUrlAllList);
                        String imageUrl = imageUrlAllList.get(0);
                        //pageNo++;
                        //boardNo = pageNo + 2;
                        boardImageUrlMap.put(boardNo, imageUrl);
                        boardWHMap.put(boardNo, imageWH);*/
                    //uuidPageNumBoardMap.put(coursewareUuid + pageNum, boardNo);
                    // } else {
                   /*     Integer boardNoM = uuidPageNumBoardMap.get(coursewareUuid + pageNum);
                        if (boardNoM == null) {
                            pageNo++;
                            boardNo = pageNo + 2;
                            //uuidPageNumBoardMap.put(coursewareUuid + pageNum, boardNo);
                        } else {
                            boardNo = boardNoM;
                        }*/
                    // }
                    //drawList.add(wrapTrackData(time, "1", String.valueOf(boardNo), "1"));
                } else if ("10".equals(methodtype)) {
                    //LOGGER.info("切换课件页面");
                    String methodparam = dataObj.getString("methodparam");
                    JSONObject methodparamObj = JSON.parseObject(methodparam);
                    String time = methodparamObj.getString("time");
                    time = String.valueOf(Integer.parseInt(time) + getDurationTime(timeList));
                    String coursewareUuid = methodparamObj.getString("courseId");
                    int pageNum = Integer.parseInt(methodparamObj.getString("pageNum"));
                    Integer boardNoM = uuidPageNumBoardMap.get(coursewareUuid + pageNum);

                    //add
                    String boardWidth = methodparamObj.getString("boardWidth");
                    String boardHeight = methodparamObj.getString("boardHeight");

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

                        for (CoursewareImage coursewareImage : coursewareImageList) {
                            if (!Objects.equals(coursewareImage.getImageUrl(), null) && !coursewareImage.getImageUrl().contains("http://")) {
                                imageUrlAllList.add(OssTeacherUtils.generateFileUrl(ossClient, coursewareImage.getImageUrl()));
                                String ossUrl=OssTeacherUtils.generateFileUrl(ossClient, coursewareImage.getImageUrl());
                            } else {
                                imageUrlAllList.add(coursewareImage.getImageUrl());
                            }
                        }

                        String[] imageWH = new String[]{boardWidth, boardHeight};
                        imageWHMap.put(coursewareUuid, imageWH);
                        imageUrlAllMap.put(coursewareUuid, imageUrlAllList);
                    }
                    String imageUrl = imageUrlAllMap.get(coursewareUuid).get(pageNum);
                    boardImageUrlMap.put(boardNo, imageUrl);
                    boardWHMap.put(boardNo, imageWHMap.get(coursewareUuid));
                    drawList.add(wrapTrackData(time, "0", String.valueOf(boardNo)));
                } else {
                    //LOGGER.info("未知methodtype={}", methodtype);
                }
            }
            //LOGGER.info("timeList={}", timeList);
            if (!recordFlag) {
                throw new Exception("dbFilePath=" + dbFilePath + " :没有methodtype=17 ！！！");
            }
            trackData.setDuration(duration);
            imageUrlList.addAll(boardImageUrlMap.values());
            //图片url保存完成后关闭oss
            ossClient.shutdown();

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
        } catch (Exception e) {
            LOGGER.info("正在解析{}第【{}】行记录", dbFilePath, row);
            //throw new Exception("解析" + dbFilePath + "第" + row + "行记录异常!");
            LOGGER.error(e.getMessage());
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
        return trackData;
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
     * @param classRoomService
     * @param classMateService
     * @param recordLogsDir
     * @param messageService
     * @param classRecordDataService
     * @param recordDir
     * @param ymlMyConfig
     * @param coursewareService
     */
    public static void statisticsTeacherRecordDataByThread(String courseUuid,ClassRoomService classRoomService, ClassMateService classMateService, File recordLogsDir, MessageService messageService, ClassRecordDataService classRecordDataService, String recordDir, YmlMyConfig ymlMyConfig, CoursewareService coursewareService, Integer comTotalTime) {
        LOGGER.info("***** 统计老师和学生的回放数据(音频和轨迹数据)");
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
                            LOGGER.info("statisticsTeacherVoiceData.");
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
    public static void statisticsTeacherRecordDataByThreadPhp(String courseUuid,ClassRoomService classRoomService, ClassMateService classMateService, File recordLogsDir, MessageService messageService, ClassRecordDataService classRecordDataService, String recordDir, YmlMyConfig ymlMyConfig, CoursewareService coursewareService, Integer comTotalTime) {
        LOGGER.info("***** 统计老师和学生的回放数据(音频和轨迹数据)");
        ClassRoom room = classRoomService.findByCourseUuid(courseUuid);
        if (room != null) {
            List<ClassMate> classMateList = classMateService.findByClassRoomUuid(room.getClassRoomUuid());
            if (classMateList != null && classMateList.size() > 1) {

                List<ClassRecordData> classRecordDataList = classRecordDataService.findByClassRoomUuid(room.getClassRoomUuid());
                if (classRecordDataList == null || classRecordDataList.size() == 0) {
                    new Thread(() -> {
                        LOGGER.info("*****课程{}保存音频和解析轨迹文件线程开始！*****", courseUuid);
                        Integer statisticsStatus=0;
                        ClassRoom classRoom = new ClassRoom();
                        try {
                            statisticsTeacherVoiceData(courseUuid, recordLogsDir, recordDir, ymlMyConfig, classRoomService, classRecordDataService, comTotalTime);

                            List<ClassRecordData> classRecordDataList1 = classRecordDataService.findByClassRoomUuidAndRecordRole(room.getClassRoomUuid(), 0);
                            if (classRecordDataList1 != null && classRecordDataList1.size() != 0) {
                                //调用php
                                uniteRecordPhp(classRecordDataList1, courseUuid, recordDir, ymlMyConfig, classRoomService, coursewareService);
                            }
                            statisticsStatus=2;
                            classRoom.setCourseUuid(courseUuid);
                            classRoom.setStatisticsStatus(2);

                        } catch (Exception e) {
                            statisticsStatus=1;
                            LOGGER.info("*****课程{}保存音频和解析轨迹文件异常！*****", courseUuid, e);
                            try {
                                messageService.sendExceptionMessage(SENDPHONE, courseUuid, e.getMessage());
                            } catch (Exception e1) {
                                LOGGER.error(e.toString(), e);
                            }
                            LOGGER.error(e.toString(), e);
                        }

                        //保留原更新用于是否有回放资源校验（上面不抛异常后，状态不为空才更新统计状态）
                        if(classRoom.getStatisticsStatus()!=null){
                            int j=classRoomService.updateByCourseUuid(classRoom);
                            //java更新后更新php
                            if(j>0){
                                roomUpdate(courseUuid,null,0,null,statisticsStatus);
                            }
                        }

                    }).start();
                }
            }
        }
    }

    /**
     * 统计老师和学生的回放数据(音频和轨迹数据)
     * 新增此方法，变动：调用了statisticsTeacherVoiceDataNew
     * 用于录制的mp4视频，统计学生和老师的音频和轨迹数据并入库
     * 及测试环境(/www/clientfile/uploadPath/recordDir/20180703)上传mp4原文件、合并后的_av.mp4文件、合并的轨迹文件到阿里云
     * ,Integer comTotalTime
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

    public static void statisticsTeacherRecordDataByThreadNew(String courseUuid, CpCourseService cpCourseService, ClassRoomService classRoomService, ClassMateService classMateService, File recordLogsDir, MessageService messageService, ClassRecordDataService classRecordDataService, String recordDir, YmlMyConfig ymlMyConfig, CoursewareService coursewareService, Integer comTotalTime) {
        LOGGER.info("***** 统计老师和学生的回放数据(音频和轨迹数据)");
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
                            statisticsTeacherVoiceDataNew(courseUuid, recordLogsDir, recordDir, ymlMyConfig, classRoomService, classRecordDataService, comTotalTime);
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
                String aacFilePath ="";
                String m4aFilePath ="";
                String[] aacAndM4aFilePaths=null;

                if(StringUtils.isNotEmpty(aacDirPath)){
                    aacAndM4aFilePaths = aac2m4a(aacDirPath);
                }
                //如果path文件第二位指向的目录不存在aac文件，不执行转换脚本
                if(StringUtils.isNotEmpty(aacAndM4aFilePaths[0])){
                    aacFilePath = aacAndM4aFilePaths[0];
                }
                if(StringUtils.isNotEmpty(aacAndM4aFilePaths[1])){
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
                String aacSaveFileName = courseUuid + "_" + aacFile.getName();
                File aacSaveFile = new File(saveDir, aacSaveFileName);
                copyFile(aacFile, aacSaveFile);
                String m4aSaveFileName = courseUuid + "_" + m4aFile.getName();
                File m4aSaveFile = new File(webSaveDir, m4aSaveFileName);
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
     * ,Integer comTotalTime
     *
     * @param courseUuid
     * @param recordLogsDir
     * @param recordDir
     * @param ymlMyConfig
     * @param classRoomService
     * @param classRecordDataService
     * @throws Exception
     */
    private static void statisticsTeacherVoiceDataNew(String courseUuid, File recordLogsDir, String recordDir, YmlMyConfig ymlMyConfig, ClassRoomService classRoomService, ClassRecordDataService classRecordDataService, Integer comTotalTime) throws Exception {
        /**点击下课到mp4源文件写入完成大概需要5秒，然后检验文件是否写完整，标志是recording2-done.txt是否存在*/
        Thread.sleep(5 * 1000);

        ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
        if (classRoom == null) {
            throw new Exception("classRoom is null");
        }
        String pathFileName = courseUuid + ".path";
        File[] listFiles = recordLogsDir.listFiles();
        String content = readFileToString(listFiles, pathFileName);
        //LOGGER.info("statisticsSCVoiceData comTotalTime:" + comTotalTime);
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
                File aacDirTemp = new File(aacDirPath);
                File[] listFileTemp = aacDirTemp.listFiles();
                for (File file : listFileTemp) {
                    String filePath = file.getPath();
                    if (filePath.endsWith("recording2-done.txt")) {
                        LOGGER.info("mp4源文件写入完成，启动脚本转换_av.mp4文件...");
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
            //某一个aac文件不存在不终止，继续执行存在acc文件的目录进行转换，
            //正常情况客户端只有生成acc文件才会写acc文件的目录到path文件，
            //但是可能会存在没有生成acc文件也写到到了path文件中；
            //throw new Exception("aacDir is not exist,aacDirPath:" + aacDirPath);
            LOGGER.error("aac文件不存在,aac文件目录:"+aacDirPath);
        }else{
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
    public static void statisticsSCRecordDataByThread(String courseUuid, ClassRoomService classRoomService, ClassMateService classMateService, File recordLogsDir, MessageService messageService, ClassRecordDataService classRecordDataService, String recordDir, YmlMyConfig ymlMyConfig, Integer comTotalTime) {
        ClassRoom room = classRoomService.findByCourseUuid(courseUuid);
        if (room != null) {
            List<ClassMate> classMateList = classMateService.findByClassRoomUuid(room.getClassRoomUuid());
            if (classMateList != null && classMateList.size() > 1) {
                //aac文件转成m4a文件和flac，并获取m4a文件时长，并保存到数据库中
                List<ClassRecordData> classRecordDataList = classRecordDataService.findByClassRoomUuid(room.getClassRoomUuid());
                if (classRecordDataList == null || classRecordDataList.size() == 0) {
                    new Thread(() -> {
                    LOGGER.info("*****课程{}保存SC音频文件线程开始！*****", courseUuid);

                    ClassRoom classRoom = new ClassRoom();
                    classRoom.setCourseUuid(courseUuid);
                    try {
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
    public static void statisticsSCRecordDataByThreadPhp(String courseUuid, ClassRoomService classRoomService, ClassMateService classMateService, File recordLogsDir, MessageService messageService, ClassRecordDataService classRecordDataService, String recordDir, YmlMyConfig ymlMyConfig, Integer comTotalTime) {
        ClassRoom room = classRoomService.findByCourseUuid(courseUuid);
        if (room != null) {
            List<ClassMate> classMateList = classMateService.findByClassRoomUuid(room.getClassRoomUuid());
            if (classMateList != null && classMateList.size() > 1) {
                List<ClassRecordData> classRecordDataList = classRecordDataService.findByClassRoomUuid(room.getClassRoomUuid());
                if (classRecordDataList == null || classRecordDataList.size() == 0) {
                    new Thread(() -> {
                        LOGGER.info("*****课程{}保存SC音频文件线程开始！*****", courseUuid);
                        try {
                            statisticsSCVoiceData(courseUuid, recordLogsDir, recordDir, ymlMyConfig, classRoomService, classRecordDataService, comTotalTime);
                            //调用php
                            Response phpResponse=null;
                            try {
                                String phpUrl =NO_ACCESS_URL.concat("client/room/update");
                                CloseableHttpClient client = HttpClients.createDefault();
                                HttpPost post=new HttpPost(phpUrl);
                                post.setHeader("Content-Type","application/json;charset=utf-8");

                                JSONObject param= new JSONObject();
                                param.put("courseUuid", courseUuid);
                                param.put("statisticsStatus", 2);
                                StringEntity stringEntity = new StringEntity(param.toString());
                                post.setEntity(stringEntity);

                                CloseableHttpResponse response = client.execute(post);
                                HttpEntity entity=response.getEntity();
                                String result= EntityUtils.toString(entity,"UTF-8");
                                LOGGER.info("statisticsSCRecordDataByThreadPhp client/room/update result ={}",result);
                                phpResponse = JSON.parseObject(result, Response.class);
                                if(!Objects.equals(CodeEnum.SUCCESS.getCode(),phpResponse.getCode())){
                                    LOGGER.error("PHP 更新房间统计状态失败！");
                                }
                            }catch (Exception e){
                                LOGGER.error(e.getMessage());
                            }

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
    public static void statisticsSCRecordDataByThreadNew(String courseUuid, ClassRoomService classRoomService, ClassMateService classMateService, File recordLogsDir, MessageService messageService, ClassRecordDataService classRecordDataService, String recordDir, YmlMyConfig ymlMyConfig, Integer comTotalTime) {
        ClassRoom room = classRoomService.findByCourseUuid(courseUuid);
        if (room != null) {
            List<ClassMate> classMateList = classMateService.findByClassRoomUuid(room.getClassRoomUuid());
            if (classMateList != null && classMateList.size() > 1) {
                //aac文件转成m4a文件和flac，并获取m4a文件时长，并保存到数据库中
                List<ClassRecordData> classRecordDataList = classRecordDataService.findByClassRoomUuid(room.getClassRoomUuid());
                if (classRecordDataList == null || classRecordDataList.size() == 0) {
                    //new Thread(() -> {
                    LOGGER.info("*****课程{}保存SC音频文件线程开始！*****", courseUuid);
                    ClassRoom classRoom = new ClassRoom();
                    classRoom.setCourseUuid(courseUuid);
                    try {
                        statisticsSCVoiceDataNew(courseUuid, recordLogsDir, recordDir, ymlMyConfig, classRoomService, classRecordDataService, comTotalTime);
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
                    //}).start();
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
        //LOGGER.info("statisticsSCVoiceData comTotalTime:" + comTotalTime);
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
    private static void statisticsSCVoiceDataNew(String courseUuid, File recordLogsDir, String recordDir, YmlMyConfig ymlMyConfig, ClassRoomService classRoomService, ClassRecordDataService classRecordDataService, Integer comTotalTime) throws Exception {
        ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
        if (classRoom == null) {
            throw new Exception("classRoom is null");
        }
        String pathFileName = courseUuid + ".path";
        File[] listFiles = recordLogsDir.listFiles();
        String content = readFileToString(listFiles, pathFileName);
        //LOGGER.info("statisticsSCVoiceDataNew comTotalTime:" + comTotalTime);
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
        LOGGER.info("creatTxtFile txtName:" + txtName);
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

    /**
     * 检查老师学生是否共同在线上课
     *
     * @param courseUuid
     * @param recordLogsDir
     * @throws Exception
     */
    public static int checkAllInClass(String courseUuid, File recordLogsDir, ClassMateService classMateService, ClassRecordService classRecordService, ClassRoomService classRoomService, CpCourseService cpCourseService) throws Exception {
        LOGGER.info("检查老师学生是否共同在线上课.");
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
        return statisticsClassTimeResult.getComTotalTime();
    }
}


