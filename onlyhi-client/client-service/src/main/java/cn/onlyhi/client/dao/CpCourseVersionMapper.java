package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.CpCourseVersion;

public interface CpCourseVersionMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(CpCourseVersion record);

    CpCourseVersion selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CpCourseVersion record);

    CpCourseVersion selectByUuid(String uuid);

    int updateByUuidSelective(CpCourseVersion record);

}