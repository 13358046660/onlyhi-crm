package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/1/2.
 */
public class TechnicalAssistanceUuidRequest extends BaseRequest{
    @NotBlank(message = "technicalAssistanceUuid不能为空！")
    private String technicalAssistanceUuid;

    public String getTechnicalAssistanceUuid() {
        return technicalAssistanceUuid;
    }

    public void setTechnicalAssistanceUuid(String technicalAssistanceUuid) {
        this.technicalAssistanceUuid = technicalAssistanceUuid;
    }

    @Override
    public String toString() {
        return "TechnicalAssistanceUuidRequest{" +
                "technicalAssistanceUuid='" + technicalAssistanceUuid + '\'' +
                "} " + super.toString();
    }
}
