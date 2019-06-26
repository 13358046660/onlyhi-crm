package cn.onlyhi.file.vo;


public class ClassRoomVo {
    private String commChannelId;
    private String signallingChannelId;
    private int recordId;
    private long sysTime;
    private String channelKey;  //用于加入频道
    private String signalingKey;    //用于登录信令系统

    public String getCommChannelId() {
        return commChannelId;
    }

    public void setCommChannelId(String commChannelId) {
        this.commChannelId = commChannelId;
    }

    public String getSignallingChannelId() {
        return signallingChannelId;
    }

    public void setSignallingChannelId(String signallingChannelId) {
        this.signallingChannelId = signallingChannelId;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
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

    @Override
    public String toString() {
        return "ClassRoomVo{" +
                "commChannelId='" + commChannelId + '\'' +
                ", signallingChannelId='" + signallingChannelId + '\'' +
                ", recordId=" + recordId +
                ", sysTime=" + sysTime +
                ", channelKey='" + channelKey + '\'' +
                ", signalingKey='" + signalingKey + '\'' +
                '}';
    }
}