package cn.onlyhi.client.dto;


/**
 * @Author wqz
 * <p>
 * Created by wqz on 2019/03/07
 */
public class LoginUserCachePhp extends BaseDto {
    private String token;
    private String userUuid;
    private String userName;
    private String userType;
    private String phone;
    private String deviceType;
    private String deviceId;
    /**
     * 一次加密后的密码
     */
    private String password;
    /**
     * 是否注册IM标记,false:否，true：是
     */
    private boolean registerIMFlag;
    /**
     * 声网用户Id
     */
    private int agoraUid;
    /**
     * 登录名
     */
    private String loginName;
    /**
     * 是否首次登录（是否使用的是默认密码）    false:不是，true:是
     */
    private boolean isFirst;
    /**
     * 用于云信注册时传入的token（来源登录后保存在redis的缓存信息（当前用户的电话号码））
     */
    private String useNeteaseimToken;

    public boolean isRegisterIMFlag() {
        return registerIMFlag;
    }

    public void setRegisterIMFlag(boolean registerIMFlag) {
        this.registerIMFlag = registerIMFlag;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public String getUseNeteaseimToken() {
        return useNeteaseimToken;
    }

    public void setUseNeteaseimToken(String useNeteaseimToken) {
        this.useNeteaseimToken = useNeteaseimToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserUuid() {
        return userUuid;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAgoraUid() {
        return agoraUid;
    }

    public void setAgoraUid(int agoraUid) {
        this.agoraUid = agoraUid;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String toString() {
        return "LoginUserCachePhp{" +
                "token='" + token + '\'' +
                ", userUuid='" + userUuid + '\'' +
                ", userName='" + userName + '\'' +
                ", userType='" + userType + '\'' +
                ", phone='" + phone + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", password='" + password + '\'' +
                ", registerIMFlag=" + registerIMFlag +
                ", agoraUid=" + agoraUid +
                ", loginName='" + loginName + '\'' +
                ", isFirst=" + isFirst +
                ", useNeteaseimToken='" + useNeteaseimToken + '\'' +
                '}';
    }

}
