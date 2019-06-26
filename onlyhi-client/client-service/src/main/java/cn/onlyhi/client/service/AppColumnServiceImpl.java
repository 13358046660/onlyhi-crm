package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.AppColumnMapper;
import cn.onlyhi.client.po.AppColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class AppColumnServiceImpl implements AppColumnService {

	@Autowired
	private AppColumnMapper appColumnMapper;

	@Override
	public int save(AppColumn appColumn) {
		return appColumnMapper.insertSelective(appColumn);
	}

	@Override
	public AppColumn findByUuid(String uuid) {
		return appColumnMapper.selectByUuid(uuid);
	}

	@Override
	public int update(AppColumn appColumn) {
		return appColumnMapper.updateByUuidSelective(appColumn);
	}

	@Override
	public List<AppColumn> findAllAppColumnByDeviceType(int deviceType) {
		return appColumnMapper.findAllAppColumnByDeviceType(deviceType);
	}

}