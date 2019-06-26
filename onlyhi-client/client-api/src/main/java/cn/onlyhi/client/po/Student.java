package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 新名单列表
 * @author shitongtong
 * Created by shitongtong on 2017/08/15
 */
public class Student extends BasePo {
    private Long id;

    /**
     * 学号
     */
    private String stuNo;

    /**
     * 对应的leadsUuid
     */
    private String leadUuid;

    /**
     * 老师的uuid
     */
    private String teacherUuid;

    /**
     * 第一节正式课uuid
     */
    private String firstCourseUuid;

    /**
     * cc的UUid
     */
    private String ccUuid;

    /**
     * cr的uuid
     */
    private String crUuid;

    /**
     * 学生名称
     */
    private String name;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 年级
     */
    private String grade;

    /**
     * 试听科目
     */
    private String subject;

    /**
     * 是否被移交0：未被移交1：已被移交
     */
    private Boolean isHandout;

    /**
     * 是否是转介绍0：否1是
     */
    private Boolean isIntroduced;

    /**
     * 转介绍人信息 姓名电话等
     */
    private String introducer;

    /**
     * 是否已安排第一节正式课0：未安排1：已安排
     */
    private Boolean isFirstCourse;

    /**
     * 移交备注
     */
    private String handoutDes;

    /**
     * 是否被分配0：未被分配1：已分配
     */
    private Boolean isDistribution;

    /**
     * 是否绑定天润账号0：否1：是
     */
    private Boolean isBound;

    /**
     * 状态0:禁用1：启用
     */
    private Boolean status;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 修改时间
     */
    private Date updateDate;

    /**
     * 是否是离职人员名单类型0:否1：是
     */
    private Boolean isDimissionType;

    private Integer sex;

    private Integer age;

    private String examArea;

    /**
     * 标记属性0:暂未被标记1：本月可维护2：本月可续费3:常规维护4：本月重点维护
     */
    private Byte keyPoint;

    private String inActivity;

    /**
     * 学生是否结课0：否1：是
     */
    private Integer isClassEnd;

    private Date handoutTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStuNo() {
        return stuNo;
    }

    public void setStuNo(String stuNo) {
        this.stuNo = stuNo == null ? null : stuNo.trim();
    }

    public String getLeadUuid() {
        return leadUuid;
    }

    public void setLeadUuid(String leadUuid) {
        this.leadUuid = leadUuid == null ? null : leadUuid.trim();
    }

    public String getTeacherUuid() {
        return teacherUuid;
    }

    public void setTeacherUuid(String teacherUuid) {
        this.teacherUuid = teacherUuid == null ? null : teacherUuid.trim();
    }

    public String getFirstCourseUuid() {
        return firstCourseUuid;
    }

    public void setFirstCourseUuid(String firstCourseUuid) {
        this.firstCourseUuid = firstCourseUuid == null ? null : firstCourseUuid.trim();
    }

    public String getCcUuid() {
        return ccUuid;
    }

    public void setCcUuid(String ccUuid) {
        this.ccUuid = ccUuid == null ? null : ccUuid.trim();
    }

    public String getCrUuid() {
        return crUuid;
    }

    public void setCrUuid(String crUuid) {
        this.crUuid = crUuid == null ? null : crUuid.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade == null ? null : grade.trim();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject == null ? null : subject.trim();
    }

    public Boolean getIsHandout() {
        return isHandout;
    }

    public void setIsHandout(Boolean isHandout) {
        this.isHandout = isHandout;
    }

    public Boolean getIsIntroduced() {
        return isIntroduced;
    }

    public void setIsIntroduced(Boolean isIntroduced) {
        this.isIntroduced = isIntroduced;
    }

    public String getIntroducer() {
        return introducer;
    }

    public void setIntroducer(String introducer) {
        this.introducer = introducer == null ? null : introducer.trim();
    }

    public Boolean getIsFirstCourse() {
        return isFirstCourse;
    }

    public void setIsFirstCourse(Boolean isFirstCourse) {
        this.isFirstCourse = isFirstCourse;
    }

    public String getHandoutDes() {
        return handoutDes;
    }

    public void setHandoutDes(String handoutDes) {
        this.handoutDes = handoutDes == null ? null : handoutDes.trim();
    }

    public Boolean getIsDistribution() {
        return isDistribution;
    }

    public void setIsDistribution(Boolean isDistribution) {
        this.isDistribution = isDistribution;
    }

    public Boolean getIsBound() {
        return isBound;
    }

    public void setIsBound(Boolean isBound) {
        this.isBound = isBound;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Boolean getIsDimissionType() {
        return isDimissionType;
    }

    public void setIsDimissionType(Boolean isDimissionType) {
        this.isDimissionType = isDimissionType;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getExamArea() {
        return examArea;
    }

    public void setExamArea(String examArea) {
        this.examArea = examArea == null ? null : examArea.trim();
    }

    public Byte getKeyPoint() {
        return keyPoint;
    }

    public void setKeyPoint(Byte keyPoint) {
        this.keyPoint = keyPoint;
    }

    public String getInActivity() {
        return inActivity;
    }

    public void setInActivity(String inActivity) {
        this.inActivity = inActivity == null ? null : inActivity.trim();
    }

    public Integer getIsClassEnd() {
        return isClassEnd;
    }

    public void setIsClassEnd(Integer isClassEnd) {
        this.isClassEnd = isClassEnd;
    }

    public Date getHandoutTime() {
        return handoutTime;
    }

    public void setHandoutTime(Date handoutTime) {
        this.handoutTime = handoutTime;
    }
}