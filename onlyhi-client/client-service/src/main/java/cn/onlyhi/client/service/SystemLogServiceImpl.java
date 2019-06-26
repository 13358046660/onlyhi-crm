package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.SystemLogMapper;
import cn.onlyhi.client.po.SystemLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class SystemLogServiceImpl implements SystemLogService {

	@Autowired
	private SystemLogMapper systemLogMapper;

	@Override
	public int save(SystemLog systemLog) {
		return systemLogMapper.insertSelective(systemLog);
	}

	@Override
	public SystemLog findByUuid(String uuid) {
		return systemLogMapper.selectByUuid(uuid);
	}

	@Override
	public int update(SystemLog systemLog) {
		return systemLogMapper.updateByUuidSelective(systemLog);
	}

}