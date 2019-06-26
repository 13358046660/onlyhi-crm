package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/4/9.
 */
public class DownloadRequest extends BaseRequest {
    @NotBlank(message = "fileUrl不能为空！")
    private String fileUrl;

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    @Override
    public String toString() {
        return "DownloadRequest{" +
                "fileUrl='" + fileUrl + '\'' +
                "} " + super.toString();
    }
}
