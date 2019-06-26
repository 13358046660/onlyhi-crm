package cn.onlyhi.client.service;

import cn.onlyhi.client.dto.MessageReturnDto;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/8/14.
 */
public interface MessageService {
    /**
     * 发送短信息
     *
     * @param phone   多个号码使用","分割
     * @param message 发送的消息内容
     * @return
     */
    MessageReturnDto sendMessage(String phone, String message) throws Exception;

    /**
     * 发送异常短信息
     *
     * @param phone   多个号码使用","分割
     * @param message 发送的消息内容
     * @return
     */
    MessageReturnDto sendExceptionMessage(String phone, String courseUuid, String message) throws Exception;
}
