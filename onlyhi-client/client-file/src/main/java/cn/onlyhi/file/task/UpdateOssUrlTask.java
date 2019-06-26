package cn.onlyhi.file.task;
import cn.onlyhi.common.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 每天凌晨3:00,根据oss key 生成的url，覆盖原url，对应的手动上传/www/clientfile/uploadPath/recordDir目录下的音频到阿里云服务器
 * 更新线上class_room的voice_url(m4a音频文件url),flac_voice_url(flac格式音频文件url),track_url(轨迹文件url)
 * 更新测试class_room的voice_url(m4a音频文件url),flac_voice_url(flac格式音频文件url),track_url(轨迹文件url),mp4_video_url(mp4视频文件下载路径)
 *
 * @Author wqz
 * <p>
 * Created by wqz on 2018/7/3.
 */
@Component
@Configuration
@EnableScheduling // 启用定时任务
public class UpdateOssUrlTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateOssUrlTask.class);
    //每天凌晨3:00点执行
    //@Scheduled(cron = "0 0 4 * * ? ")
    public void exportDataTask() {
        LOGGER.info("updateOssUrlTask...start...");
        String url = "http://localhost:20027/client/oss/updateUrl";
        LOGGER.info("url={}", url);
        String result = HttpUtil.sendPost(url);
        LOGGER.info("result={}", result);
        LOGGER.info("updateOssUrlTask...end...");
    }
}
