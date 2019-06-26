package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.UserDeviceInformationMapper;
import cn.onlyhi.client.po.UserDeviceInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class UserDeviceInformationServiceImpl implements UserDeviceInformationService {

	@Autowired
	private UserDeviceInformationMapper userDeviceInformationMapper;

	@Override
	public int save(UserDeviceInformation userDeviceInformation) {
		return userDeviceInformationMapper.insertSelective(userDeviceInformation);
	}

}