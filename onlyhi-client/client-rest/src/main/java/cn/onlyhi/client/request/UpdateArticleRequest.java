package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/20.
 */
public class UpdateArticleRequest extends BaseRequest {
    @NotBlank(message = "articleUuid不能为空！")
    private String articleUuid;
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

    public String getArticleUuid() {
        return articleUuid;
    }

    public void setArticleUuid(String articleUuid) {
        this.articleUuid = articleUuid;
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
        return "UpdateArticleRequest{" +
                "articleUuid='" + articleUuid + '\'' +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", link='" + link + '\'' +
                ", sort=" + sort +
                "} " + super.toString();
    }
}
