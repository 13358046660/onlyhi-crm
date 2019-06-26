package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/3/24.
 */
public class CoursewareUuidRequest extends BaseRequest {
    @NotBlank(message = "课件uuid不能为空！")
    private String coursewareUuid;

    public String getCoursewareUuid() {
        return coursewareUuid;
    }

    public void setCoursewareUuid(String coursewareUuid) {
        this.coursewareUuid = coursewareUuid;
    }

    @Override
    public String toString() {
        return "CoursewareUuidRequest{" +
                "coursewareUuid='" + coursewareUuid + '\'' +
                "} " + super.toString();
    }
}
