package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 家长表
 * @author JYDZ3010164
 * Created by JYDZ3010164 on 2018/05/07
 */
public class Patriarch extends BasePo {
    /**
     * id
     */
    private Integer id;

    private String patriarchUuid;

    /**
     * 家长手机号
     */
    private String patriarchPhone;

    /**
     * 账号登录密码
     */
    private String patriarchPassword;

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
     * 更新版本
     */
    private Integer version;

    /**
     * 家长姓名
     */
    private String patriarchName;

    /**
     * 孩子年级
     */
    private String childrenGrade;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPatriarchUuid() {
        return patriarchUuid;
    }

    public void setPatriarchUuid(String patriarchUuid) {
        this.patriarchUuid = patriarchUuid == null ? null : patriarchUuid.trim();
    }

    public String getPatriarchPhone() {
        return patriarchPhone;
    }

    public void setPatriarchPhone(String patriarchPhone) {
        this.patriarchPhone = patriarchPhone == null ? null : patriarchPhone.trim();
    }

    public String getPatriarchPassword() {
        return patriarchPassword;
    }

    public void setPatriarchPassword(String patriarchPassword) {
        this.patriarchPassword = patriarchPassword == null ? null : patriarchPassword.trim();
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getPatriarchName() {
        return patriarchName;
    }

    public void setPatriarchName(String patriarchName) {
        this.patriarchName = patriarchName == null ? null : patriarchName.trim();
    }

    public String getChildrenGrade() {
        return childrenGrade;
    }

    public void setChildrenGrade(String childrenGrade) {
        this.childrenGrade = childrenGrade == null ? null : childrenGrade.trim();
    }
}