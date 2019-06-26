package cn.onlyhi.client.vo;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/12.
 */
public class StudentCourseScheduleVo {
    private String courseDate;    //课程日期
    private List<StudentCourseRemindVo> CourseScheduleList;    //课程信息

    public String getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(String courseDate) {
        this.courseDate = courseDate;
    }

    public List<StudentCourseRemindVo> getCourseScheduleList() {
        return CourseScheduleList;
    }

    public void setCourseScheduleList(List<StudentCourseRemindVo> courseScheduleList) {
        CourseScheduleList = courseScheduleList;
    }

    @Override
    public String toString() {
        return "StudentCourseScheduleVo{" +
                "courseDate='" + courseDate + '\'' +
                ", CourseScheduleList=" + CourseScheduleList +
                '}';
    }
}
