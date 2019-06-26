package cn.onlyhi.client.vo;

import java.util.List;

/**
 * 声网Uid列表
 *
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/12/4.
 */
public class AgoraVo {
    private Integer teaAgoraUid;
    private Integer stuAgoraUid;
    private Integer patAgoraUid;    //家长
    private Integer ccAgoraUid;
    private Integer crAgoraUid;
    private List<Integer> tsAgoraUidList;  //教学监课
    private List<Integer> qcAgoraUidList;  //qc监课

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

    public Integer getPatAgoraUid() {
        return patAgoraUid;
    }

    public void setPatAgoraUid(Integer patAgoraUid) {
        this.patAgoraUid = patAgoraUid;
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

    public List<Integer> getTsAgoraUidList() {
        return tsAgoraUidList;
    }

    public void setTsAgoraUidList(List<Integer> tsAgoraUidList) {
        this.tsAgoraUidList = tsAgoraUidList;
    }

    public List<Integer> getQcAgoraUidList() {
        return qcAgoraUidList;
    }

    public void setQcAgoraUidList(List<Integer> qcAgoraUidList) {
        this.qcAgoraUidList = qcAgoraUidList;
    }

    @Override
    public String toString() {
        return "AgoraVo{" +
                "teaAgoraUid=" + teaAgoraUid +
                ", stuAgoraUid=" + stuAgoraUid +
                ", patAgoraUid=" + patAgoraUid +
                ", ccAgoraUid=" + ccAgoraUid +
                ", crAgoraUid=" + crAgoraUid +
                ", tsAgoraUidList=" + tsAgoraUidList +
                ", qcAgoraUidList=" + qcAgoraUidList +
                '}';
    }
}
