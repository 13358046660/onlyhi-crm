package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.AgoraCallLog;

public interface AgoraCallLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(AgoraCallLog record);

    AgoraCallLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AgoraCallLog record);

    AgoraCallLog selectByUuid(String uuid);

    int updateByUuidSelective(AgoraCallLog record);
}