package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.CourseLeadsMapper;
import cn.onlyhi.client.dto.CourseDto;
import cn.onlyhi.client.dto.CourseLeadsDto;
import cn.onlyhi.client.po.CourseLeads;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class CourseLeadsServiceImpl implements CourseLeadsService {

	@Autowired
	private CourseLeadsMapper courseLeadsMapper;

	@Override
	public int save(CourseLeads courseLeads) {
		return courseLeadsMapper.insertSelective(courseLeads);
	}

	@Override
	public CourseLeads findByUuid(String uuid) {
		return courseLeadsMapper.selectByUuid(uuid);
	}

	@Override
	public int update(CourseLeads courseLeads) {
		return courseLeadsMapper.updateByUuidSelective(courseLeads);
	}

	@Override
	public List<CourseLeadsDto> findByCourseUuid(String courseUuid) {
		return courseLeadsMapper.findByCourseUuid(courseUuid);
	}

	@Override
	public int countNoEndCourseByLeadsUuid(String leadsUuid) {
		return courseLeadsMapper.countNoEndCourseByLeadsUuid(leadsUuid);
	}

	@Override
	public List<CourseDto> findNoEndCourseByLeadsUuid(String leadsUuid, Integer pageNo, Integer pageSize) {
		int startSize = (pageNo - 1) * pageSize;
		return courseLeadsMapper.findNoEndCourseByLeadsUuid(leadsUuid,startSize,pageSize);
	}

}