package cn.onlyhi.client.vo;

public class AppInfoVo {
    private String url;
    private String version;
    private String remark;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "AppInfoVo{" +
                "url='" + url + '\'' +
                ", version='" + version + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}