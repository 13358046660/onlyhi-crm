package cn.onlyhi.file.request;

import cn.onlyhi.common.enums.ClientEnum;
import cn.onlyhi.common.request.BaseRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/3/23.
 */
public class UploadFileRequest extends BaseRequest {
    private MultipartFile file;
//    @NotBlank(message = "上传文件名不能为空！")
    private String fileName;//因为file.getOriginalFilename();乱码
    private String uploadSource;
    private ClientEnum.CoursewareType coursewareType;

    public ClientEnum.CoursewareType getCoursewareType() {
        return coursewareType;
    }

    public void setCoursewareType(ClientEnum.CoursewareType coursewareType) {
        this.coursewareType = coursewareType;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUploadSource() {
        return uploadSource;
    }

    public void setUploadSource(String uploadSource) {
        this.uploadSource = uploadSource;
    }

    @Override
    public String toString() {
        return "UploadFileRequest{" +
                "file=" + file +
                ", fileName='" + fileName + '\'' +
                ", uploadSource='" + uploadSource + '\'' +
                ", coursewareType=" + coursewareType +
                "} " + super.toString();
    }
}
