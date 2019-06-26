package cn.onlyhi.client.po;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 上课房间表
 * @author shitongtong
 * Created by shitongtong on 2017/12/06
 */
public class ClassRoom extends BasePo {
    /**
     * id
     */
    private Integer id;

    /**
     * 上课房间uuid
     */
    private String classRoomUuid;

    /**
     * 课程uuid
     */
    private String courseUuid;

    /**
     * 通信频道Id
     */
    private String commChannelId;

    /**
     * 信令频道Id
     */
    private String signallingChannelId;

    /**
     * 录制id
     */
    private Integer recordId;

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
     * 轨迹文件url
     */
    private String trackUrl;

    /**
     * 最初进入房间时间
     */
    private Long enterRoomTime;

    /**
     * 最后退出房间时间
     */
    private Long outRoomTime;

    /**
     * 教师延长上课时间，单位：min
     */
    private Integer overtime;

    /**
     * 学生端消耗的流量总时长，以分钟为单位
     */
    private BigDecimal studentClassTime;

    /**
     * 老师端消耗的流量总时长，以分钟为单位
     */
    private BigDecimal teacherClassTime;

    /**
     * 0:未统计; 1:不必统计; 2:已统计
     */
    private Integer statisticsStatus;

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
     * mp4视频总时长
     */
    private Integer videoDuration;

    /**
     * 阿里云空间存储m4a下载路径对应的key
     */
    private String m4aOssKey;

    /**
     * 阿里云空间存储flac文件的下载路径对应的key
     */
    private String flacOssKey;

    /**
     * 阿里云空间存储轨迹文件路径对应的key
     */
    public String getMp4OssKey() {
        return mp4OssKey;
    }

    public void setMp4OssKey(String mp4OssKey) {
        this.mp4OssKey = mp4OssKey;
    }

    /**
     * 阿里云空间存储mp4文件路径对应的key
     */
    private String mp4OssKey;

    private String trackOssKey;

    public String getM4aOssKey() {
        return m4aOssKey;
    }

    public void setM4aOssKey(String m4aOssKey) {
        this.m4aOssKey = m4aOssKey;
    }

    public String getFlacOssKey() {
        return flacOssKey;
    }

    public void setFlacOssKey(String flacOssKey) {
        this.flacOssKey = flacOssKey;
    }

    public String getTrackOssKey() {
        return trackOssKey;
    }

    public void setTrackOssKey(String trackOssKey) {
        this.trackOssKey = trackOssKey;
    }

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

    public String getClassRoomUuid() {
        return classRoomUuid;
    }

    public void setClassRoomUuid(String classRoomUuid) {
        this.classRoomUuid = classRoomUuid == null ? null : classRoomUuid.trim();
    }

    public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid == null ? null : courseUuid.trim();
    }

    public String getCommChannelId() {
        return commChannelId;
    }

    public void setCommChannelId(String commChannelId) {
        this.commChannelId = commChannelId == null ? null : commChannelId.trim();
    }

    public String getSignallingChannelId() {
        return signallingChannelId;
    }

    public void setSignallingChannelId(String signallingChannelId) {
        this.signallingChannelId = signallingChannelId == null ? null : signallingChannelId.trim();
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
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

    public String getTrackUrl() {
        return trackUrl;
    }

    public void setTrackUrl(String trackUrl) {
        this.trackUrl = trackUrl == null ? null : trackUrl.trim();
    }

    public Long getEnterRoomTime() {
        return enterRoomTime;
    }

    public void setEnterRoomTime(Long enterRoomTime) {
        this.enterRoomTime = enterRoomTime;
    }

    public Long getOutRoomTime() {
        return outRoomTime;
    }

    public void setOutRoomTime(Long outRoomTime) {
        this.outRoomTime = outRoomTime;
    }

    public Integer getOvertime() {
        return overtime;
    }

    public void setOvertime(Integer overtime) {
        this.overtime = overtime;
    }

    public BigDecimal getStudentClassTime() {
        return studentClassTime;
    }

    public void setStudentClassTime(BigDecimal studentClassTime) {
        this.studentClassTime = studentClassTime;
    }

    public BigDecimal getTeacherClassTime() {
        return teacherClassTime;
    }

    public void setTeacherClassTime(BigDecimal teacherClassTime) {
        this.teacherClassTime = teacherClassTime;
    }

    public Integer getStatisticsStatus() {
        return statisticsStatus;
    }

    public void setStatisticsStatus(Integer statisticsStatus) {
        this.statisticsStatus = statisticsStatus;
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