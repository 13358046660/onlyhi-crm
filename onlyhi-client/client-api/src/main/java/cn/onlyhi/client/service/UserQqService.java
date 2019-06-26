package cn.onlyhi.client.service;


import cn.onlyhi.client.po.UserQq;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface UserQqService {

    int save(UserQq userQq);

    UserQq findByUuid(String uuid);

    /**
     * 根据uuid更新
     *
     * @param userQq
     * @return
     */
    int update(UserQq userQq);

    /**
     * 根据uid查找qq用户信息
     *
     * @param uid
     * @return
     */
    UserQq findUserQqByUid(String uid);

    /**
     * 查找phone当前绑定的qq信息
     *
     * @param phone
     * @return
     */
    UserQq findUserQQByPhone(String phone);


}