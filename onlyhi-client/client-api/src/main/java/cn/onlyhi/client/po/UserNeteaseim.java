package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 用户网易云信IM表
 * @author shitongtong
 * Created by shitongtong on 2018/01/03
 */
public class UserNeteaseim extends BasePo {
    private Integer id;

    private String userNeteaseimUuid;

    /**
     * 用户uuid
     */
    private String userUuid;

    /**
     * 用户类型：student、teacher、patriarch、员工（cc、cr、ts...）
     */
    private String userType;

    /**
     * 网易云信id
     */
    private String neteaseAccid;

    /**
     * 网易云信昵称
     */
    private String neteaseName;

    /**
     * 网易云信token
     */
    private String neteaseToken;

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

    public String getUserNeteaseimUuid() {
        return userNeteaseimUuid;
    }

    public void setUserNeteaseimUuid(String userNeteaseimUuid) {
        this.userNeteaseimUuid = userNeteaseimUuid == null ? null : userNeteaseimUuid.trim();
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

    public String getNeteaseAccid() {
        return neteaseAccid;
    }

    public void setNeteaseAccid(String neteaseAccid) {
        this.neteaseAccid = neteaseAccid == null ? null : neteaseAccid.trim();
    }

    public String getNeteaseName() {
        return neteaseName;
    }

    public void setNeteaseName(String neteaseName) {
        this.neteaseName = neteaseName == null ? null : neteaseName.trim();
    }

    public String getNeteaseToken() {
        return neteaseToken;
    }

    public void setNeteaseToken(String neteaseToken) {
        this.neteaseToken = neteaseToken == null ? null : neteaseToken.trim();
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