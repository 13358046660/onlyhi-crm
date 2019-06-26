package cn.onlyhi.client.baidustaging;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/26.
 */
@Component
@ConfigurationProperties(prefix = "baiduStaging")
public class BaiduStagingConfig {
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
