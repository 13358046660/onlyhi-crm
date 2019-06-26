package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 上课评价表
 * @author wangqianzhi
 * Created by wangqianzhi on 2017/09/08
 */
public class ClassAppraise extends BasePo {
    private Integer id;

    private String classAppraiseUuid;

    /**
     * cp_course_uuid
     */
    private String courseUuid;

    /**
     * 评价人uuid
     */
    private String appraiserUuid;

    /**
     * 评价人身份 1:学生
     */
    private Integer appraiserIdentity;

    /**
     * 星级
     */
    private Integer star;

    /**
     * 选择的评价星级uuid,多个以,分割
     */
    private String classAppraiseStarUuids;

    /**
     * 状态:0:无效，1：正常
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClassAppraiseUuid() {
        return classAppraiseUuid;
    }

    public void setClassAppraiseUuid(String classAppraiseUuid) {
        this.classAppraiseUuid = classAppraiseUuid == null ? null : classAppraiseUuid.trim();
    }

    public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid == null ? null : courseUuid.trim();
    }

    public String getAppraiserUuid() {
        return appraiserUuid;
    }

    public void setAppraiserUuid(String appraiserUuid) {
        this.appraiserUuid = appraiserUuid == null ? null : appraiserUuid.trim();
    }

    public Integer getAppraiserIdentity() {
        return appraiserIdentity;
    }

    public void setAppraiserIdentity(Integer appraiserIdentity) {
        this.appraiserIdentity = appraiserIdentity;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public String getClassAppraiseStarUuids() {
        return classAppraiseStarUuids;
    }

    public void setClassAppraiseStarUuids(String classAppraiseStarUuids) {
        this.classAppraiseStarUuids = classAppraiseStarUuids == null ? null : classAppraiseStarUuids.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
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
}