package cn.onlyhi.client.service;

import cn.onlyhi.client.dto.UserDto;
import cn.onlyhi.client.po.User;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface UserService {

    int save(User user);

    User findByUuid(String uuid);

    /**
     * 根据uuid更新
     *
     * @param user
     * @return
     */
    int update(User user);

    /**
     * 根据登录名查询用户
     *
     * @param loginName
     * @return
     */
    User findByLoginName(String loginName);

    /**
     * 查找教学监课用户uuid
     *
     * @return
     */
    List<UserDto> findAllMonitor();

    /**
     * 拥有教学监课角色的userUuid列表
     *
     * @return
     */
    List<UserDto> findQCMonitor();

    List<User> findByRoleAlias(String alias);
    /**
     * 根据用户id查对应角色名
     *
     * @return
     */
    UserDto findUserByUuid(String curUuid);
}