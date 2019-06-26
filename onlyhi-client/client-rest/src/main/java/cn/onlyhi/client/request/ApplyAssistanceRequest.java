package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/2.
 */
public class ApplyAssistanceRequest extends BaseRequest{
    @NotBlank(message = "id不能为空！")
    private String assistanceId;
    @NotBlank(message = "密码不能为空！")
    private String assistancePwd;
    private String issue;

    public String getAssistanceId() {
        return assistanceId;
    }

    public void setAssistanceId(String assistanceId) {
        this.assistanceId = assistanceId;
    }

    public String getAssistancePwd() {
        return assistancePwd;
    }

    public void setAssistancePwd(String assistancePwd) {
        this.assistancePwd = assistancePwd;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    @Override
    public String toString() {
        return "ApplyAssistanceRequest{" +
                "assistanceId='" + assistanceId + '\'' +
                ", assistancePwd='" + assistancePwd + '\'' +
                ", issue='" + issue + '\'' +
                "} " + super.toString();
    }
}
