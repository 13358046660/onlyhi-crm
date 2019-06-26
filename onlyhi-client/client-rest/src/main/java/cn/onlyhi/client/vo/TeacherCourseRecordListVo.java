package cn.onlyhi.client.vo;

import java.util.List;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2018/2/1.
 */
public class TeacherCourseRecordListVo {
    private String courseDate;
    private List<TeacherCourseRecordVo> list;

    public String getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(String courseDate) {
        this.courseDate = courseDate;
    }

    public List<TeacherCourseRecordVo> getList() {
        return list;
    }

    public void setList(List<TeacherCourseRecordVo> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "TeacherCourseRecordListVo{" +
                "courseDate='" + courseDate + '\'' +
                ", list=" + list +
                '}';
    }
}
