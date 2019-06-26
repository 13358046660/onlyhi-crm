package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/25.
 */
public class CourseDateRequest extends BaseRequest {
    @NotBlank(message = "课程日期不可为空！")
    @Pattern(regexp = "^(\\d{4}-\\d{2}-\\d{2})$", message = "课程日期格式错误，正确格式：yyyy-MM-dd")
    private String courseDate;

    public String getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(String courseDate) {
        this.courseDate = courseDate;
    }

    @Override
    public String toString() {
        return "CourseDateRequest{" +
                "courseDate='" + courseDate + '\'' +
                "} " + super.toString();
    }
}
