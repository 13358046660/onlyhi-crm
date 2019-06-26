package cn.onlyhi.client.service;

import cn.onlyhi.client.po.Role;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface RoleService {

    int save(Role role);

    Role findByUuid(String uuid);

    /**
     * 根据uuid更新
     *
     * @param role
     * @return
     */
    int update(Role role);

    /**
     * 查询用户角色列表
     *
     * @param userUuid
     * @return 角色alias列表
     */
    List<String> findByUserUuid(String userUuid);
}