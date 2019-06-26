package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.RoleMapper;
import cn.onlyhi.client.po.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleMapper roleMapper;

	@Override
	public int save(Role role) {
		return roleMapper.insertSelective(role);
	}

	@Override
	public Role findByUuid(String uuid) {
		return roleMapper.selectByUuid(uuid);
	}

	@Override
	public int update(Role role) {
		return roleMapper.updateByUuidSelective(role);
	}

	@Override
	public List<String> findByUserUuid(String userUuid) {
		return roleMapper.findByUserUuid(userUuid);
	}

}