package cn.onlyhi.client.vo;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/3/15.
 */
public class CoursewareListVo {
    private List<CoursewareDirVo> coursewareDirList;
    private List<CoursewareVo> coursewareList;

    public List<CoursewareDirVo> getCoursewareDirList() {
        return coursewareDirList;
    }

    public void setCoursewareDirList(List<CoursewareDirVo> coursewareDirList) {
        this.coursewareDirList = coursewareDirList;
    }

    public List<CoursewareVo> getCoursewareList() {
        return coursewareList;
    }

    public void setCoursewareList(List<CoursewareVo> coursewareList) {
        this.coursewareList = coursewareList;
    }

    @Override
    public String toString() {
        return "CoursewareListVo{" +
                "coursewareDirList=" + coursewareDirList +
                ", coursewareList=" + coursewareList +
                '}';
    }
}
