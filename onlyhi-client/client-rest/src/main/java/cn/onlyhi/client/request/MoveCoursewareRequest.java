package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/3/16.
 */
public class MoveCoursewareRequest extends BaseRequest {
    @NotBlank(message = "coursewareUuid不为空！")
    private String coursewareUuid;
    private String coursewareDirUuid;

    public String getCoursewareUuid() {
        return coursewareUuid;
    }

    public void setCoursewareUuid(String coursewareUuid) {
        this.coursewareUuid = coursewareUuid;
    }

    public String getCoursewareDirUuid() {
        return coursewareDirUuid;
    }

    public void setCoursewareDirUuid(String coursewareDirUuid) {
        this.coursewareDirUuid = coursewareDirUuid;
    }

    @Override
    public String toString() {
        return "MoveCoursewareRequest{" +
                "coursewareUuid='" + coursewareUuid + '\'' +
                ", coursewareDirUuid='" + coursewareDirUuid + '\'' +
                "} " + super.toString();
    }
}
