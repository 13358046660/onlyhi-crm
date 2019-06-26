package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 客户端app调用声网接口日志
 * @author shitongtong
 * Created by shitongtong on 2018/03/12
 */
public class AgoraCallLog extends BasePo {
    private Integer id;

    private String agoraCallLogUuid;

    /**
     * 课程uuid
     */
    private String courseUuid;

    /**
     * 声网用户uuid
     */
    private String agoraUuid;

    /**
     * 调用声网接口类型
     */
    private String agoraType;

    /**
     * 调用声网接口返回的code
     */
    private String agoraCode;

    /**
     * 设备类型（PC、IOS、ANDROID...）
     */
    private String deviceType;

    /**
     * 用户类型（student、teacher、patriarch...）
     */
    private String userType;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAgoraCallLogUuid() {
        return agoraCallLogUuid;
    }

    public void setAgoraCallLogUuid(String agoraCallLogUuid) {
        this.agoraCallLogUuid = agoraCallLogUuid == null ? null : agoraCallLogUuid.trim();
    }

    public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid == null ? null : courseUuid.trim();
    }

    public String getAgoraUuid() {
        return agoraUuid;
    }

    public void setAgoraUuid(String agoraUuid) {
        this.agoraUuid = agoraUuid == null ? null : agoraUuid.trim();
    }

    public String getAgoraType() {
        return agoraType;
    }

    public void setAgoraType(String agoraType) {
        this.agoraType = agoraType == null ? null : agoraType.trim();
    }

    public String getAgoraCode() {
        return agoraCode;
    }

    public void setAgoraCode(String agoraCode) {
        this.agoraCode = agoraCode == null ? null : agoraCode.trim();
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType == null ? null : deviceType.trim();
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType == null ? null : userType.trim();
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