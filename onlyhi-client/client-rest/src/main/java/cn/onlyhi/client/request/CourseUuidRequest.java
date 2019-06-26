package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/13.
 */
public class CourseUuidRequest extends BaseRequest {
    @NotBlank(message = "课程uuid不能为空！")
    private String courseUuid;

    public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid;
    }

    @Override
    public String toString() {
        return "CourseUuidRequest{" +
                "courseUuid='" + courseUuid + '\'' +
                "} " + super.toString();
    }
}
