package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 老师的空闲时间
 * @author shitongtong
 * Created by shitongtong on 2018/01/18
 */
public class TcTeacherDate extends BasePo {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 老师的UUID
     */
    private String teacherUuid;

    /**
     * 日期
     */
    private String tcDate;

    /**
     * 时间
     */
    private String tcTime;

    /**
     * 非空闲原因 1 该时间段已经排课
     */
    private Integer notIdle;

    /**
     * 是否可用 0 可用
     */
    private Integer enabled;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 创建人
     */
    private String createUserId;

    /**
     * 更新时间
     */
    private Date updateDate;

    /**
     * 更新人
     */
    private String updateUserId;

    /**
     * 版本
     */
    private Long version;

    /**
     * 空闲的月份:yyyy-MM
     */
    private String freetimeMonth;

    /**
     * 月的第几周
     */
    private Integer weekOfMonth;

    /**
     * 空闲结束时间: HH:mm
     */
    private String endTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTeacherUuid() {
        return teacherUuid;
    }

    public void setTeacherUuid(String teacherUuid) {
        this.teacherUuid = teacherUuid == null ? null : teacherUuid.trim();
    }

    public String getTcDate() {
        return tcDate;
    }

    public void setTcDate(String tcDate) {
        this.tcDate = tcDate == null ? null : tcDate.trim();
    }

    public String getTcTime() {
        return tcTime;
    }

    public void setTcTime(String tcTime) {
        this.tcTime = tcTime == null ? null : tcTime.trim();
    }

    public Integer getNotIdle() {
        return notIdle;
    }

    public void setNotIdle(Integer notIdle) {
        this.notIdle = notIdle;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId == null ? null : createUserId.trim();
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId == null ? null : updateUserId.trim();
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getFreetimeMonth() {
        return freetimeMonth;
    }

    public void setFreetimeMonth(String freetimeMonth) {
        this.freetimeMonth = freetimeMonth == null ? null : freetimeMonth.trim();
    }

    public Integer getWeekOfMonth() {
        return weekOfMonth;
    }

    public void setWeekOfMonth(Integer weekOfMonth) {
        this.weekOfMonth = weekOfMonth;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime == null ? null : endTime.trim();
    }
}