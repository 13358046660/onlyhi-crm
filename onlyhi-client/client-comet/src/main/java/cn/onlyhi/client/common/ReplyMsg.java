package cn.onlyhi.client.common;

import java.io.Serializable;

/**
 * 相应类型body对像
 *
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/4.
 */
public class ReplyMsg implements Serializable {
    private static final long serialVersionUID = 1L;

    private int reply;  //0:建立连接    1:ping    2:login     3:t     4:数据格式不对异常
    private String deviceId;
    private String channelId;
    /**返回前端手机号*/
    private String phone;
    /**返回前端token*/
    private String token;
    /**返回前端userMark（手机号+"STUDENT或TEACHER或员工或家长'）*/
    private String userMark;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserMark() {
        return userMark;
    }

    public void setUserMark(String userMark) {
        this.userMark = userMark;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public ReplyMsg(int reply) {
        this.reply = reply;
    }

    public int getReply() {
        return reply;
    }

    public void setReply(int reply) {
        this.reply = reply;
    }
}
