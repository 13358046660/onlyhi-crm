package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 课件表
 *
 * @author shitongtong
 * Created by shitongtong on 2018/03/15
 */
public class Courseware extends BasePo {
    private Integer id;

    private String coursewareUuid;

    /**
     * cp_course_version_uuid
     */
    private String courseVersionUuid;

    /**
     * 教师uuid
     */
    private String teacherUuid;

    /**
     * 课件url
     */
    private String coursewareUrl;

    /**
     * 服务器路径
     */
    private String coursewarePath;

    /**
     * 上传的原课件名称
     */
    private String originalName;

    /**
     * 课件名称
     */
    private String coursewareName;

    /**
     * 课件类型:系统和教师
     */
    private String coursewareType;

    /**
     * 课件转换状态  0:转换未开始, 1:转换中, 2:转换成功, 3:转换失败
     */
    private Integer converStatus;

    /**
     * 转换成pdf名称
     */
    private String pdfName;

    /**
     * 页数
     */
    private Integer pageNum;

    /**
     * 科目
     */
    private String subject;

    /**
     * 年级
     */
    private String grade;

    /**
     * 课程知识点uuid
     */
    private String courseKnowledgeUuid;

    /**
     * 课程类别：1：普通、2：精品
     */
    private Integer courseClasses;

    /**
     * 课件pdf预览路径
     */
    private String coursewarePreviewUrl;

    /**
     * 所属课程的知识点uuid（包括一级二级三级...）,多个以,分割
     */
    private String courseKnowledgeUuids;

    /**
     * 课件目录uuid
     */
    private String coursewareDirUuid;

    /**
     * 状态:0:删除，1：正常
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;

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
     * 已加密的文件流
     */
    private String streamMd5;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCoursewareUuid() {
        return coursewareUuid;
    }

    public void setCoursewareUuid(String coursewareUuid) {
        this.coursewareUuid = coursewareUuid == null ? null : coursewareUuid.trim();
    }

    public String getCourseVersionUuid() {
        return courseVersionUuid;
    }

    public void setCourseVersionUuid(String courseVersionUuid) {
        this.courseVersionUuid = courseVersionUuid == null ? null : courseVersionUuid.trim();
    }

    public String getTeacherUuid() {
        return teacherUuid;
    }

    public void setTeacherUuid(String teacherUuid) {
        this.teacherUuid = teacherUuid == null ? null : teacherUuid.trim();
    }

    public String getCoursewareUrl() {
        return coursewareUrl;
    }

    public void setCoursewareUrl(String coursewareUrl) {
        this.coursewareUrl = coursewareUrl == null ? null : coursewareUrl.trim();
    }

    public String getCoursewarePath() {
        return coursewarePath;
    }

    public void setCoursewarePath(String coursewarePath) {
        this.coursewarePath = coursewarePath == null ? null : coursewarePath.trim();
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName == null ? null : originalName.trim();
    }

    public String getCoursewareName() {
        return coursewareName;
    }

    public void setCoursewareName(String coursewareName) {
        this.coursewareName = coursewareName == null ? null : coursewareName.trim();
    }

    public String getCoursewareType() {
        return coursewareType;
    }

    public void setCoursewareType(String coursewareType) {
        this.coursewareType = coursewareType == null ? null : coursewareType.trim();
    }

    public Integer getConverStatus() {
        return converStatus;
    }

    public void setConverStatus(Integer converStatus) {
        this.converStatus = converStatus;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName == null ? null : pdfName.trim();
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject == null ? null : subject.trim();
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade == null ? null : grade.trim();
    }

    public String getCourseKnowledgeUuid() {
        return courseKnowledgeUuid;
    }

    public void setCourseKnowledgeUuid(String courseKnowledgeUuid) {
        this.courseKnowledgeUuid = courseKnowledgeUuid == null ? null : courseKnowledgeUuid.trim();
    }

    public Integer getCourseClasses() {
        return courseClasses;
    }

    public void setCourseClasses(Integer courseClasses) {
        this.courseClasses = courseClasses;
    }

    public String getCoursewarePreviewUrl() {
        return coursewarePreviewUrl;
    }

    public void setCoursewarePreviewUrl(String coursewarePreviewUrl) {
        this.coursewarePreviewUrl = coursewarePreviewUrl == null ? null : coursewarePreviewUrl.trim();
    }

    public String getCourseKnowledgeUuids() {
        return courseKnowledgeUuids;
    }

    public void setCourseKnowledgeUuids(String courseKnowledgeUuids) {
        this.courseKnowledgeUuids = courseKnowledgeUuids == null ? null : courseKnowledgeUuids.trim();
    }

    public String getCoursewareDirUuid() {
        return coursewareDirUuid;
    }

    public void setCoursewareDirUuid(String coursewareDirUuid) {
        this.coursewareDirUuid = coursewareDirUuid == null ? null : coursewareDirUuid.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
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

    public String getStreamMd5() {
        return streamMd5;
    }

    public void setStreamMd5(String streamMd5) {
        this.streamMd5 = streamMd5 == null ? null : streamMd5.trim();
    }
}