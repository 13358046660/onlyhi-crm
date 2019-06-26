package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/18.
 */
public class StudentCourseRecordVo {
    private String courseUuid;
    private String courseDate;
    private String startTime;
    private String endTime;
    private String subject;
    private String name;
    private Integer courseType;
    private Integer recordStatus; //1:有回放，2:没有回放数据，3:视频转换中，次日方可查看。
    private String classAppraiseUuid;   //学生对老师上课评价uuid，若为空则未评价
    private String classTeacherAppraiseUuid;//老师对学生上课的反馈

    public String getClassAppraiseUuid() {
        return classAppraiseUuid;
    }

    public void setClassAppraiseUuid(String classAppraiseUuid) {
        this.classAppraiseUuid = classAppraiseUuid;
    }

    public Integer getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(Integer recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getClassTeacherAppraiseUuid() {
		return classTeacherAppraiseUuid;
	}

	public void setClassTeacherAppraiseUuid(String classTeacherAppraiseUuid) {
		this.classTeacherAppraiseUuid = classTeacherAppraiseUuid;
	}

	public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCourseType() {
        return courseType;
    }

    public void setCourseType(Integer courseType) {
        this.courseType = courseType;
    }

    @Override
    public String toString() {
        return "StudentCourseRecordVo{" +
                "courseUuid='" + courseUuid + '\'' +
                ", courseDate='" + courseDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", subject='" + subject + '\'' +
                ", name='" + name + '\'' +
                ", courseType=" + courseType +
                ", recordStatus=" + recordStatus +
                ", classAppraiseUuid='" + classAppraiseUuid + '\'' +
                ", classTeacherAppraiseUuid='" + classTeacherAppraiseUuid + '\'' +
                '}';
    }
}
