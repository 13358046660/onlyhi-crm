package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/12/6.
 */
public class TodayCourseRequest extends BaseRequest {
    /**
     *  学生或老师姓名
     */
    private String name;
    /**
     *  学生或老师电话
     */
    private String phone;

    @Override
    public String toString() {
        return "TodayCourseRequest{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
