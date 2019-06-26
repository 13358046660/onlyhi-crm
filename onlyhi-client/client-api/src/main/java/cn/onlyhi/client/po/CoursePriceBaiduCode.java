package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 课时包对应的百度分期编码表
 * @author shitongtong
 * Created by shitongtong on 2017/08/15
 */
public class CoursePriceBaiduCode extends BasePo {
    private Integer id;

    private String coursePriceBaiduCodeUuid;

    /**
     * 课时包类型
     */
    private String type;

    /**
     * 课时包对应的百度分期编码
     */
    private String baiduCode;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCoursePriceBaiduCodeUuid() {
        return coursePriceBaiduCodeUuid;
    }

    public void setCoursePriceBaiduCodeUuid(String coursePriceBaiduCodeUuid) {
        this.coursePriceBaiduCodeUuid = coursePriceBaiduCodeUuid == null ? null : coursePriceBaiduCodeUuid.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getBaiduCode() {
        return baiduCode;
    }

    public void setBaiduCode(String baiduCode) {
        this.baiduCode = baiduCode == null ? null : baiduCode.trim();
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
}