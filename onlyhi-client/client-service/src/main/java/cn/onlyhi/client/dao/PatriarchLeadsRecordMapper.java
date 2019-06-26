package cn.onlyhi.client.dao;


import cn.onlyhi.client.po.PatriarchLeadsRecord;

public interface PatriarchLeadsRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(PatriarchLeadsRecord record);

    PatriarchLeadsRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PatriarchLeadsRecord record);

    PatriarchLeadsRecord selectByUuid(String uuid);

    int updateByUuidSelective(PatriarchLeadsRecord record);

    PatriarchLeadsRecord findByPhone(String phone);

    int deleteByPatriarchUuid(String patriarchUuid);
}