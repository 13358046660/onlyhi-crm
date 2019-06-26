package cn.onlyhi.client.service;

import cn.onlyhi.client.po.CoursewareDir;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface CoursewareDirService {

	int save(CoursewareDir coursewareDir);

	CoursewareDir findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param coursewareDir
     * @return
     */
	int update(CoursewareDir coursewareDir);


	List<CoursewareDir> findByTeacherUuid(String teacherUuid);

	List<String> findMatchCoursewareDirNameByTeacherUuid(String coursewareDirName, String teacherUuid);

	CoursewareDir findByCoursewareDirName(String teacherUuid, String coursewareDirName);
}