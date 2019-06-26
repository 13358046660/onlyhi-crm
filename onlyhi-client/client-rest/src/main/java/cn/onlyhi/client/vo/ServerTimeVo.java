package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/11.
 */
public class ServerTimeVo {
    private long serverTime;

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    @Override
    public String toString() {
        return "ServerTimeVo{" +
                "serverTime=" + serverTime +
                '}';
    }
}
