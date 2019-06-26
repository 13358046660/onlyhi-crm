package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/3/16.
 */
public class SaveCoursewareDirRequest extends BaseRequest{
    @NotBlank(message = "目录名称不能为空！")
    @Length(max = 30, message = "目录名称应在1-30字之间！")
    private String coursewareDirName;

    public String getCoursewareDirName() {
        return coursewareDirName;
    }

    public void setCoursewareDirName(String coursewareDirName) {
        this.coursewareDirName = coursewareDirName;
    }

    @Override
    public String toString() {
        return "SaveCoursewareDirRequest{" +
                "coursewareDirName='" + coursewareDirName + '\'' +
                "} " + super.toString();
    }
}
