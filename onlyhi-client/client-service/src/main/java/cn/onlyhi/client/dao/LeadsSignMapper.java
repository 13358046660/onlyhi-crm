package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.LeadsSign;

public interface LeadsSignMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(LeadsSign record);

    LeadsSign selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LeadsSign record);

    LeadsSign findByLeadsUuid(String leadsUuid);
}