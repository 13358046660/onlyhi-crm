package cn.onlyhi.client.service;


import cn.onlyhi.client.po.TeacherRecommend;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface TeacherRecommendService {

	int save(TeacherRecommend teacherRecommend);

	TeacherRecommend findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param teacherRecommend
     * @return
     */
	int update(TeacherRecommend teacherRecommend);

	List<TeacherRecommend> findAllTeacherRecommendByDeviceType(int deviceType);

}