package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 技术协助表
 * @author shitongtong
 * Created by shitongtong on 2018/01/02
 */
public class TechnicalAssistance extends BasePo {
    private Integer id;

    private String technicalAssistanceUuid;

    /**
     * 技术支持uuid
     */
    private String assistanceUuid;

    /**
     * 需要协助的用户uuid
     */
    private String userUuid;

    /**
     * 需要协助的用户姓名
     */
    private String userName;

    /**
     * 用户类型：student、teacher、patriarch、员工（cc、cr、ts...）
     */
    private String userType;

    /**
     * 设备问题
     */
    private String issue;

    /**
     * 需要协助的用户id（远程软件识别用id）
     */
    private String userAssistanceId;

    /**
     * 需要协助的用户密码（远程软件识别用密码）
     */
    private String userAssistancePwd;

    /**
     * 协助状态 1:请求协助,2:撤销协助,3:完成协助
     */
    private Integer assistanceStatus;

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

    public String getTechnicalAssistanceUuid() {
        return technicalAssistanceUuid;
    }

    public void setTechnicalAssistanceUuid(String technicalAssistanceUuid) {
        this.technicalAssistanceUuid = technicalAssistanceUuid == null ? null : technicalAssistanceUuid.trim();
    }

    public String getAssistanceUuid() {
        return assistanceUuid;
    }

    public void setAssistanceUuid(String assistanceUuid) {
        this.assistanceUuid = assistanceUuid == null ? null : assistanceUuid.trim();
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid == null ? null : userUuid.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType == null ? null : userType.trim();
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue == null ? null : issue.trim();
    }

    public String getUserAssistanceId() {
        return userAssistanceId;
    }

    public void setUserAssistanceId(String userAssistanceId) {
        this.userAssistanceId = userAssistanceId == null ? null : userAssistanceId.trim();
    }

    public String getUserAssistancePwd() {
        return userAssistancePwd;
    }

    public void setUserAssistancePwd(String userAssistancePwd) {
        this.userAssistancePwd = userAssistancePwd == null ? null : userAssistancePwd.trim();
    }

    public Integer getAssistanceStatus() {
        return assistanceStatus;
    }

    public void setAssistanceStatus(Integer assistanceStatus) {
        this.assistanceStatus = assistanceStatus;
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