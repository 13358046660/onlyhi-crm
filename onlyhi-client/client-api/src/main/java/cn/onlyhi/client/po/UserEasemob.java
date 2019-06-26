package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 用户环信表
 * @author shitongtong
 * Created by shitongtong on 2017/12/12
 */
public class UserEasemob extends BasePo {
    /**
     * id
     */
    private Integer id;

    private String userEasemobUuid;

    /**
     * 用户uuid
     */
    private String userUuid;

    /**
     * 用户类型：STUDENT、TEACHER、PATRIARCH（家长）、CC、CR、MONITOR（教学监课）、QC等等
     */
    private String userType;

    /**
     * 环信IM用户uuid
     */
    private String easemobUuid;

    /**
     * 环信IM用户名称
     */
    private String easemobUsername;

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

    public String getUserEasemobUuid() {
        return userEasemobUuid;
    }

    public void setUserEasemobUuid(String userEasemobUuid) {
        this.userEasemobUuid = userEasemobUuid == null ? null : userEasemobUuid.trim();
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid == null ? null : userUuid.trim();
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType == null ? null : userType.trim();
    }

    public String getEasemobUuid() {
        return easemobUuid;
    }

    public void setEasemobUuid(String easemobUuid) {
        this.easemobUuid = easemobUuid == null ? null : easemobUuid.trim();
    }

    public String getEasemobUsername() {
        return easemobUsername;
    }

    public void setEasemobUsername(String easemobUsername) {
        this.easemobUsername = easemobUsername == null ? null : easemobUsername.trim();
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