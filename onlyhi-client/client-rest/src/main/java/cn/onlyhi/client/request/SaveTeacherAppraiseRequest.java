package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * @author ywj
 */
public class SaveTeacherAppraiseRequest extends BaseRequest {
    @NotBlank(message = "课程uuid不能为空！")
    private String courseUuid;
    @Size(max = 150, message = "描述限150字！")
    private String appraiseContent;//评价内容

    public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid;
    }

    public String getAppraiseContent() {
        return appraiseContent;
    }

    public void setAppraiseContent(String appraiseContent) {
        this.appraiseContent = appraiseContent;
    }

    @Override
    public String toString() {

        return "SaveTeacherAppraiseRequest{" +
                "courseUuid='" + courseUuid + '\'' +
                ", appraiseContent='" + appraiseContent + '\'' +
                "} " + super.toString();
    }

}
