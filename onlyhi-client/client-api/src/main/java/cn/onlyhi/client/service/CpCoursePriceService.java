package cn.onlyhi.client.service;


import cn.onlyhi.client.dto.CoursePriceTypeDto;
import cn.onlyhi.client.po.CpCoursePrice;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface CpCoursePriceService {

    int save(CpCoursePrice cpCoursePrice);

    CpCoursePrice findByUuid(String uuid);

    /**
     * 根据uuid更新
     *
     * @param cpCoursePrice
     * @return
     */
    int update(CpCoursePrice cpCoursePrice);

    /**
     * 获取课时包类型
     *
     * @param activityType
     * @return
     */
    List<CoursePriceTypeDto> findCoursePriceTypeListByActivityType(String activityType);

    /**
     * 根据活动类型和课时包类型查询课时包列表
     *
     * @param activityType
     * @param type
     * @return
     */
    List<CpCoursePrice> findCoursePriceListByActivityTypeAndType(String activityType, String type);


}