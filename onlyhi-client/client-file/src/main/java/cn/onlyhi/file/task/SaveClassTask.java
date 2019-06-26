package cn.onlyhi.file.task;
import cn.onlyhi.common.util.HttpUtil;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 每天凌晨1:00导出当天汇总的课耗
 *
 * @Author wqz
 * <p>
 * Created by wangqianzhi on 2018/10/30.
 */
@Component
@Configuration
@EnableScheduling // 启用定时任务
public class SaveClassTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveClassTask.class);

    @Scheduled(cron = "0 10 2 * * ? ")
    public void saveClassTask() {
        LOGGER.info("exportClassTask...start...");
        String exportDate = DateFormatUtils.format(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000), "yyyy-MM-dd");
        String url = "http://localhost:20027/client/class/exportClass?exportDate=" + exportDate + "&downloadFlag=false";
        LOGGER.info("url={}", url);
        String result = HttpUtil.sendGet(url);
        LOGGER.info("result={}", result);
        LOGGER.info("exportClassTask...end...");
    }
}
