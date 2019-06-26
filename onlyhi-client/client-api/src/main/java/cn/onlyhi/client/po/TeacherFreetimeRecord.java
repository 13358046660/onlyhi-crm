package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 教师空闲时间提交记录表
 * @author shitongtong
 * Created by shitongtong on 2018/04/25
 */
public class TeacherFreetimeRecord extends BasePo {
    private Integer id;

    /**
     * 教师uuid
     */
    private String teacherUuid;

    /**
     * 空闲的月份:yyyy-MM
     */
    private String freetimeMonth;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}