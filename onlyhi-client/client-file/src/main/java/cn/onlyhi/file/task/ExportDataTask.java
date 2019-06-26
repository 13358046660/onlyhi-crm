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
 * @Author wqz
 * <p>
 * Created by wqz on 2018/4/12.
 */
@Component
@Configuration
@EnableScheduling // 启用定时任务
public class ExportDataTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportDataTask.class);

    @Scheduled(cron = "0 0 2 * * ? ")    //每天凌晨2:00点执行
    public void exportDataTask() {
        LOGGER.info("exportDataTask...start...");
        String exportDate = DateFormatUtils.format(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000), "yyyy-MM-dd");
        String url = "http://localhost:20027/client/task/exportData?exportDate=" + exportDate + "&downloadFlag=false";
        LOGGER.info("url={}", url);
        String result = HttpUtil.sendGet(url);
        LOGGER.info("result={}", result);
        LOGGER.info("exportDataTask...end...");
    }
}
