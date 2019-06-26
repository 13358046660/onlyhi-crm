package cn.onlyhi.client.dto;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/4/14.
 */
public class CourseInfoDto extends BaseDto {

    private String classPackageName;    //课时包名称

    private String courseUuid;

    private String totalTime;   //总课时

    private String courseDate;   //预定的上课日期

    private String startTime;   //预定的开始时间

    private String endTime;     //预定的结束时间

    public String getClassPackageName() {
        return classPackageName;
    }

    public void setClassPackageName(String classPackageName) {
        this.classPackageName = classPackageName;
    }

    public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
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
}
