package cn.onlyhi.file.request;

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
    private String coursewareUuid;
    /**客户端上传的文件是否是源文件0是1否，用于反馈给前端判断当前文件是否可下载*/
    private Integer isOriginalFile;
   /**由于客户端与页面用用同一个下载，此参数传1表示页面调用的上传，用于标注在转换方法中是否保存courseware_url，客户端转码时不需要存*/
    private Integer isPageRequest;
    /**客户端上传时需传入源文件名，用于课件列表展示文件名称（必须是源文件，因为要下载的是源文件）*/
    private String originalName;

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public Integer getIsPageRequest() {
        return isPageRequest;
    }

    public void setIsPageRequest(Integer isPageRequest) {
        this.isPageRequest = isPageRequest;
    }

    public Integer getIsOriginalFile() {
        return isOriginalFile;
    }

    public void setIsOriginalFile(Integer isOriginalFile) {
        this.isOriginalFile = isOriginalFile;
    }

    public String getCoursewareUuid() {
        return coursewareUuid;
    }

    public void setCoursewareUuid(String coursewareUuid) {
        this.coursewareUuid = coursewareUuid;
    }

    public MultipartFile getOriginalFile() {
        return originalFile;
    }

    @Override
    public String toString() {
        return "UploadCoursewareRequest{" +
                "originalFile=" + originalFile +
                ", pdfFile=" + pdfFile +
                ", coursewareUuid='" + coursewareUuid + '\'' +
                ", isOriginalFile=" + isOriginalFile +
                ", isPageRequest=" + isPageRequest +
                ", originalName='" + originalName + '\'' +
                '}';
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
