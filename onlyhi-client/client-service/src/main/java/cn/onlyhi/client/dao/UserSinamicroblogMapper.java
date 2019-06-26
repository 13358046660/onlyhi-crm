package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.UserSinamicroblog;

public interface UserSinamicroblogMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(UserSinamicroblog record);

    UserSinamicroblog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserSinamicroblog record);

    UserSinamicroblog selectByUuid(String uuid);

    int updateByUuidSelective(UserSinamicroblog record);

    /**
     * 根据uid查找新浪微博用户信息
     *
     * @param uid
     * @return
     */
    UserSinamicroblog findUserSinamicroblogByUid(String uid);

    UserSinamicroblog findUserSinamicroblogByPhone(String phone);

}