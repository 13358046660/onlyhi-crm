package cn.onlyhi.client.vo;

import java.util.Date;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/2.
 */
public class TechnicalAssistanceVo {
    private String technicalAssistanceUuid;
    private String userName;
    private String issue;
    private String userAssistanceId;
    private String userAssistancePwd;
    private Date createTime;
    private int assistanceStatus;

    public String getTechnicalAssistanceUuid() {
        return technicalAssistanceUuid;
    }

    public void setTechnicalAssistanceUuid(String technicalAssistanceUuid) {
        this.technicalAssistanceUuid = technicalAssistanceUuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getUserAssistanceId() {
        return userAssistanceId;
    }

    public void setUserAssistanceId(String userAssistanceId) {
        this.userAssistanceId = userAssistanceId;
    }

    public String getUserAssistancePwd() {
        return userAssistancePwd;
    }

    public void setUserAssistancePwd(String userAssistancePwd) {
        this.userAssistancePwd = userAssistancePwd;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getAssistanceStatus() {
        return assistanceStatus;
    }

    public void setAssistanceStatus(int assistanceStatus) {
        this.assistanceStatus = assistanceStatus;
    }

    @Override
    public String toString() {
        return "TechnicalAssistanceVo{" +
                "technicalAssistanceUuid='" + technicalAssistanceUuid + '\'' +
                ", userName='" + userName + '\'' +
                ", issue='" + issue + '\'' +
                ", userAssistanceId='" + userAssistanceId + '\'' +
                ", userAssistancePwd='" + userAssistancePwd + '\'' +
                ", createTime=" + createTime +
                ", assistanceStatus=" + assistanceStatus +
                '}';
    }
}
