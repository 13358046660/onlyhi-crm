package cn.onlyhi.client.vo;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/4/21.
 */
public class CourseUserListVo {
    private CourseUserVo teaUser;
    private CourseUserVo stuUser;
    private CourseUserVo patUser;
    private CourseUserVo ccUser;
    private CourseUserVo crUser;
    private List<CourseUserVo> tsUserList;
    private List<CourseUserVo> qcUserList;
    /**技術支持*/
    private CourseUserVo technicalAssistance;

    public CourseUserVo getTechnicalAssistance() {
        return technicalAssistance;
    }

    public void setTechnicalAssistance(CourseUserVo technicalAssistance) {
        this.technicalAssistance = technicalAssistance;
    }

    public CourseUserVo getTeaUser() {
        return teaUser;
    }

    public void setTeaUser(CourseUserVo teaUser) {
        this.teaUser = teaUser;
    }

    public CourseUserVo getStuUser() {
        return stuUser;
    }

    public void setStuUser(CourseUserVo stuUser) {
        this.stuUser = stuUser;
    }

    public CourseUserVo getPatUser() {
        return patUser;
    }

    public void setPatUser(CourseUserVo patUser) {
        this.patUser = patUser;
    }

    public CourseUserVo getCcUser() {
        return ccUser;
    }

    public void setCcUser(CourseUserVo ccUser) {
        this.ccUser = ccUser;
    }

    public CourseUserVo getCrUser() {
        return crUser;
    }

    public void setCrUser(CourseUserVo crUser) {
        this.crUser = crUser;
    }

    public List<CourseUserVo> getTsUserList() {
        return tsUserList;
    }

    public void setTsUserList(List<CourseUserVo> tsUserList) {
        this.tsUserList = tsUserList;
    }

    public List<CourseUserVo> getQcUserList() {
        return qcUserList;
    }

    public void setQcUserList(List<CourseUserVo> qcUserList) {
        this.qcUserList = qcUserList;
    }

    @Override
    public String toString() {
        return "CourseUserListVo{" +
                "teaUser=" + teaUser +
                ", stuUser=" + stuUser +
                ", patUser=" + patUser +
                ", ccUser=" + ccUser +
                ", crUser=" + crUser +
                ", tsUserList=" + tsUserList +
                ", qcUserList=" + qcUserList +
                '}';
    }
}
