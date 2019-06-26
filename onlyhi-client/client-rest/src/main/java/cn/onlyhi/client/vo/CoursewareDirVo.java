package cn.onlyhi.client.vo;

import java.util.Date;
import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/3/15.
 */
public class CoursewareDirVo {
    private String coursewareDirUuid;
    private String coursewareDirName;
    private Date updateTime;
    private List<CoursewareVo> coursewareList;

    public String getCoursewareDirUuid() {
        return coursewareDirUuid;
    }

    public void setCoursewareDirUuid(String coursewareDirUuid) {
        this.coursewareDirUuid = coursewareDirUuid;
    }

    public String getCoursewareDirName() {
        return coursewareDirName;
    }

    public void setCoursewareDirName(String coursewareDirName) {
        this.coursewareDirName = coursewareDirName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<CoursewareVo> getCoursewareList() {
        return coursewareList;
    }

    public void setCoursewareList(List<CoursewareVo> coursewareList) {
        this.coursewareList = coursewareList;
    }

    @Override
    public String toString() {
        return "CoursewareDirVo{" +
                "coursewareDirUuid='" + coursewareDirUuid + '\'' +
                ", coursewareDirName='" + coursewareDirName + '\'' +
                ", updateTime=" + updateTime +
                ", coursewareList=" + coursewareList +
                '}';
    }
}
