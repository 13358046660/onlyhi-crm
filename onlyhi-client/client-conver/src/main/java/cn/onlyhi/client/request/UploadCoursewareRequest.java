package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/3/19.
 */
public class UploadCoursewareRequest extends BaseRequest {
    private MultipartFile originalFile;
    private MultipartFile pdfFile;
    private String teacherUuid;
    private String websocketToken;
    private String backUrl;

    public String getBackUrl() {
        return backUrl;
    }

    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }

    public String getWebsocketToken() {
        return websocketToken;
    }

    public void setWebsocketToken(String websocketToken) {
        this.websocketToken = websocketToken;
    }

    public String getTeacherUuid() {
        return teacherUuid;
    }

    public void setTeacherUuid(String teacherUuid) {
        this.teacherUuid = teacherUuid;
    }

    public MultipartFile getOriginalFile() {
        return originalFile;
    }

    public void setOriginalFile(MultipartFile originalFile) {
        this.originalFile = originalFile;
    }

    public MultipartFile getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(MultipartFile pdfFile) {
        this.pdfFile = pdfFile;
    }


}
