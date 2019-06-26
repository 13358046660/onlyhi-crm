package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/3/21.
 */
public class CoursewareNameRequest extends BaseRequest {
    private String coursewareName;

    public String getCoursewareName() {
        return coursewareName;
    }

    public void setCoursewareName(String coursewareName) {
        this.coursewareName = coursewareName;
    }

    @Override
    public String toString() {
        return "CoursewareNameRequest{" +
                "coursewareName='" + coursewareName + '\'' +
                "} " + super.toString();
    }
}
