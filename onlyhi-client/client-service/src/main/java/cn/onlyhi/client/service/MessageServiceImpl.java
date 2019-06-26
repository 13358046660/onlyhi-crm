package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.MessageDao;
import cn.onlyhi.client.dto.MessageReturnDto;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/8/14.
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageDao messageDao;
    @Value("${env}")
    private String env;

    @Override
    public MessageReturnDto sendMessage(String phone, String message) throws Exception {
        return messageDao.sendMessage(phone, message);
    }

    @Override
    public MessageReturnDto sendExceptionMessage(String phone, String courseUuid, String message) throws Exception {
        String timeStr = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
        message = "{" + env + "} " + timeStr + ":" + "course_uuid:" + courseUuid + message;
        return messageDao.sendMessage(phone, message);
    }
}
