package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.LeadsExt;

public interface LeadsExtMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(LeadsExt record);

    LeadsExt selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LeadsExt record);

    int updateLeadsExtByLeadsUuidSelective(LeadsExt leadsExt);

    /**
     * 根据leadsUuid查询
     *
     * @param leadsUuid
     * @return
     */
    LeadsExt findLeadsExtByLeadsUuid(String leadsUuid);
}