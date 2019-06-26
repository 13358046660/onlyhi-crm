package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.UserNeteaseimMapper;
import cn.onlyhi.client.po.UserNeteaseim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class UserNeteaseimServiceImpl implements UserNeteaseimService {

	@Autowired
	private UserNeteaseimMapper userNeteaseimMapper;

	@Override
	public int save(UserNeteaseim userNeteaseim) {
		return userNeteaseimMapper.insertSelective(userNeteaseim);
	}

	@Override
	public UserNeteaseim findByUuid(String uuid) {
		return userNeteaseimMapper.selectByUuid(uuid);
	}

	@Override
	public int update(UserNeteaseim userNeteaseim) {
		return userNeteaseimMapper.updateByUuidSelective(userNeteaseim);
	}

	@Override
	public UserNeteaseim findByUserUuid(String userUuid) {
		return userNeteaseimMapper.findByUserUuid(userUuid);
	}

	@Override
	public int updateByUserUuid(UserNeteaseim userNeteaseim) {
		return userNeteaseimMapper.updateByUserUuid(userNeteaseim);
	}

}