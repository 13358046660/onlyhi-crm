package cn.onlyhi.client.dto;

import java.io.Serializable;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/11/17.
 */
public class ClassRecordDBData implements Serializable {
    private static final long serialVersionUID = 7086072878820834505L;
    private int userid; //用户声网uid或录制id（首条或末条记录）
    private int status; //0：退出房间；1：进入房间
    private long timestamp; //进入或退出房间时间戳

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
