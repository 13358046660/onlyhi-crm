package cn.onlyhi.file.request;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2018/12/6.
 */
public class SyncParamRequest {
    private String courseUuid;
    private String userUuid;
    private String userType;
    private String userName;
    private Integer agoraUid;
    private Long currentTimeMillis;
    public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getAgoraUid() {
        return agoraUid;
    }

    public void setAgoraUid(Integer agoraUid) {
        this.agoraUid = agoraUid;
    }

    public Long getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public void setCurrentTimeMillis(Long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }
}
