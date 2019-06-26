package cn.onlyhi.client.push;

/**
 * 立即推送信息封装
 *
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/7.
 */
public class PushInfo {
    private String leadsUuid;
    private String title;
    private String content;
    private String deviceToken;
    private String tag;
    private String pushTime;    //推送时间   格式:"yyyy-MM-dd hh:mm:ss"

    public String getLeadsUuid() {
        return leadsUuid;
    }

    public void setLeadsUuid(String leadsUuid) {
        this.leadsUuid = leadsUuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPushTime() {
        return pushTime;
    }

    public void setPushTime(String pushTime) {
        this.pushTime = pushTime;
    }
}
