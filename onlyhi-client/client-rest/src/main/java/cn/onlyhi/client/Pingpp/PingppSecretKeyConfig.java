package cn.onlyhi.client.Pingpp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/26.
 */
@Component
@ConfigurationProperties(prefix = "pingppSecretKey")
public class PingppSecretKeyConfig {
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
