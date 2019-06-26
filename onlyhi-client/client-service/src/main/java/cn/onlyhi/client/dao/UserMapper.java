package cn.onlyhi.client.dao;

import cn.onlyhi.client.dto.UserDto;
import cn.onlyhi.client.po.User;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    User selectByUuid(String uuid);

    int updateByUuidSelective(User record);

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