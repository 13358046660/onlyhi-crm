package cn.onlyhi.client.dao;


import cn.onlyhi.client.po.PushMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PushMessageMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(PushMessage record);

    PushMessage selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PushMessage record);

    PushMessage selectByUuid(String uuid);

    int updateByUuidSelective(PushMessage record);

    PushMessage findByLeadsUuid(String leadsUuid);

    List<PushMessage> findByLeadsUuids(@Param("leadsUuids") List<String> leadsUuids);

    PushMessage findByLeadsUuidOrDeviceToken(@Param("leadsUuid") String leadsUuid, @Param("deviceToken") String deviceToken);
}