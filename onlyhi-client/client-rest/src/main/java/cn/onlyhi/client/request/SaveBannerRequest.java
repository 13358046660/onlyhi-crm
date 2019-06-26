package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/20.
 */
public class SaveBannerRequest extends BaseRequest {
    private String image;
    private String link;
    private Integer sort;

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

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "SaveBannerRequest{" +
                "image='" + image + '\'' +
                ", link='" + link + '\'' +
                ", sort=" + sort +
                "} " + super.toString();
    }
}
