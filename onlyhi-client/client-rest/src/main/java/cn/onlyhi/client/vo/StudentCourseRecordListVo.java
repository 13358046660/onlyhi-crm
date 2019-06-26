package cn.onlyhi.client.vo;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/2/1.
 */
public class StudentCourseRecordListVo {
    private String courseDate;
    private List<StudentCourseRecordVo> list;

    public String getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(String courseDate) {
        this.courseDate = courseDate;
    }

    public List<StudentCourseRecordVo> getList() {
        return list;
    }

    public void setList(List<StudentCourseRecordVo> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "StudentCourseRecordListVo{" +
                "courseDate='" + courseDate + '\'' +
                ", list=" + list +
                '}';
    }
}
