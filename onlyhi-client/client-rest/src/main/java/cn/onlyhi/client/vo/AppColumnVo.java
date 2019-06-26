package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/19.
 */
public class AppColumnVo {
    private String appColumnUuid;
    private String title;
    private String image;
    private String link;
    private int sort;

    public String getAppColumnUuid() {
        return appColumnUuid;
    }

    public void setAppColumnUuid(String appColumnUuid) {
        this.appColumnUuid = appColumnUuid;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
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
        return "AppColumnVo{" +
                "appColumnUuid='" + appColumnUuid + '\'' +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", link='" + link + '\'' +
                ", sort=" + sort +
                '}';
    }
}
