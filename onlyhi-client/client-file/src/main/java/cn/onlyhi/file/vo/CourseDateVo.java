package cn.onlyhi.file.vo;


import java.io.File;

public class CourseDateVo {
    private String courseId;
    /**课程id所属日期目录*/
    private File recordLogsDir;
    private Integer recordId;

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public File getRecordLogsDir() {
        return recordLogsDir;
    }

    public void setRecordLogsDir(File recordLogsDir) {
        this.recordLogsDir = recordLogsDir;
    }

    @Override
    public String toString() {
        return "CourseDateVo{" +
                "courseId='" + courseId + '\'' +
                ", recordLogsDir=" + recordLogsDir +
                '}';
    }
}