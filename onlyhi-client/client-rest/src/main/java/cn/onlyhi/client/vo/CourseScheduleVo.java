package cn.onlyhi.client.vo;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/12.
 */
public class CourseScheduleVo {
    private String courseDate;    //课程日期
    private List<TeacherCourseRemindVo> CourseScheduleList;    //课程信息

    public String getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(String courseDate) {
        this.courseDate = courseDate;
    }

    public List<TeacherCourseRemindVo> getCourseScheduleList() {
        return CourseScheduleList;
    }

    public void setCourseScheduleList(List<TeacherCourseRemindVo> courseScheduleList) {
        CourseScheduleList = courseScheduleList;
    }

    @Override
    public String toString() {
        return "CourseScheduleVo{" +
                "courseDate='" + courseDate + '\'' +
                ", CourseScheduleList=" + CourseScheduleList +
                '}';
    }
}
