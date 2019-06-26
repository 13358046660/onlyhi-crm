package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.MessageSendRecord;

public interface MessageSendRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(MessageSendRecord record);

    MessageSendRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MessageSendRecord record);
}