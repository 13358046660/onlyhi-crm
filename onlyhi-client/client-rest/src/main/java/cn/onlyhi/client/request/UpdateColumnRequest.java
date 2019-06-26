package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/20.
 */
public class UpdateColumnRequest extends BaseRequest {
    @NotBlank(message = "appColumnUuid不能为空！")
    private String appColumnUuid;
    private String title;
    private String image;
    private String link;
    private Integer sort;

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getAppColumnUuid() {
        return appColumnUuid;
    }

    public void setAppColumnUuid(String appColumnUuid) {
        this.appColumnUuid = appColumnUuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "UpdateColumnRequest{" +
                "appColumnUuid='" + appColumnUuid + '\'' +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", link='" + link + '\'' +
                ", sort=" + sort +
                "} " + super.toString();
    }
}
