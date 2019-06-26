package cn.onlyhi.file.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/3/23.
 */
public class UploadTrackFileRequest extends BaseRequest {
    private MultipartFile file;
    @NotBlank(message = "课程uuid不能为空！")
    private String courseUuid;
    private String dbFilePath;
    private Date courseDate;
    private String restoreDate;
    private String scriptUrl;

    public String getScriptUrl() {
        return scriptUrl;
    }

    public void setScriptUrl(String scriptUrl) {
        this.scriptUrl = scriptUrl;
    }

    public String getRestoreDate() {
        return restoreDate;
    }

    public void setRestoreDate(String restoreDate) {
        this.restoreDate = restoreDate;
    }

    public Date getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(Date courseDate) {
        this.courseDate = courseDate;
    }

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

    public String getDbFilePath() {
        return dbFilePath;
    }

    public void setDbFilePath(String dbFilePath) {
        this.dbFilePath = dbFilePath;
    }

}
