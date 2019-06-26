package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.TechnicalAssistance;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TechnicalAssistanceMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(TechnicalAssistance record);

    TechnicalAssistance selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TechnicalAssistance record);

    TechnicalAssistance selectByUuid(String uuid);

    int updateByUuidSelective(TechnicalAssistance record);

    List<TechnicalAssistance> findByAssistanceUuidAndAssistanceStatus(@Param("assistanceUuid") String assistanceUuid, @Param("assistanceStatus") int assistanceStatus);
}