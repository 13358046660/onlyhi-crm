package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.CoursePriceBaiduCodeMapper;
import cn.onlyhi.client.po.CoursePriceBaiduCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class CoursePriceBaiduCodeServiceImpl implements CoursePriceBaiduCodeService {

	@Autowired
	private CoursePriceBaiduCodeMapper coursePriceBaiduCodeMapper;

	@Override
	public int save(CoursePriceBaiduCode coursePriceBaiduCode) {
		return coursePriceBaiduCodeMapper.insertSelective(coursePriceBaiduCode);
	}

	@Override
	public CoursePriceBaiduCode findByUuid(String uuid) {
		return coursePriceBaiduCodeMapper.selectByUuid(uuid);
	}

	@Override
	public int update(CoursePriceBaiduCode coursePriceBaiduCode) {
		return coursePriceBaiduCodeMapper.updateByUuidSelective(coursePriceBaiduCode);
	}
	@Override
	public List<CoursePriceBaiduCode> findBaiduCodeByType(String type) {
		return coursePriceBaiduCodeMapper.findBaiduCodeByType(type);
	}
}