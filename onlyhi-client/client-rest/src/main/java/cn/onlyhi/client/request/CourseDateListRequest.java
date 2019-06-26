package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/25.
 */
public class CourseDateListRequest extends BaseRequest {
    private List<String> courseDateList;    //课程日期集合，格式：yyyy-MM-dd

    public List<String> getCourseDateList() {
        return courseDateList;
    }

    public void setCourseDateList(List<String> courseDateList) {
        this.courseDateList = courseDateList;
    }

    @Override
    public String toString() {
        return "CourseDateListRequest{" +
                "courseDateList=" + courseDateList +
                "} " + super.toString();
    }
}
