package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/5.
 */
public class SaveAvatarRequest extends BaseRequest {
    @NotBlank(message = "头像链接不能为空！")
    private String imagePath; //头像链接
    @NotBlank(message = "头像名称不能为空！")
    private String imageName;   //头像名称

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    @Override
    public String toString() {
        return "saveAvatarRequest{" +
                "imagePath='" + imagePath + '\'' +
                ", imageName='" + imageName + '\'' +
                "} " + super.toString();
    }
}
