package cn.onlyhi.file.vo;

/**
 * 声网Uid列表
 *
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/12/4.
 */
public class MasterAgoraVo {
    private Integer teaAgoraUid;
    private Integer stuAgoraUid;
    private Integer ccAgoraUid;
    private Integer crAgoraUid;
    private Integer courseType;//0:测评课;1:正式课;2:调试课

    public Integer getTeaAgoraUid() {
        return teaAgoraUid;
    }

    public void setTeaAgoraUid(Integer teaAgoraUid) {
        this.teaAgoraUid = teaAgoraUid;
    }

    public Integer getStuAgoraUid() {
        return stuAgoraUid;
    }

    public void setStuAgoraUid(Integer stuAgoraUid) {
        this.stuAgoraUid = stuAgoraUid;
    }

    public Integer getCcAgoraUid() {
        return ccAgoraUid;
    }

    public void setCcAgoraUid(Integer ccAgoraUid) {
        this.ccAgoraUid = ccAgoraUid;
    }

    public Integer getCrAgoraUid() {
        return crAgoraUid;
    }

    public void setCrAgoraUid(Integer crAgoraUid) {
        this.crAgoraUid = crAgoraUid;
    }

    public Integer getCourseType() {
        return courseType;
    }

    public void setCourseType(Integer courseType) {
        this.courseType = courseType;
    }
}
