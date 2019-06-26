package cn.onlyhi.client.dto;

public class ClassOutPutExcel {
    private String stuName;
    private String tcName;
    private String courseDate;
    private String startTime;
    private String endTime;
    private String tcEnterTime;
    private String tcOuterTime;
    private String stuEnterTime;
    private String stuOuterTime;
    private Integer realLength;
    private Integer teacherLength;
    private Integer leadsLength;
    private String roomId;

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getTcName() {
        return tcName;
    }

    public void setTcName(String tcName) {
        this.tcName = tcName;
    }

    public String getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(String courseDate) {
        this.courseDate = courseDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTcEnterTime() {
        return tcEnterTime;
    }

    public void setTcEnterTime(String tcEnterTime) {
        this.tcEnterTime = tcEnterTime;
    }

    public String getTcOuterTime() {
        return tcOuterTime;
    }

    public void setTcOuterTime(String tcOuterTime) {
        this.tcOuterTime = tcOuterTime;
    }

    public String getStuEnterTime() {
        return stuEnterTime;
    }

    public void setStuEnterTime(String stuEnterTime) {
        this.stuEnterTime = stuEnterTime;
    }

    public String getStuOuterTime() {
        return stuOuterTime;
    }

    public void setStuOuterTime(String stuOuterTime) {
        this.stuOuterTime = stuOuterTime;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Integer getRealLength() {
        return realLength;
    }

    public void setRealLength(Integer realLength) {
        this.realLength = realLength;
    }

    public Integer getTeacherLength() {
        return teacherLength;
    }

    public void setTeacherLength(Integer teacherLength) {
        this.teacherLength = teacherLength;
    }

    public Integer getLeadsLength() {
        return leadsLength;
    }

    public void setLeadsLength(Integer leadsLength) {
        this.leadsLength = leadsLength;
    }

    private String grade;

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

}
