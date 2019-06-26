package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.LeadsLog;

public interface LeadsLogMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(LeadsLog record);

    LeadsLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LeadsLog record);
}