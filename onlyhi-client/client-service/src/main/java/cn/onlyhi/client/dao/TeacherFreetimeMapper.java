package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.TeacherFreetime;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TeacherFreetimeMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(TeacherFreetime record);

    TeacherFreetime selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TeacherFreetime record);

    TeacherFreetime selectByUuid(String uuid);

    int updateByUuidSelective(TeacherFreetime record);

    int batchSave(@Param("list") List<TeacherFreetime> teacherFreetimeList);

}