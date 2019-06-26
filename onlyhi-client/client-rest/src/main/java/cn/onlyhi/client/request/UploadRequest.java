package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/3/23.
 */
public class UploadRequest extends BaseRequest {
    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "UploadRequest{" +
                "file=" + file +
                "} " + super.toString();
    }
}
