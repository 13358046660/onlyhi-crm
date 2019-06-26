package cn.onlyhi.client.service;

import cn.onlyhi.client.dto.CourseDto;
import cn.onlyhi.client.dto.CourseLeadsDto;
import cn.onlyhi.client.po.CourseLeads;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface CourseLeadsService {

	int save(CourseLeads courseLeads);

	CourseLeads findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param courseLeads
     * @return
     */
	int update(CourseLeads courseLeads);

	/**
	 * 查询同一课程的学生列表
	 * @param courseUuid
	 * @return
     */
	List<CourseLeadsDto> findByCourseUuid(String courseUuid);

	/**
	 * 查询leads的一对多课程数
	 * @param leadsUuid
	 * @return
     */
	int countNoEndCourseByLeadsUuid(String leadsUuid);

	/**
	 * 查询leads的一对多课程列表
	 * @param leadsUuid
	 * @param pageNo
	 * @param pageSize
     * @return
     */
	List<CourseDto> findNoEndCourseByLeadsUuid(String leadsUuid, Integer pageNo, Integer pageSize);
}