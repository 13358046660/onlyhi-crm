package cn.onlyhi.client.po;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 上课记录表
 * @author shitongtong
 * Created by shitongtong on 2017/11/17
 */
public class ClassRecord extends BasePo {
    /**
     * id
     */
    private Integer id;

    /**
     * 上课记录uuid
     */
    private String classRecordUuid;

    /**
     * 上课房间uuid
     */
    private String classRoomUuid;

    /**
     * 用户uuid
     */
    private String userUuid;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户类型：STUDENT、TEACHER、PATRIARCH（家长）、CC、CR、MONITOR（监课）
     */
    private String userType;

    /**
     * 进出房间时间
     */
    private Long recordTime;

    /**
     * 0:退出房间; 1:进入房间
     */
    private Integer recordType;

    /**
     * 消耗的流量时长，以分钟为单位
     */
    private BigDecimal classTime;

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

    public String getClassRecordUuid() {
        return classRecordUuid;
    }

    public void setClassRecordUuid(String classRecordUuid) {
        this.classRecordUuid = classRecordUuid == null ? null : classRecordUuid.trim();
    }

    public String getClassRoomUuid() {
        return classRoomUuid;
    }

    public void setClassRoomUuid(String classRoomUuid) {
        this.classRoomUuid = classRoomUuid == null ? null : classRoomUuid.trim();
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

    public Long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Long recordTime) {
        this.recordTime = recordTime;
    }

    public Integer getRecordType() {
        return recordType;
    }

    public void setRecordType(Integer recordType) {
        this.recordType = recordType;
    }

    public BigDecimal getClassTime() {
        return classTime;
    }

    public void setClassTime(BigDecimal classTime) {
        this.classTime = classTime;
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