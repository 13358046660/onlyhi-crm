package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/30.
 */
public class TeacherFileRequest extends BaseRequest{
    @NotNull(message = "purpose不能为空！")
    private Integer purpose;
    @NotBlank(message = "fileAddress不能为空！")
    private String fileAddress;
    @NotBlank(message = "fileOriginalName不能为空！")
    private String fileOriginalName;

    public Integer getPurpose() {
        return purpose;
    }

    public void setPurpose(Integer purpose) {
        this.purpose = purpose;
    }

    public String getFileAddress() {
        return fileAddress;
    }

    public void setFileAddress(String fileAddress) {
        this.fileAddress = fileAddress;
    }

    public String getFileOriginalName() {
        return fileOriginalName;
    }

    public void setFileOriginalName(String fileOriginalName) {
        this.fileOriginalName = fileOriginalName;
    }

    @Override
    public String toString() {
        return "TeacherFileRequest{" +
                "purpose=" + purpose +
                ", fileAddress='" + fileAddress + '\'' +
                ", fileOriginalName='" + fileOriginalName + '\'' +
                "} " + super.toString();
    }
}
