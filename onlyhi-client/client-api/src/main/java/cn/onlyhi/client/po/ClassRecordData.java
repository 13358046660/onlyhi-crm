package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 上课录制数据表
 * @author wangqianzhi
 * Created by wangqianzhi on 2017/12/19
 */
public class ClassRecordData extends BasePo {
    private Integer id;

    private String classRecordDataUuid;

    /**
     * 上课房间uuid
     */
    private String classRoomUuid;

    /**
     * 音频文件url
     */
    private String voiceUrl;

    /**
     * flac格式音频文件url
     */
    private String flacVoiceUrl;

    /**
     * 音频时长 单位毫秒
     */
    private Integer voiceDuration;

    /**
     * 录制角色：0:学生和老师、3：学生和cc、4：学生和cr
     */
    private Integer recordRole;

    /**
     * 轨迹文件路径
     */
    private String trackPath;

    /**
     * 轨迹数据url
     */
    private String trackUrl;

    /**
     * aac文件路径
     */
    private String aacVoicePath;

    /**
     * 通用状态 0:删除,1:正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人uuid
     */
    private String createUid;

    /**
     * 更新人uuid
     */
    private String updateUid;

    /**
     * 通用备注
     */
    private String remark;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * mp4视频文件下载路径
     */
    private String mp4VideoUrl;

    /**
     * mp4视频时长
     */
    private Integer videoDuration;

    public Integer getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(Integer videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getMp4VideoUrl() {
        return mp4VideoUrl;
    }

    public void setMp4VideoUrl(String mp4VideoUrl) {
        this.mp4VideoUrl = mp4VideoUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClassRecordDataUuid() {
        return classRecordDataUuid;
    }

    public void setClassRecordDataUuid(String classRecordDataUuid) {
        this.classRecordDataUuid = classRecordDataUuid == null ? null : classRecordDataUuid.trim();
    }

    public String getClassRoomUuid() {
        return classRoomUuid;
    }

    public void setClassRoomUuid(String classRoomUuid) {
        this.classRoomUuid = classRoomUuid == null ? null : classRoomUuid.trim();
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl == null ? null : voiceUrl.trim();
    }

    public String getFlacVoiceUrl() {
        return flacVoiceUrl;
    }

    public void setFlacVoiceUrl(String flacVoiceUrl) {
        this.flacVoiceUrl = flacVoiceUrl == null ? null : flacVoiceUrl.trim();
    }

    public Integer getVoiceDuration() {
        return voiceDuration;
    }

    public void setVoiceDuration(Integer voiceDuration) {
        this.voiceDuration = voiceDuration;
    }

    public Integer getRecordRole() {
        return recordRole;
    }

    public void setRecordRole(Integer recordRole) {
        this.recordRole = recordRole;
    }

    public String getTrackPath() {
        return trackPath;
    }

    public void setTrackPath(String trackPath) {
        this.trackPath = trackPath == null ? null : trackPath.trim();
    }

    public String getTrackUrl() {
        return trackUrl;
    }

    public void setTrackUrl(String trackUrl) {
        this.trackUrl = trackUrl == null ? null : trackUrl.trim();
    }

    public String getAacVoicePath() {
        return aacVoicePath;
    }

    public void setAacVoicePath(String aacVoicePath) {
        this.aacVoicePath = aacVoicePath == null ? null : aacVoicePath.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateUid() {
        return createUid;
    }

    public void setCreateUid(String createUid) {
        this.createUid = createUid == null ? null : createUid.trim();
    }

    public String getUpdateUid() {
        return updateUid;
    }

    public void setUpdateUid(String updateUid) {
        this.updateUid = updateUid == null ? null : updateUid.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}