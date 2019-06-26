package cn.onlyhi.client.dao;

import cn.onlyhi.client.dto.CoursePriceTypeDto;
import cn.onlyhi.client.po.CpCoursePrice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CpCoursePriceMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(CpCoursePrice record);

    CpCoursePrice selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CpCoursePrice record);

    CpCoursePrice selectByUuid(String uuid);

    int updateByUuidSelective(CpCoursePrice record);

    List<CoursePriceTypeDto> findCoursePriceTypeListByActivityType(String activityType);

    List<CpCoursePrice> findCoursePriceListByActivityTypeAndType(@Param("activityType") String activityType, @Param("type") String type);
}