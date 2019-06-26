package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.ClassTeacherAppraise;

public interface ClassTeacherAppraiseMapper {

	ClassTeacherAppraise findByCourseUuid(String courseUuid);

	int insertSelective(ClassTeacherAppraise classTeacherAppraise);

	ClassTeacherAppraise selectByUuid(String classTeacherAppraiseUuid);

}
