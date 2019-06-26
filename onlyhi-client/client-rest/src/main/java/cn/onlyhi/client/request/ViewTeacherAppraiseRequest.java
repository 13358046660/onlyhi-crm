package cn.onlyhi.client.request;

import org.hibernate.validator.constraints.NotBlank;

import cn.onlyhi.common.request.BaseRequest;

/**
 * @Author ywj
 * <p>
 * Created by ywj on 2018/1/24.
 */

public class ViewTeacherAppraiseRequest extends BaseRequest {
    private String classTeacherAppraiseUuid;   //上课评价uuid，若为空则未评价

    public String getClassTeacherAppraiseUuid() {
        return classTeacherAppraiseUuid;
    }

    public void setClassTeacherAppraiseUuid(String classTeacherAppraiseUuid) {
        this.classTeacherAppraiseUuid = classTeacherAppraiseUuid;
    }

    @Override
    public String toString() {
        return "ViewTeacherAppraiseRequest{" +
                "classTeacherAppraiseUuid='" + classTeacherAppraiseUuid + '\'' +
                "} " + super.toString();
    }

}
