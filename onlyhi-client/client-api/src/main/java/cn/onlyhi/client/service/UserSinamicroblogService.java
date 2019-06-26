package cn.onlyhi.client.service;


import cn.onlyhi.client.po.UserSinamicroblog;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface UserSinamicroblogService {

    int save(UserSinamicroblog userSinamicroblog);

    UserSinamicroblog findByUuid(String uuid);

    /**
     * 根据uuid更新
     *
     * @param userSinamicroblog
     * @return
     */
    int update(UserSinamicroblog userSinamicroblog);

    /**
     * 根据uid查找新浪微博用户信息
     *
     * @param uid
     * @return
     */
    UserSinamicroblog findUserSinamicroblogByUid(String uid);

    /**
     * 查找phone当前绑定的微博信息
     *
     * @param phone
     * @return
     */
    UserSinamicroblog findUserSinamicroblogByPhone(String phone);


}