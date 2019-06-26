package cn.onlyhi.client.util;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackages = { "cn.onlyhi.client" })
@ImportResource(value = { "classpath:/application-context-core.xml" })
@EnableScheduling
public class MultiThreadConfig {
}
