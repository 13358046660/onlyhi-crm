package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/3/16.
 */
public class CoursewareDirUuidRequest extends BaseRequest {
    @NotBlank(message = "coursewareDirUuid不能为空！")
    private String coursewareDirUuid;

    public String getCoursewareDirUuid() {
        return coursewareDirUuid;
    }

    public void setCoursewareDirUuid(String coursewareDirUuid) {
        this.coursewareDirUuid = coursewareDirUuid;
    }

    @Override
    public String toString() {
        return "CoursewareDirUuidRequest{" +
                "coursewareDirUuid='" + coursewareDirUuid + '\'' +
                "} " + super.toString();
    }
}
