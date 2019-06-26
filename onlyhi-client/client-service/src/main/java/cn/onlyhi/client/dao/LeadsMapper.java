package cn.onlyhi.client.dao;

import cn.onlyhi.client.dto.LeadsExtendEntity;
import cn.onlyhi.client.po.Leads;
import org.apache.ibatis.annotations.Param;

public interface LeadsMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(Leads record);

    Leads selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Leads record);

    Leads selectByUuid(String uuid);

    int updateByUuidSelective(Leads record);

    /**
     * 根据手机号查询leads信息
     *
     * @param phone
     * @return
     */
    Leads selectByPhone(String phone);

    int updateSexByUuid(@Param("uuid") String uuid, @Param("sex") Integer sex);

    int updateGradeByUuid(@Param("uuid") String uuid, @Param("grade") String grade);

    int updateExamAreaByUuid(@Param("uuid") String uuid, @Param("examArea") String examArea);

    int updateSubjectByUuid(@Param("leadsUuid") String leadsUuid, @Param("subject") String subject);
    LeadsExtendEntity selectLeadsByPhone(String phone);
}