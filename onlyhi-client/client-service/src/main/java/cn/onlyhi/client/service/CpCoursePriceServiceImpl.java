package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.CpCoursePriceMapper;
import cn.onlyhi.client.dto.CoursePriceTypeDto;
import cn.onlyhi.client.po.CpCoursePrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class CpCoursePriceServiceImpl implements CpCoursePriceService {

	@Autowired
	private CpCoursePriceMapper cpCoursePriceMapper;

	@Override
	public int save(CpCoursePrice cpCoursePrice) {
		return cpCoursePriceMapper.insertSelective(cpCoursePrice);
	}

	@Override
	public CpCoursePrice findByUuid(String uuid) {
		return cpCoursePriceMapper.selectByUuid(uuid);
	}

	@Override
	public int update(CpCoursePrice cpCoursePrice) {
		return cpCoursePriceMapper.updateByUuidSelective(cpCoursePrice);
	}
	@Override
	public List<CoursePriceTypeDto> findCoursePriceTypeListByActivityType(String activityType) {
		return cpCoursePriceMapper.findCoursePriceTypeListByActivityType(activityType);
	}

	@Override
	public List<CpCoursePrice> findCoursePriceListByActivityTypeAndType(String activityType, String type) {
		return cpCoursePriceMapper.findCoursePriceListByActivityTypeAndType(activityType,type);
	}
}