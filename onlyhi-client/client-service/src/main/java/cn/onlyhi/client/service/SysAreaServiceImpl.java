package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.SysAreaMapper;
import cn.onlyhi.client.po.SysArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class SysAreaServiceImpl implements SysAreaService {

	@Autowired
	private SysAreaMapper sysAreaMapper;

	@Override
	public int save(SysArea sysArea) {
		return sysAreaMapper.insertSelective(sysArea);
	}

	@Override
	public List<SysArea> findByAreaLevel(Integer areaLevel) {
		return sysAreaMapper.findByAreaLevel(areaLevel);
	}

	@Override
	public List<SysArea> findByAreaLevelAndParentCode(Integer areaLevel, String parentCode) {
		return sysAreaMapper.findByAreaLevelAndParentCode(areaLevel,parentCode);
	}

	@Override
	public SysArea findByAreaCode(String areaCode) {
		return sysAreaMapper.findByAreaCode(areaCode);
	}

}