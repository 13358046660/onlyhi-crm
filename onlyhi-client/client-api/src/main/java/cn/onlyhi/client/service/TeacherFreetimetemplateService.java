package cn.onlyhi.client.service;

import cn.onlyhi.client.po.TeacherFreetimetemplate;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface TeacherFreetimetemplateService {

	int save(TeacherFreetimetemplate teacherFreetimetemplate);

	TeacherFreetimetemplate findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param teacherFreetimetemplate
     * @return
     */
	int update(TeacherFreetimetemplate teacherFreetimetemplate);


	List<TeacherFreetimetemplate> findByTeacherUuidAndWeekOfMonth(String teacherUuid, int weekOfMonth);

	int deleteAndSave(String teacherUuid, List<TeacherFreetimetemplate> teacherFreetimetemplateList);
}