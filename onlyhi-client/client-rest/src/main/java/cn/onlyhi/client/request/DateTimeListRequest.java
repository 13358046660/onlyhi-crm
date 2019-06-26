package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;

import java.util.List;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2018/2/2.
 */
public class DateTimeListRequest extends BaseRequest {
    private List<String> dateTimeList;
    private String grade;
    public List<String> getDateTimeList() {
        return dateTimeList;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setDateTimeList(List<String> dateTimeList) {
        this.dateTimeList = dateTimeList;
    }

    @Override
    public String toString() {
        return "DateTimeListRequest{" +
                "dateTimeList=" + dateTimeList +
                ", grade='" + grade + '\'' +
                '}';
    }
}
