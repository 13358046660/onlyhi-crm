package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.TcTeacherSchool;

import java.util.List;

public interface TcTeacherSchoolMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TcTeacherSchool record);

    TcTeacherSchool selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TcTeacherSchool record);

    TcTeacherSchool selectByUuid(String uuid);

    int updateByUuidSelective(TcTeacherSchool record);

    List<TcTeacherSchool> findAll();
}