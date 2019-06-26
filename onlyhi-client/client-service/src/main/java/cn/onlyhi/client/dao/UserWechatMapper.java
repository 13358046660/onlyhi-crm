package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.UserWechat;

public interface UserWechatMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(UserWechat record);

    UserWechat selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserWechat record);

    UserWechat selectByUuid(String uuid);

    int updateByUuidSelective(UserWechat record);

    /**
     * 根据uid查找微信用户信息
     *
     * @param uid
     * @return
     */
    UserWechat findUserWechatByUid(String uid);

    UserWechat findUserWechatByPhone(String phone);

}