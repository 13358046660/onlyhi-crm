package cn.onlyhi.client.task;


import cn.onlyhi.client.config.UmengPushConfig;
import cn.onlyhi.client.po.CpCourse;
import cn.onlyhi.client.po.PushMessage;
import cn.onlyhi.client.push.AndroidNotification;
import cn.onlyhi.client.push.PushClient;
import cn.onlyhi.client.push.PushInfo;
import cn.onlyhi.client.push.android.AndroidGroupcast;
import cn.onlyhi.client.push.android.AndroidUnicast;
import cn.onlyhi.client.push.ios.IOSGroupcast;
import cn.onlyhi.client.push.ios.IOSUnicast;
import cn.onlyhi.client.service.CpCourseService;
import cn.onlyhi.client.service.PushMessageService;
import cn.onlyhi.common.enums.ClientEnum;
import cn.onlyhi.common.util.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 上课前给学生推送即将上课的消息的定时任务
 *
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/5/23.
 */
@Component
@Configuration
@EnableScheduling // 启用定时任务
public class PushMessageTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(PushMessageTask.class);

    private static String title = "嗨课堂上课提醒";
    private static String content = "您有一节课程将在30分钟后开始，请提前做好上课准备哦~";

    @Autowired
    private CpCourseService cpCourseService;
    @Autowired
    private PushMessageService pushMessageService;
    @Autowired
    private UmengPushConfig umengPushConfig;

    @Scheduled(cron = "0 0/15 * * * ? ")    //每15分钟执行: 0 0/15 * * * ?  每十秒执行:  0/10 * * * * ?
    public void pushMessage() throws Exception {
        LOGGER.info("【上课消息友盟推送】定时任务开始...");
        Calendar calendar = Calendar.getInstance();
        long begin = calendar.getTimeInMillis();
        String currentDate = FileUtils.getFormatDate(calendar, FileUtils.DATEPATTERN);
        String currentTime = FileUtils.getFormatTime(calendar, FileUtils.TIMEPATTERN);
        //查询所有课程未开始且消息未推送的信息
        List<CpCourse> cpCoursesList = cpCourseService.selectStudentNoPushMessageCourseList(currentDate, currentTime);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (cpCoursesList.size() > 0) {
            //立即推送的leadsList
            List<PushInfo> pushInfoList = new ArrayList<>();
            //定时推送的信息列表
            List<PushInfo> timingPushInfoList = new ArrayList<>();
            PushInfo pushInfo;
            for (CpCourse cpCourse : cpCoursesList) {
                String leadsUuid = cpCourse.getLeadsUuid();
                String courseDate = cpCourse.getCourseDate();
                String startTime = cpCourse.getStartTime();
                Date courseStartTime = dateFormat.parse(courseDate + " " + startTime + ":00");
                long time = courseStartTime.getTime(); //上课时间
                long time1 = time - 30 * 60 * 1000; //上课前30分钟
                if (begin > time1) { //需立即推送消息
                    long intervalTime = (time - begin) / (1000 * 60);
                    pushInfo = new PushInfo();
                    pushInfo.setLeadsUuid(leadsUuid);
                    pushInfo.setTitle(title);
                    pushInfo.setContent(content.replace("30", String.valueOf(intervalTime)));
                    pushInfoList.add(pushInfo);
                } else {
                    String pushTime = dateFormat.format(new Date(time1));
                    pushInfo = new PushInfo();
                    pushInfo.setLeadsUuid(leadsUuid);
                    pushInfo.setPushTime(pushTime);
                    pushInfo.setTitle(title);
                    pushInfo.setContent(content);
                    timingPushInfoList.add(pushInfo);
                }
            }
            //立即推送消息
            if (pushInfoList.size() > 0) {
                pushMessageToApp(pushInfoList);
            }
            //上课前30分钟推送
            if (timingPushInfoList.size() > 0) {
                timingPushMessageToApp(timingPushInfoList);
            }
        }
        long end = System.currentTimeMillis();
        LOGGER.info("【上课消息友盟推送】定时任务结束，共耗时：[{}]秒", (end - begin) / 1000);
    }

    /**
     * 定时给移动端推送消息
     *
     * @param timingPushInfoList
     */
    private void timingPushMessageToApp(List<PushInfo> timingPushInfoList) throws Exception {
        List<PushInfo> androidPushInfoList = new ArrayList<>();
        List<PushInfo> iosPushInfoList = new ArrayList<>();
        List<String> leadsUuidList = timingPushInfoList.stream().map(pushInfo -> pushInfo.getLeadsUuid()).collect(toList());
        List<PushMessage> pushMessageList = pushMessageService.findByLeadsUuids(leadsUuidList);
        for (PushMessage pushMessage : pushMessageList) {
            String deviceType = pushMessage.getDeviceType();
            String leadsUuid = pushMessage.getLeadsUuid();
            String tag = pushMessage.getTag();
            PushInfo info = timingPushInfoList.stream().filter(s -> s.getLeadsUuid().equals(leadsUuid)).findFirst().get();
            info.setTag(tag);
            if (ClientEnum.DeviceType.ANDROID.name().equals(deviceType) || ClientEnum.DeviceType.APAD.name().equals(deviceType)) {
                androidPushInfoList.add(info);
            } else if (ClientEnum.DeviceType.IOS.name().equals(deviceType) || ClientEnum.DeviceType.IPAD.name().equals(deviceType)) {
                iosPushInfoList.add(info);
            }
        }

        List<String> androidLeadsUuidList = new ArrayList<>();
        if (androidPushInfoList.size() > 0) {
            LOGGER.info("android需定时推送消息数：[{}]", androidPushInfoList.size());
            for (PushInfo pushInfo : androidPushInfoList) {
                boolean androidSendFlag = androidTimingPushMessage(pushInfo);
                if (androidSendFlag) {
                    LOGGER.info("android定时推送成功,推送时间:[{}]", pushInfo.getPushTime());
                    androidLeadsUuidList.add(pushInfo.getLeadsUuid());
                }
            }
        }
        List<String> iosLeadsUuidList = new ArrayList<>();
        if (iosPushInfoList.size() > 0) {
            LOGGER.info("ios需定时推送消息数：[{}]", iosPushInfoList.size());
            for (PushInfo pushInfo : iosPushInfoList) {
                boolean iosSendFlag = iosTimingPushMessage(pushInfo);
                if (iosSendFlag) {
                    LOGGER.info("ios定时推送成功,推送时间:[{}]", pushInfo.getPushTime());
                    iosLeadsUuidList.add(pushInfo.getLeadsUuid());
                }
            }
        }

        List<String> pushLeadsUuidList = new ArrayList<>();
        pushLeadsUuidList.addAll(androidLeadsUuidList);
        pushLeadsUuidList.addAll(iosLeadsUuidList);
        if (pushLeadsUuidList.size() > 0) {
            LOGGER.info("android定时推送成功消息数：[{}]", androidLeadsUuidList.size());
            LOGGER.info("ios定时推送成功消息数：[{}]", iosLeadsUuidList.size());
            cpCourseService.updatePushStatus(pushLeadsUuidList);
        }

    }


    /**
     * 立即给移动端推送消息
     *
     * @param pushInfoList
     */
    private void pushMessageToApp(List<PushInfo> pushInfoList) throws Exception {
        List<PushInfo> androidPushInfoList = new ArrayList<>();
        List<PushInfo> iosPushInfoList = new ArrayList<>();
        List<String> leadsUuidList = pushInfoList.stream().map(pushInfo -> pushInfo.getLeadsUuid()).collect(toList());
        List<PushMessage> pushMessageList = pushMessageService.findByLeadsUuids(leadsUuidList);
        for (PushMessage pushMessage : pushMessageList) {
            String deviceToken = pushMessage.getDeviceToken();
            String deviceType = pushMessage.getDeviceType();
            String leadsUuid = pushMessage.getLeadsUuid();
            PushInfo info = pushInfoList.stream().filter(s -> s.getLeadsUuid().equals(leadsUuid)).findFirst().get();
            info.setDeviceToken(deviceToken);
            if (ClientEnum.DeviceType.ANDROID.name().equals(deviceType) || ClientEnum.DeviceType.APAD.name().equals(deviceType)) {
                androidPushInfoList.add(info);
            } else if (ClientEnum.DeviceType.IOS.name().equals(deviceType) || ClientEnum.DeviceType.IPAD.name().equals(deviceType)) {
                iosPushInfoList.add(info);
            }
        }

        List<String> androidLeadsUuidList = new ArrayList<>();
        if (androidPushInfoList.size() > 0) {
            LOGGER.info("android需推送消息数：[{}]", androidPushInfoList.size());
            for (PushInfo pushInfo : androidPushInfoList) {
                boolean androidSendFlag = androidPushMessage(pushInfo);
                if (androidSendFlag) {
                    androidLeadsUuidList.add(pushInfo.getLeadsUuid());
                }
            }
        }
        List<String> iosLeadsUuidList = new ArrayList<>();
        if (iosPushInfoList.size() > 0) {
            LOGGER.info("ios需推送消息数：[{}]", iosPushInfoList.size());
            for (PushInfo pushInfo : iosPushInfoList) {
                boolean iosSendFlag = iosPushMessage(pushInfo);
                if (iosSendFlag) {
                    iosLeadsUuidList.add(pushInfo.getLeadsUuid());
                }
            }
        }

        List<String> pushLeadsUuidList = new ArrayList<>();
        pushLeadsUuidList.addAll(androidLeadsUuidList);
        pushLeadsUuidList.addAll(iosLeadsUuidList);
        if (pushLeadsUuidList.size() > 0) {
            LOGGER.info("android推送成功消息数：[{}]", androidLeadsUuidList.size());
            LOGGER.info("ios推送成功消息数：[{}]", iosLeadsUuidList.size());
            cpCourseService.updatePushStatus(pushLeadsUuidList);
        }
    }

    /**
     * ios推送定时消息
     *
     * @param timingPushInfo
     * @return
     * @throws Exception
     */
    private boolean iosTimingPushMessage(PushInfo timingPushInfo) throws Exception {
        String appkey = umengPushConfig.getIosAppkey();
        String appMasterSecret = umengPushConfig.getIosAppMasterSecret();
        String content = timingPushInfo.getContent();
        String tag = timingPushInfo.getTag();
        String pushTime = timingPushInfo.getPushTime();

        JSONObject filterJson = new JSONObject();
        JSONObject whereJson = new JSONObject();
        JSONArray tagArray = new JSONArray();
        JSONObject testTag = new JSONObject();
        testTag.put("tag", tag);
        tagArray.put(testTag);
        whereJson.put("and", tagArray);
        filterJson.put("where", whereJson);

        IOSGroupcast groupcast = new IOSGroupcast(appkey, appMasterSecret);
        groupcast.setFilter(filterJson);
        groupcast.setAlert(content);
        groupcast.setBadge(1);
        groupcast.setSound("default");
        groupcast.setProductionMode();
        groupcast.setStartTime(pushTime);
        groupcast.setCustomizedField("class_reminder", "1");

        PushClient client = new PushClient();
        return client.send(groupcast);
    }

    /**
     * android推送定时消息
     *
     * @param timingPushInfo
     * @return
     * @throws Exception
     */
    private boolean androidTimingPushMessage(PushInfo timingPushInfo) throws Exception {
        String appkey = umengPushConfig.getAndroidAppkey();
        String appMasterSecret = umengPushConfig.getAndroidAppMasterSecret();
        String title = timingPushInfo.getTitle();
        String content = timingPushInfo.getContent();
        String tag = timingPushInfo.getTag();
        String pushTime = timingPushInfo.getPushTime();

        JSONObject filterJson = new JSONObject();
        JSONObject whereJson = new JSONObject();
        JSONArray tagArray = new JSONArray();
        JSONObject testTag = new JSONObject();
        testTag.put("tag", tag);
        tagArray.put(testTag);
        whereJson.put("and", tagArray);
        filterJson.put("where", whereJson);

        AndroidGroupcast groupcast = new AndroidGroupcast(appkey, appMasterSecret);
        groupcast.setFilter(filterJson);
        groupcast.setTicker(title);
        groupcast.setTitle(title);
        groupcast.setText(content);
        groupcast.goCustomAfterOpen("class_reminder");
        groupcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
        groupcast.setProductionMode();
        groupcast.setStartTime(pushTime);

        PushClient client = new PushClient();
        return client.send(groupcast);
    }

    /**
     * ios推送消息
     *
     * @param pushInfo
     * @return
     * @throws Exception
     */
    private boolean iosPushMessage(PushInfo pushInfo) throws Exception {
        String appkey = umengPushConfig.getIosAppkey();
        String appMasterSecret = umengPushConfig.getIosAppMasterSecret();
        String content = pushInfo.getContent();
        String deviceToken = pushInfo.getDeviceToken();

        IOSUnicast unicast = new IOSUnicast(appkey, appMasterSecret);
        unicast.setDeviceToken(deviceToken);
        unicast.setAlert(content);
        unicast.setBadge(1);
        unicast.setSound("default");
        unicast.setProductionMode();
        unicast.setCustomizedField("class_reminder", "1");

        PushClient client = new PushClient();
        return client.send(unicast);
    }

    /**
     * android立即推送消息
     *
     * @param pushInfo
     * @return
     * @throws Exception
     */
    private boolean androidPushMessage(PushInfo pushInfo) throws Exception {
        String appkey = umengPushConfig.getAndroidAppkey();
        String appMasterSecret = umengPushConfig.getAndroidAppMasterSecret();
        String title = pushInfo.getTitle();
        String content = pushInfo.getContent();
        String deviceToken = pushInfo.getDeviceToken();

        AndroidUnicast unicast = new AndroidUnicast(appkey, appMasterSecret);
        unicast.setDeviceToken(deviceToken);
        unicast.setTicker(title);    //必填 通知栏提示文字
        unicast.setTitle(title);     // 必填 通知标题
        unicast.setText(content);     // 必填 通知文字描述
        unicast.goCustomAfterOpen("class_reminder");
        unicast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
        unicast.setProductionMode();

        PushClient client = new PushClient();
        return client.send(unicast);
    }
}
