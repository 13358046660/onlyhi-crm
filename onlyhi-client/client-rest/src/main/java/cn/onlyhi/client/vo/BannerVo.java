package cn.onlyhi.client.vo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/19.
 */
public class BannerVo {
    private String bannerUuid;
    private String image;
    private String link;
    private int sort;

    public String getBannerUuid() {
        return bannerUuid;
    }

    public void setBannerUuid(String bannerUuid) {
        this.bannerUuid = bannerUuid;
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

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "BannerVo{" +
                "bannerUuid='" + bannerUuid + '\'' +
                ", image='" + image + '\'' +
                ", link='" + link + '\'' +
                ", sort=" + sort +
                '}';
    }
}
