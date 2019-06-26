package cn.onlyhi.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/8/16.
 */
@SpringBootApplication
@ImportResource({"classpath:dubbo-consumer.xml"})
public class WebsocketApplication extends WebMvcConfigurerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketApplication.class);

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(WebsocketApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        Map<String, Object> defaultMap = new HashMap<>();
        defaultMap.put("spring.profiles.active", "dev");
        app.setDefaultProperties(defaultMap);
        app.run(args);
        LOGGER.debug("容器启动成功!");
    }

    /**
     * 解决跨域问题
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
    }
}
