package cn.onlyhi.client.controller;

import cn.onlyhi.client.config.YmlMyConfig;
import cn.onlyhi.client.dto.CourseDto;
import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.po.Channel;
import cn.onlyhi.client.po.ClassRoom;
import cn.onlyhi.client.po.CpCourse;
import cn.onlyhi.client.po.Leads;
import cn.onlyhi.client.po.UserEasemob;
import cn.onlyhi.client.service.*;
import cn.onlyhi.common.util.Response;
import cn.onlyhi.common.util.SecurityUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static cn.onlyhi.common.constants.Constants.PERSONOFCOURSE_KEY;
import static cn.onlyhi.common.enums.ClientEnum.UserType.CC;
import static cn.onlyhi.common.enums.ClientEnum.UserType.CR;
import static cn.onlyhi.common.enums.ClientEnum.UserType.STUDENT;
import static cn.onlyhi.common.enums.ClientEnum.UserType.TEACHER;
import static cn.onlyhi.common.util.ClientUtil.getAgoraUid;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/7/3.
 */
public class BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    protected LeadsService leadsService;
    @Autowired
    protected RedisService redisService;
    @Autowired
    protected ChannelService channelService;
    @Autowired
    protected YmlMyConfig ymlMyConfig;
    @Autowired
    protected LeadsExtService leadsExtService;
    @Autowired
    protected UserEasemobService userEasemobService;
    @Autowired
    protected CpCourseService cpCourseService;
    @Autowired
    protected ClassRoomService classRoomService;
    @Autowired
    protected ClassRecordService classRecordService;
    @Value("${phpStaging.url}")
    private String url;

    /**
     * 获取课程的录制状态，即有无回放数据
     *
     * @param courseUuid
     * @return
     */
    protected int getRecordStatus(String courseUuid) {
        int recordStatus = 1;
        CpCourse cpCourse = cpCourseService.findByUuid(courseUuid);
        Integer classStatus = cpCourse.getClassStatus();
        Integer studentClassStatus = cpCourse.getStudentClassStatus();
        if (Objects.equals(classStatus,0) || Objects.equals(studentClassStatus,0)) {
            recordStatus = 2;
            return recordStatus;
        }
        ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
        if (classRoom == null) {
            LOGGER.info("classRoom == null");
            recordStatus = 2;
            return recordStatus;
        }
        Integer statisticsStatus = classRoom.getStatisticsStatus();
        if (statisticsStatus == 0) {
            recordStatus = 3;
            return recordStatus;
        }
   /*     String trackUrl = classRoom.getTrackUrl();
        String voiceUrl = classRoom.getVoiceUrl();
        Integer voiceDuration = classRoom.getVoiceDuration();
        Integer vedioDuration=classRoom.getVideoDuration();
        String vedioUrl = classRoom.getMp4VideoUrl(); || StringUtils.isBlank(trackUrl)*/
        if (statisticsStatus == 1 ) {
            recordStatus = 2;
            return recordStatus;
        }
        return recordStatus;
    }
    /**
     * 获取课程的录制状态，即有无回放数据
     * 调用php
     * @param courseUuid
     * @return
     */
    protected int getRecordStatusPhp(String courseUuid,String phpToken) {
        String phpCacheToken = redisService.getLoginUserCache(phpToken).getToken();
        int recordStatus = 1;
        CpCourse cpCourse =null;
        Response phpResponse;
        try{
            String phpUrl =url.concat("client/enterRoom/check");
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post=new HttpPost(phpUrl);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            post.setHeader("Authorization", "Bearer ".concat(phpCacheToken));
            post.setHeader("Accept", "application/json");

            JSONObject param= new JSONObject();
            param.put("courseUuid", courseUuid);
            StringEntity stringEntity = new StringEntity(param.toString());
            post.setEntity(stringEntity);

            LOGGER.info("获取老师与学生的课程状态 传给php ={}",JSON.toJSONString(param));

            CloseableHttpResponse response = client.execute(post);
            HttpEntity entity=response.getEntity();
            String result= EntityUtils.toString(entity,"UTF-8");
            if(result==null){
                LOGGER.info("获取老师与学生的课程状态，php 返回空");
            }

            phpResponse = JSON.parseObject(result, Response.class);
            Object object=phpResponse.getData();
            if(object!=null){
                cpCourse=JSON.parseObject(object.toString(),CpCourse.class);
            }

        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        //recordStatus 1:有回放，2:没有回放数据，3:视频转换中，次日方可查看
        try {
            Integer classStatus = cpCourse.getClassStatus();
            Integer studentClassStatus = cpCourse.getStudentClassStatus();
            if (Objects.equals(classStatus,0) || Objects.equals(studentClassStatus,0)) {
                LOGGER.info("Objects.equals(classStatus,0) || Objects.equals(studentClassStatus,0)={}",courseUuid);
                recordStatus = 2;
                return recordStatus;
            }
            ClassRoom classRoom = classRoomService.findByCourseUuid(courseUuid);
            if (classRoom == null) {
                LOGGER.info("classRoom == null={}",courseUuid);
                recordStatus = 2;
                return recordStatus;
            }
            Integer statisticsStatus = classRoom.getStatisticsStatus();
            if (statisticsStatus == 0) {
                recordStatus = 3;
                return recordStatus;
            }
            if (statisticsStatus == 1 ) {
                LOGGER.info("statisticsStatus == 1={}",courseUuid);
                recordStatus = 2;
                return recordStatus;
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }

        return recordStatus;
    }
    /**
     * 根据房间时候存在人获取真正的待上课或课程记录列表
     *
     * @param courseDtoList
     * @param pageNo
     * @param pageSize
     * @param isCourseRecord
     * @return
     * @throws ParseException
     */
    protected Object[] getCourseDtoPageList(List<CourseDto> courseDtoList, int pageNo, int pageSize, boolean isCourseRecord, String userType) throws ParseException {

        List<CourseDto> courseDtoTmpList = new ArrayList<>();
        List<CourseDto> courseDtoPageList = new ArrayList<>();
        try{
            Date date = new Date();
            long currentTimeMillis = date.getTime();
            String currentDay = DateFormatUtils.format(date, "yyyy-MM-dd");
            for (CourseDto courseDto : courseDtoList) {
                String courseDate = courseDto.getCourseDate();
                String endTime = courseDto.getEndTime();
                long time =0;
                if(courseDate!=null && endTime!=null){
                    time = DateUtils.parseDate(courseDate + " " + endTime, "yyyy-MM-dd HH:mm").getTime();
                }
                Integer courseType = courseDto.getCourseType();
                String courseUuid = courseDto.getCourseUuid();
                //调试课逻辑与下面测评课和正式课不同
                if (Objects.equals(courseType,2)) {
                    courseDtoTmpList.add(courseDto);
                } else {
                    if (isCourseRecord) {
                        Integer classStatus = getClassStatus(courseDto, userType);
                        //已主动下课
                        if (Objects.equals(classStatus,1)) {
                            courseDtoTmpList.add(courseDto);
                        } else {
                            //过去了一天
                            if (!currentDay.equals(courseDate)) {
                                courseDtoTmpList.add(courseDto);
                            } else {
                                //课程时间到了并且房间内没有人
                                if (time <= currentTimeMillis && !isExistPersonOfCourse(courseUuid)) {
                                    courseDtoTmpList.add(courseDto);
                                }
                            }
                        }
                    } else {
                        //未结束列表
                        //未主动下课
                        if (getClassStatus(courseDto, userType) != 1) {
                            //课程时间未到
                            if (time > currentTimeMillis) {
                                courseDtoTmpList.add(courseDto);
                            } else {
                                //课程时间到了，但是房间还有人
                                if (isExistPersonOfCourse(courseUuid)) {
                                    courseDtoTmpList.add(courseDto);
                                }
                            }
                        }
                    }
                }
            }
            int startSize = (pageNo - 1) * pageSize;
            int endSize = courseDtoTmpList.size() > pageSize * pageNo ? pageSize * pageNo : courseDtoTmpList.size();
            for (int i = startSize; i < endSize; i++) {
                courseDtoPageList.add(courseDtoTmpList.get(i));
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return new Object[]{courseDtoTmpList.size(), courseDtoPageList};
    }

    /**
     * 根据用户获取课程状态
     *
     * @param courseDto
     * @param userType  大写
     * @return
     */
    protected Integer getClassStatus(CourseDto courseDto, String userType) {
        Integer classStatus = 0;
        if (STUDENT.name().equals(userType)) {
            classStatus = courseDto.getStudentClassStatus();
        } else if (TEACHER.name().equals(userType)) {
            classStatus = courseDto.getClassStatus();
        } else if (CC.name().equals(userType)) {
            classStatus = courseDto.getCcClassStatus();
        } else if (CR.name().equals(userType)) {
            classStatus = courseDto.getCrClassStatus();
        } else {    //其他userType以学生课程状态为标准
            classStatus = courseDto.getStudentClassStatus();
        }
        return classStatus;
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

    protected String getAuthCode(String phone) {
        String authCodecache;
        if ("192.168.0.251".equals(ymlMyConfig.getServerIp()) || "17721432057".equals(phone)) {
            authCodecache = "8888";
        } else {
            authCodecache = redisService.get(phone + "ClientAuthCode");
        }
        return authCodecache;
    }

    /**
     * 根据渠道adid获取其uuid
     *
     * @param adid
     * @return
     */
    protected String getChannelUuid(String adid) {
        Channel channel = channelService.findChannelByAdid(adid);
        if (channel == null) {
            return "unknown";
        }
        return channel.getUuid();
    }

    /**
     * pc客户端渠道uuid
     *
     * @return
     */
    protected String getPcChannelUuid() {
        String adid = "FO3QXNGZK8";
        Channel channel = channelService.findChannelByAdid(adid);
        if (channel == null) {
            return "unknown";
        }
        return channel.getUuid();
    }

    /**
     * app渠道uuid
     *
     * @return
     */
    protected String getChannelUuid() {
        String adid = "IL1L1HTUVX";
        Channel channel = channelService.findChannelByAdid(adid);
        if (channel == null) {
            return "unknown";
        }
        return channel.getUuid();
    }

    protected String getIndexToken(String phone, String userType) {
        return phone + userType;
    }

    protected ResponseEntity<Response> userLogin(String phone, String deviceId, String deviceType) {
        return userLogin(phone, deviceId, deviceType, null);
    }

    protected ResponseEntity<Response> userLogin(String phone, String deviceId, String deviceType, String userName) {
        LoginUserCache loginUserCache = new LoginUserCache();
        Leads leads = leadsService.findLeadsByPhone(phone);
        if (leads == null) {
            return ResponseEntity.ok(Response.success(loginUserCache));
        }
        String userType = STUDENT.name();
        deviceType = deviceType.toUpperCase();
        String newToken = SecurityUtil.hashMD5Hex(phone + deviceId + userType);
        String indexToken = getIndexToken(phone, userType);
        String cacheToken = redisService.get(indexToken);
        if (!newToken.equals(cacheToken)) {
            String leadsUuid = leads.getUuid();
            //重置token和用户信息
            loginUserCache = new LoginUserCache();
            loginUserCache.setPhone(phone);
            loginUserCache.setDeviceId(deviceId);
            loginUserCache.setDeviceType(deviceType);
            loginUserCache.setUserType(userType);
            loginUserCache.setToken(newToken);
            loginUserCache.setUserName(leads.getName());
            loginUserCache.setUserUuid(leadsUuid);
            loginUserCache.setPassword(leads.getPassword());
            loginUserCache.setAgoraUid(getAgoraUid(leadsUuid, userType));

            /*LeadsExt leadsExt = leadsExtService.findLeadsExtByLeadsUuid(leadsUuid);
            if (leadsExt != null && StringUtils.isNotBlank(leadsExt.getEasemobUuid())) {
                loginUserCache.setRegisterIMFlag(true);
            }*/
            UserEasemob userEasemob = userEasemobService.findByUserUuid(leadsUuid);
            if (userEasemob != null) {
                loginUserCache.setRegisterIMFlag(true);
            }

            //登录成功
            redisService.set(indexToken, newToken);
            if (StringUtils.isNotBlank(cacheToken)) {
                redisService.rename(cacheToken, newToken);
            }
            redisService.setLoginUserCache(newToken, loginUserCache);
        } else {
            //延长过期时间
            redisService.expire(cacheToken);
            loginUserCache = redisService.getLoginUserCache(cacheToken);
            if (loginUserCache.getAgoraUid() == 0) {    //处理已缓存账号
                loginUserCache.setAgoraUid(getAgoraUid(leads.getUuid(), userType));
            }
            if (userName != null) {
                loginUserCache.setUserName(userName);
                redisService.setLoginUserCache(cacheToken, loginUserCache);
            }
        }
        loginUserCache.setPassword("");
        return ResponseEntity.ok(Response.success(loginUserCache));
    }

}
