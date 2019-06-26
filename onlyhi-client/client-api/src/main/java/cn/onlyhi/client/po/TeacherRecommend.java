package cn.onlyhi.client.po;

import java.util.Date;

/**
 * app名师推荐表
 * @author shitongtong
 * Created by shitongtong on 2017/08/15
 */
public class TeacherRecommend extends BasePo {
    private Integer id;

    private String teacherRecommendUuid;

    /**
     * 教师姓名
     */
    private String name;

    /**
     * 教师图片
     */
    private String image;

    /**
     * 教学年龄
     */
    private String teachingAge;

    /**
     * 带毕业班
     */
    private String graduateClass;

    /**
     * 平均提分
     */
    private String improveScore;

    /**
     * 教师介绍
     */
    private String introduction;

    /**
     * 设备类型：1:手机,2:平板
     */
    private Integer deviceType;

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

    public String getTeacherRecommendUuid() {
        return teacherRecommendUuid;
    }

    public void setTeacherRecommendUuid(String teacherRecommendUuid) {
        this.teacherRecommendUuid = teacherRecommendUuid == null ? null : teacherRecommendUuid.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image == null ? null : image.trim();
    }

    public String getTeachingAge() {
        return teachingAge;
    }

    public void setTeachingAge(String teachingAge) {
        this.teachingAge = teachingAge == null ? null : teachingAge.trim();
    }

    public String getGraduateClass() {
        return graduateClass;
    }

    public void setGraduateClass(String graduateClass) {
        this.graduateClass = graduateClass == null ? null : graduateClass.trim();
    }

    public String getImproveScore() {
        return improveScore;
    }

    public void setImproveScore(String improveScore) {
        this.improveScore = improveScore == null ? null : improveScore.trim();
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction == null ? null : introduction.trim();
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
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