package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/3/29.
 */
public class UserFeedbackRequest extends BaseRequest {
    @NotBlank(message = "内容不能为空！")
    @Length(max = 140, message = "内容字数应在1-140之间！")
    private String content;
    private String osName;  //客户端操作系统名称
    private String version; //版本号

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "UserFeedbackRequest{" +
                "content='" + content + '\'' +
                ", osName='" + osName + '\'' +
                ", version='" + version + '\'' +
                "} " + super.toString();
    }
}
