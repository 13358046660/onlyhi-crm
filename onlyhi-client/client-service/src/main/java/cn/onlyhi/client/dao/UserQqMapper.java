package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.UserQq;

public interface UserQqMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(UserQq record);

    UserQq selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserQq record);

    UserQq selectByUuid(String uuid);

    int updateByUuidSelective(UserQq record);

    /**
     * 根据uid查找Qq用户信息
     *
     * @param uid
     * @return
     */
    UserQq findUserQqByUid(String uid);

    UserQq findUserQQByPhone(String phone);

}