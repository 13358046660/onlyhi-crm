package cn.onlyhi.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/9/7.
 */
@Component
@ConfigurationProperties(prefix = "umengpush")
public class UmengPushConfig {
    private String iosAppkey;
    private String iosAppMasterSecret;
    private String androidAppkey;
    private String androidAppMasterSecret;

    public String getIosAppkey() {
        return iosAppkey;
    }

    public void setIosAppkey(String iosAppkey) {
        this.iosAppkey = iosAppkey;
    }

    public String getIosAppMasterSecret() {
        return iosAppMasterSecret;
    }

    public void setIosAppMasterSecret(String iosAppMasterSecret) {
        this.iosAppMasterSecret = iosAppMasterSecret;
    }

    public String getAndroidAppkey() {
        return androidAppkey;
    }

    public void setAndroidAppkey(String androidAppkey) {
        this.androidAppkey = androidAppkey;
    }

    public String getAndroidAppMasterSecret() {
        return androidAppMasterSecret;
    }

    public void setAndroidAppMasterSecret(String androidAppMasterSecret) {
        this.androidAppMasterSecret = androidAppMasterSecret;
    }
}
