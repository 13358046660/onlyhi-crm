package cn.onlyhi.client.service;


import cn.onlyhi.client.po.UserWechat;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface UserWechatService {

    int save(UserWechat userWechat);

    UserWechat findByUuid(String uuid);

    /**
     * 根据uuid更新
     *
     * @param userWechat
     * @return
     */
    int update(UserWechat userWechat);
    /**
     * 根据uid查找微信用户信息
     *
     * @param uid
     * @return
     */
    UserWechat findUserWechatByUid(String uid);

    /**
     * 查找phone当前绑定的微信信息
     *
     * @param phone
     * @return
     */
    UserWechat findUserWechatByPhone(String phone);



}