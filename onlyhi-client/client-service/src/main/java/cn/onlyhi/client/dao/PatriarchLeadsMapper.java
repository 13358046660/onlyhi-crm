package cn.onlyhi.client.dao;


import cn.onlyhi.client.po.PatriarchLeads;

public interface PatriarchLeadsMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(PatriarchLeads record);

    PatriarchLeads selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PatriarchLeads record);

    PatriarchLeads selectByUuid(String uuid);

    int updateByUuidSelective(PatriarchLeads record);

    PatriarchLeads findByPhone(String phone);

    PatriarchLeads findByLeadsUuid(String leadsUuid);

    int deleteByPatriarchUuid(String patriarchUuid);
}