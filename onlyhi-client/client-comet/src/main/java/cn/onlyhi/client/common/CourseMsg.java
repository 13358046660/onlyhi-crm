package cn.onlyhi.client.common;

/**
 * 登录类型消息：
 *
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/4.
 */
public class CourseMsg extends BaseMsg {
    private String token;
    private String courseUuid;

    public CourseMsg() {
        super();
        setType(4);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid;
    }
}
