package cn.onlyhi.client.vo;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/2/1.
 */
public class StudentNoEndCourseListVo {
    private String courseDate;
    private List<StudentNoEndCourseVo> list;

    public String getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(String courseDate) {
        this.courseDate = courseDate;
    }

    public List<StudentNoEndCourseVo> getList() {
        return list;
    }

    public void setList(List<StudentNoEndCourseVo> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "StudentNoEndCourseListVo{" +
                "courseDate='" + courseDate + '\'' +
                ", list=" + list +
                '}';
    }
}
