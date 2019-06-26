package cn.onlyhi.client.dao;


import cn.onlyhi.client.po.Patriarch;
import org.apache.ibatis.annotations.Param;

public interface PatriarchMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(Patriarch record);

    Patriarch selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Patriarch record);

    Patriarch selectByUuid(String uuid);

    int updateByUuidSelective(Patriarch record);

    Patriarch findByPhone(String phone);

    int updatePasswordByUuid(@Param("patriarchUuid") String patriarchUuid, @Param("newPassword") String newPassword);

}