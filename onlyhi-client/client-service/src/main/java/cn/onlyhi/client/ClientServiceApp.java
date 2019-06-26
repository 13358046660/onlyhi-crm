package cn.onlyhi.client;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/***
 * 启动类
 */

public class ClientServiceApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientServiceApp.class);

    public static void main(String[] args) {
        if (StringUtils.isEmpty(System.getProperty("spring.profiles.active"))) {
                System.setProperty("spring.profiles.active", "test");
        }
        LOGGER.info("ENV={}", System.getProperty("spring.profiles.active"));
        com.alibaba.dubbo.container.Main.main(args);
    }
}

