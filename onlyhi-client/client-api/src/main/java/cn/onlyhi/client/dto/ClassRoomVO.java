package cn.onlyhi.client.dto;

import cn.onlyhi.client.po.ClassRoom;

/**
 * @Author wqz
 * <p>
 * Created by wqz on 2018/7/14.
 */
public class ClassRoomVO extends ClassRoom {
    private String startDate;
    private String endDate;
    private String courseDate;
    private long sysTime;
    private String channelKey;  //用于加入频道
    private String signalingKey;    //用于登录信令系统
    private String invalid_course;
    private String course_no_starat;
    private String course_has_end;
    private String userUuid;
    private String userName;
    private String userType;
    private String appId;
    private String appCertificate;
    private Integer agoraUid;
    /**课程状态：0：课程未完成，1：课程已完成，2：课程进行中*/
    private Integer classStatus;

    public Integer getClassStatus() {
        return classStatus;
    }

    public void setClassStatus(Integer classStatus) {
        this.classStatus = classStatus;
    }

    public Integer getAgoraUid() {
        return agoraUid;
    }

    public void setAgoraUid(Integer agoraUid) {
        this.agoraUid = agoraUid;
    }

    public String getAppCertificate() {
        return appCertificate;
    }

    public void setAppCertificate(String appCertificate) {
        this.appCertificate = appCertificate;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getInvalid_course() {
        return invalid_course;
    }

    public void setInvalid_course(String invalid_course) {
        this.invalid_course = invalid_course;
    }

    public String getCourse_no_starat() {
        return course_no_starat;
    }

    public void setCourse_no_starat(String course_no_starat) {
        this.course_no_starat = course_no_starat;
    }

    public String getCourse_has_end() {
        return course_has_end;
    }

    public void setCourse_has_end(String course_has_end) {
        this.course_has_end = course_has_end;
    }

    public long getSysTime() {
        return sysTime;
    }

    public void setSysTime(long sysTime) {
        this.sysTime = sysTime;
    }

    public String getChannelKey() {
        return channelKey;
    }

    public void setChannelKey(String channelKey) {
        this.channelKey = channelKey;
    }

    public String getSignalingKey() {
        return signalingKey;
    }

    public void setSignalingKey(String signalingKey) {
        this.signalingKey = signalingKey;
    }

    public String getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(String courseDate) {
        this.courseDate = courseDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "ClassRoomVO{" +
                "startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", courseDate='" + courseDate + '\'' +
                ", sysTime=" + sysTime +
                ", channelKey='" + channelKey + '\'' +
                ", signalingKey='" + signalingKey + '\'' +
                ", invalid_course='" + invalid_course + '\'' +
                ", course_no_starat='" + course_no_starat + '\'' +
                ", course_has_end='" + course_has_end + '\'' +
                ", userUuid='" + userUuid + '\'' +
                ", userName='" + userName + '\'' +
                ", userType='" + userType + '\'' +
                ", appId='" + appId + '\'' +
                ", appCertificate='" + appCertificate + '\'' +
                ", agoraUid=" + agoraUid +
                ", classStatus=" + classStatus +
                '}';
    }
}
