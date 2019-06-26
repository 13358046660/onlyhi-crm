package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Size;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/9/8.
 */
public class SaveAppraiseRequest extends BaseRequest {
    @NotBlank(message = "课程uuid不能为空！")
    private String courseUuid;
    @Range(min = 1, max = 5, message = "star参数非法！")
    private int star;
    private String classAppraiseStarUuids;   //多个以,分割
    @Size(max = 140,message = "描述限140字！")
    private String remark;
    /**评价人uuid*/
    private String appraiserUuid;

    @Override
    public String toString() {
        return "SaveAppraiseRequest{" +
                "courseUuid='" + courseUuid + '\'' +
                ", star=" + star +
                ", classAppraiseStarUuids='" + classAppraiseStarUuids + '\'' +
                ", remark='" + remark + '\'' +
                ", appraiserUuid='" + appraiserUuid + '\'' +
                '}';
    }

    public String getAppraiserUuid() {
        return appraiserUuid;
    }

    public void setAppraiserUuid(String appraiserUuid) {
        this.appraiserUuid = appraiserUuid;
    }

    public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getClassAppraiseStarUuids() {
        return classAppraiseStarUuids;
    }

    public void setClassAppraiseStarUuids(String classAppraiseStarUuids) {
        this.classAppraiseStarUuids = classAppraiseStarUuids;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
