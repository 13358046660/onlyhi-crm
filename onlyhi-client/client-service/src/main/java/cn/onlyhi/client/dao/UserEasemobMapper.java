package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.UserEasemob;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserEasemobMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(UserEasemob record);

    UserEasemob selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserEasemob record);

    UserEasemob selectByUuid(String uuid);

    int updateByUuidSelective(UserEasemob record);

    UserEasemob findByUserUuid(String userUuid);

    List<UserEasemob> findByEasemobUsernameList(@Param("list") List<String> easemobUsernameList, @Param("startSize") int startSize, @Param("pageSize") int pageSize);

    int countByEasemobUsernameList(@Param("list") List<String> imUserNameList);
}