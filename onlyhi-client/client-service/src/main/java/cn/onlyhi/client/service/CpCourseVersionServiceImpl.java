package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.CpCourseVersionMapper;
import cn.onlyhi.client.po.CpCourseVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class CpCourseVersionServiceImpl implements CpCourseVersionService {

	@Autowired
	private CpCourseVersionMapper cpCourseVersionMapper;

	@Override
	public int save(CpCourseVersion cpCourseVersion) {
		return cpCourseVersionMapper.insertSelective(cpCourseVersion);
	}

	@Override
	public CpCourseVersion findByUuid(String uuid) {
		return cpCourseVersionMapper.selectByUuid(uuid);
	}

	@Override
	public int update(CpCourseVersion cpCourseVersion) {
		return cpCourseVersionMapper.updateByUuidSelective(cpCourseVersion);
	}

}