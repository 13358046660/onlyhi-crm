package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 教师空闲时间表
 * @author shitongtong
 * Created by shitongtong on 2018/01/18
 */
public class TeacherFreetime extends BasePo {
    private Integer id;

    private String teacherFreetimeUuid;

    /**
     * 教师uuid
     */
    private String teacherUuid;

    /**
     * 空闲的月份:yyyy-MM
     */
    private String freetimeMonth;

    /**
     * 月的第几周
     */
    private Integer weekOfMonth;

    /**
     * 空闲的日期:yyyy-MM-dd
     */
    private String freetimeDate;

    /**
     * 空闲开始时间: HH:mm
     */
    private String startTime;

    /**
     * 空闲结束时间: HH:mm
     */
    private String endTime;

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

    public String getTeacherFreetimeUuid() {
        return teacherFreetimeUuid;
    }

    public void setTeacherFreetimeUuid(String teacherFreetimeUuid) {
        this.teacherFreetimeUuid = teacherFreetimeUuid == null ? null : teacherFreetimeUuid.trim();
    }

    public String getTeacherUuid() {
        return teacherUuid;
    }

    public void setTeacherUuid(String teacherUuid) {
        this.teacherUuid = teacherUuid == null ? null : teacherUuid.trim();
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

    public String getFreetimeDate() {
        return freetimeDate;
    }

    public void setFreetimeDate(String freetimeDate) {
        this.freetimeDate = freetimeDate == null ? null : freetimeDate.trim();
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime == null ? null : startTime.trim();
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime == null ? null : endTime.trim();
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