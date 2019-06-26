package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.Role;

import java.util.List;

public interface RoleMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(Role record);

    Role selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Role record);

    Role selectByUuid(String uuid);

    int updateByUuidSelective(Role record);

    /**
     * 查询用户角色列表
     *
     * @param userUuid
     * @return
     */
    List<String> findByUserUuid(String userUuid);
}