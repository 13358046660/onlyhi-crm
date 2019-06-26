package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/3/12.
 */
public class SaveAgoraCallLogRequest extends BaseRequest {
    @NotBlank(message = "courseUuid不能为空！")
    private String courseUuid;
    @NotBlank(message = "agoraUuid不能为空！")
    private String agoraUuid;
    @NotBlank(message = "agoraType不能为空！")
    private String agoraType;
    @NotBlank(message = "agoraCode不能为空！")
    private String agoraCode;

    public String getCourseUuid() {
        return courseUuid;
    }

    public void setCourseUuid(String courseUuid) {
        this.courseUuid = courseUuid;
    }

    public String getAgoraUuid() {
        return agoraUuid;
    }

    public void setAgoraUuid(String agoraUuid) {
        this.agoraUuid = agoraUuid;
    }

    public String getAgoraType() {
        return agoraType;
    }

    public void setAgoraType(String agoraType) {
        this.agoraType = agoraType;
    }

    public String getAgoraCode() {
        return agoraCode;
    }

    public void setAgoraCode(String agoraCode) {
        this.agoraCode = agoraCode;
    }

    @Override
    public String toString() {
        return "SaveAgoraCallLogRequest{" +
                "courseUuid='" + courseUuid + '\'' +
                ", agoraUuid='" + agoraUuid + '\'' +
                ", agoraType='" + agoraType + '\'' +
                ", agoraCode='" + agoraCode + '\'' +
                "} " + super.toString();
    }
}
