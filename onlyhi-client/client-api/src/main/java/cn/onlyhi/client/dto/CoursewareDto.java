package cn.onlyhi.client.dto;

import java.util.Date;

public class CoursewareDto extends BaseDto {

    private String coursewareUuid;
    private Integer status;
    private String remark;
    private String createUser;  //创建人
    private Date createTime;
    private Date updateTime;
    private String subject;
    private String grade;
    private String courseKnowledgeName; //知识点
    private Integer courseClasses;
    private String coursewareName;
    private String coursewarePreviewUrl;
    private String coursewareVersion;   //教材版本
    private String coursewareUrl;
    private String originalName;
    private Integer pageNum;
    private String teacherName;
    private Integer converStatus;

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public Integer getConverStatus() {
        return converStatus;
    }

    public void setConverStatus(Integer converStatus) {
        this.converStatus = converStatus;
    }

    public String getCoursewareVersion() {
        return coursewareVersion;
    }

    public void setCoursewareVersion(String coursewareVersion) {
        this.coursewareVersion = coursewareVersion;
    }

    public String getCoursewarePreviewUrl() {
        return coursewarePreviewUrl;
    }

    public void setCoursewarePreviewUrl(String coursewarePreviewUrl) {
        this.coursewarePreviewUrl = coursewarePreviewUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getCourseKnowledgeName() {
        return courseKnowledgeName;
    }

    public void setCourseKnowledgeName(String courseKnowledgeName) {
        this.courseKnowledgeName = courseKnowledgeName;
    }

    public Integer getCourseClasses() {
        return courseClasses;
    }

    public void setCourseClasses(Integer courseClasses) {
        this.courseClasses = courseClasses;
    }

    public String getCoursewareName() {
        return coursewareName;
    }

    public void setCoursewareName(String coursewareName) {
        this.coursewareName = coursewareName;
    }

    public String getCoursewareUuid() {
        return coursewareUuid;
    }

    public void setCoursewareUuid(String coursewareUuid) {
        this.coursewareUuid = coursewareUuid;
    }

    public String getCoursewareUrl() {
        return coursewareUrl;
    }

    public void setCoursewareUrl(String coursewareUrl) {
        this.coursewareUrl = coursewareUrl;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }
}