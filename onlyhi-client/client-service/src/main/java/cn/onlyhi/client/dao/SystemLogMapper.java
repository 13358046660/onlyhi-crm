package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.SystemLog;

public interface SystemLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(SystemLog record);

    SystemLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SystemLog record);

    SystemLog selectByUuid(String uuid);

    int updateByUuidSelective(SystemLog record);
}