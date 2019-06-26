package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.CoursePriceBaiduCode;

import java.util.List;

public interface CoursePriceBaiduCodeMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(CoursePriceBaiduCode record);

    CoursePriceBaiduCode selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CoursePriceBaiduCode record);

    CoursePriceBaiduCode selectByUuid(String uuid);

    int updateByUuidSelective(CoursePriceBaiduCode record);


    List<CoursePriceBaiduCode> findBaiduCodeByType(String type);

}