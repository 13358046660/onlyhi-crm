package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.SysEnumMapper;
import cn.onlyhi.client.po.SysEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class SysEnumServiceImpl implements SysEnumService {

	@Autowired
	private SysEnumMapper sysEnumMapper;

	@Override
	public int save(SysEnum sysEnum) {
		return sysEnumMapper.insertSelective(sysEnum);
	}

	@Override
	public SysEnum findByUuid(String uuid) {
		return sysEnumMapper.selectByUuid(uuid);
	}

	@Override
	public int update(SysEnum sysEnum) {
		return sysEnumMapper.updateByUuidSelective(sysEnum);
	}

	@Override
	public List<SysEnum> findByEnumType(String enumType) {
		return sysEnumMapper.findByEnumType(enumType);
	}

}