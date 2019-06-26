package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.TeacherRecommend;

import java.util.List;

public interface TeacherRecommendMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(TeacherRecommend record);

    TeacherRecommend selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TeacherRecommend record);

    TeacherRecommend selectByUuid(String uuid);

    int updateByUuidSelective(TeacherRecommend record);

    List<TeacherRecommend> findAllTeacherRecommendByDeviceType(int deviceType);

}