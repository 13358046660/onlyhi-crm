package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/12/6.
 */
public class CourseOvertimeRequest extends BaseRequest {
    @NotBlank(message = "课程uuid不能为空！")
    private String courseUuid;
    @Range(min = 0, max = 60, message = "延长时间范围：0~60")
    private int overtime;   //延长时间 单位：min

    public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid;
    }

    public int getOvertime() {
        return overtime;
    }

    public void setOvertime(int overtime) {
        this.overtime = overtime;
    }

    @Override
    public String toString() {
        return "CourseOvertimeRequest{" +
                "courseUuid='" + courseUuid + '\'' +
                ", overtime=" + overtime +
                "} " + super.toString();
    }
}
