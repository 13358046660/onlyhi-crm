package cn.onlyhi.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.onlyhi.client.dao.ClassAppraiseMapper;
import cn.onlyhi.client.dao.ClassTeacherAppraiseMapper;
import cn.onlyhi.client.po.ClassTeacherAppraise;

/**
 * @Author ywj
 * <p>
 * Created by ywj on 2018/1/24.
 */
@Service
public class ClassTeacherAppraiseServiceImpl implements ClassTeacherAppraiseService {
	@Autowired
	private ClassTeacherAppraiseMapper classTeacherAppraiseMapper;

	@Override
	public ClassTeacherAppraise findByCourseUuid(String CourseUuid) {
		return classTeacherAppraiseMapper.findByCourseUuid(CourseUuid);
	}

	@Override
	public int save(ClassTeacherAppraise classTeacherAppraise) {
		return classTeacherAppraiseMapper.insertSelective(classTeacherAppraise);
	}

	@Override
	public ClassTeacherAppraise findByUuid(String classTeacherAppraiseUuid) {
		return classTeacherAppraiseMapper.selectByUuid(classTeacherAppraiseUuid);
	}

}
