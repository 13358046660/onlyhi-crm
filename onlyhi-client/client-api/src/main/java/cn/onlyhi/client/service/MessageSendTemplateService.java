package cn.onlyhi.client.service;


import cn.onlyhi.client.po.MessageSendTemplate;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/13.
 */
public interface MessageSendTemplateService {

    MessageSendTemplate findByPurpose(int purpose);

    int save(MessageSendTemplate messageSendTemplate);

    MessageSendTemplate findByUuid(String uuid);

    /**
     * 根据uuid更新
     * @param messageSendTemplate
     * @return
     */
    int update(MessageSendTemplate messageSendTemplate);
}
