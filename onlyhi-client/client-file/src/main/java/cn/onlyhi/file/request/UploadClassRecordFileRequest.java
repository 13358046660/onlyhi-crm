package cn.onlyhi.file.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/3/23.
 */
public class UploadClassRecordFileRequest extends BaseRequest {
    private MultipartFile file;
    @NotBlank(message = "课程uuid不能为空！")
    private String courseUuid;
    @NotBlank(message = "recordLogsPath不能为空！")
    private String recordLogsPath;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid;
    }

    public String getRecordLogsPath() {
        return recordLogsPath;
    }

    public void setRecordLogsPath(String recordLogsPath) {
        this.recordLogsPath = recordLogsPath;
    }
}
