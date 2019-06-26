package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.LeadsSignRecord;

public interface LeadsSignRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(LeadsSignRecord record);

    LeadsSignRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LeadsSignRecord record);
}