package cn.onlyhi.client.service;

import cn.onlyhi.client.po.ClassTeacherAppraise;

/**
 * @Author ywj
 * <p>
 * Created by ywj on 2018/1/24.
 */
public interface ClassTeacherAppraiseService {

	ClassTeacherAppraise findByCourseUuid(String CourseUuid);

	int save(ClassTeacherAppraise classTeacherAppraise);

	ClassTeacherAppraise findByUuid(String classTeacherAppraiseUuid);

}
