package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Digits;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/3/22.
 */
public class CourseRoomRequest extends BaseRequest {
    @NotBlank(message = "课程uuid不能为空！")
    private String courseUuid;

    @NotBlank(message = "流量时长不可为空！")
    @Digits(integer = 4, fraction = 2, message = "流量时长最多为两位小数！")
    private String classTime;   //统计的流量时长

    public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    @Override
    public String toString() {
        return "CourseRoomRequest{" +
                "courseUuid='" + courseUuid + '\'' +
                ", classTime='" + classTime + '\'' +
                "} " + super.toString();
    }
}
