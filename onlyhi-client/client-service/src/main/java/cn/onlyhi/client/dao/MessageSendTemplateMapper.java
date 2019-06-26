package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.MessageSendTemplate;

public interface MessageSendTemplateMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(MessageSendTemplate record);

    MessageSendTemplate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MessageSendTemplate record);

    MessageSendTemplate selectByUuid(String uuid);

    int updateByUuidSelective(MessageSendTemplate record);

    MessageSendTemplate findByPurpose(int purpose);
}