package cn.onlyhi.client.vo;

import java.util.Date;

public class CoursewareVo {
    private String coursewareUuid;
    private String teacherUuid;
    private String coursewareUrl;
    private String coursewareName;
    private Integer pageNum;
    private Integer converStatus;   //课件转换状态  0:转换未开始, 1:转换中, 2:转换成功, 3:转换失败
    private Date updateTime;
    /**课件源文件是否可以下载0不可以1可以*/
    private Integer isDownload;

    public Integer getIsDownload() {
        return isDownload;
    }

    public void setIsDownload(Integer isDownload) {
        this.isDownload = isDownload;
    }

    public String getCoursewareUuid() {
        return coursewareUuid;
    }

    public void setCoursewareUuid(String coursewareUuid) {
        this.coursewareUuid = coursewareUuid;
    }

    public String getTeacherUuid() {
        return teacherUuid;
    }

    public void setTeacherUuid(String teacherUuid) {
        this.teacherUuid = teacherUuid;
    }

    public String getCoursewareUrl() {
        return coursewareUrl;
    }

    public void setCoursewareUrl(String coursewareUrl) {
        this.coursewareUrl = coursewareUrl;
    }

    public String getCoursewareName() {
        return coursewareName;
    }

    public void setCoursewareName(String coursewareName) {
        this.coursewareName = coursewareName;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getConverStatus() {
        return converStatus;
    }

    public void setConverStatus(Integer converStatus) {
        this.converStatus = converStatus;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "CoursewareVo{" +
                "coursewareUuid='" + coursewareUuid + '\'' +
                ", teacherUuid='" + teacherUuid + '\'' +
                ", coursewareUrl='" + coursewareUrl + '\'' +
                ", coursewareName='" + coursewareName + '\'' +
                ", pageNum=" + pageNum +
                ", converStatus=" + converStatus +
                ", updateTime=" + updateTime +
                '}';
    }
}