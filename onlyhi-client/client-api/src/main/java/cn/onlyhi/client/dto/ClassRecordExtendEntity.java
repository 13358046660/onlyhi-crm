package cn.onlyhi.client.dto;

import cn.onlyhi.client.po.ClassRecord;

/**
 * @Author wqz
 * <p> 老师端回放列表，当前课程老师与进出房间记录
 * Created by wqz on 2018/10/26
 */
public class ClassRecordExtendEntity extends ClassRecord {
    /**
     * 房间id
     */
    private String roomUuid;
    /**
     * 共同有效时长(分钟)
     */
    private Integer voiceDuration;

    public Integer getVoiceDuration() {
        return voiceDuration;
    }

    public void setVoiceDuration(Integer voiceDuration) {
        this.voiceDuration = voiceDuration;
    }

    public String getRoomUuid() {
        return roomUuid;
    }

    public void setRoomUuid(String roomUuid) {
        this.roomUuid = roomUuid;
    }

    @Override
    public String toString() {
        return "ClassRecordExtendEntity{" +
                "roomUuid='" + roomUuid + '\'' +
                ", voiceDuration=" + voiceDuration +
                '}';
    }

}
