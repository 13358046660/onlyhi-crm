package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 教师空闲时间模板表
 * @author shitongtong
 * Created by shitongtong on 2018/01/18
 */
public class TeacherFreetimetemplate extends BasePo {
    private Integer id;

    private String teacherFreetimetemplateUuid;

    /**
     * 教师uuid
     */
    private String teacherUuid;

    /**
     * 月的第几周
     */
    private Integer weekOfMonth;

    /**
     * 星期：1:星期日,2:星期一,3:星期二,4:星期三,5:星期四,6:星期五,7:星期六
     */
    private Integer week;

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

    public String getTeacherFreetimetemplateUuid() {
        return teacherFreetimetemplateUuid;
    }

    public void setTeacherFreetimetemplateUuid(String teacherFreetimetemplateUuid) {
        this.teacherFreetimetemplateUuid = teacherFreetimetemplateUuid == null ? null : teacherFreetimetemplateUuid.trim();
    }

    public String getTeacherUuid() {
        return teacherUuid;
    }

    public void setTeacherUuid(String teacherUuid) {
        this.teacherUuid = teacherUuid == null ? null : teacherUuid.trim();
    }

    public Integer getWeekOfMonth() {
        return weekOfMonth;
    }

    public void setWeekOfMonth(Integer weekOfMonth) {
        this.weekOfMonth = weekOfMonth;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
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