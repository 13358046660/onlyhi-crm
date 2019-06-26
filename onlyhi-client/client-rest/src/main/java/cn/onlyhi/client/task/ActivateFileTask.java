package cn.onlyhi.client.task;

import cn.onlyhi.client.config.YmlMyConfig;
import cn.onlyhi.common.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/11/28.
 */
@Component
@Configuration
@EnableScheduling // 启用定时任务
public class ActivateFileTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateFileTask.class);

    @Autowired
    private YmlMyConfig ymlMyConfig;


    @Scheduled(cron = "0 01 01 * * ? ")
    public void classStatisticsTask() {
        LOGGER.info("classStatisticsTask...start...");
        String baseUrl = ymlMyConfig.getUploadAddress();
        //String url = baseUrl + "/client/task/classStatistics";
        //测试使用mp4视频提交这个statisticsStudentRecordDataNew，线上用statisticsStudentRecordData包括兼容1.0（解析还在用flac）
        // 2.0可以不用解析成flac，前端只用了m4a
        //测试oss 切换到statisticsStudentRecordDataOss
        String url = baseUrl + "/client/task/statisticsStudentRecordData";
        LOGGER.info("url={}", url);
        String result = HttpUtil.sendGet(url);
        LOGGER.info("result={}", result);
        LOGGER.info("classStatisticsTask...end...");
    }
}
