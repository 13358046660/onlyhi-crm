package cn.onlyhi.client.common;

/**
 * 登录类型消息：
 *
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/4.
 */
public class LoginMsg extends BaseMsg {
    private String userMark;    //登录用户名userName(phone)+userType(teacher或student)
    private String token;
    private String channelId;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public LoginMsg() {
        super();
        setType(2);
    }

    public String getUserMark() {
        return userMark;
    }

    public void setUserMark(String userMark) {
        this.userMark = userMark;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
