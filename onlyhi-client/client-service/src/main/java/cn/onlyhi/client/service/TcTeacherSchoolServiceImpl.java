package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.TcTeacherSchoolMapper;
import cn.onlyhi.client.po.TcTeacherSchool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class TcTeacherSchoolServiceImpl implements TcTeacherSchoolService {

	@Autowired
	private TcTeacherSchoolMapper tcTeacherSchoolMapper;

	@Override
	public int save(TcTeacherSchool tcTeacherSchool) {
		return tcTeacherSchoolMapper.insertSelective(tcTeacherSchool);
	}

	@Override
	public TcTeacherSchool findByUuid(String uuid) {
		return tcTeacherSchoolMapper.selectByUuid(uuid);
	}

	@Override
	public int update(TcTeacherSchool tcTeacherSchool) {
		return tcTeacherSchoolMapper.updateByUuidSelective(tcTeacherSchool);
	}

	@Override
	public List<TcTeacherSchool> findAll() {
		return tcTeacherSchoolMapper.findAll();
	}

}