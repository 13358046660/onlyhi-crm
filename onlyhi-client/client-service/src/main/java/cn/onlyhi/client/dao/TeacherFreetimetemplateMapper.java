package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.TeacherFreetimetemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TeacherFreetimetemplateMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(TeacherFreetimetemplate record);

    TeacherFreetimetemplate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TeacherFreetimetemplate record);

    TeacherFreetimetemplate selectByUuid(String uuid);

    int updateByUuidSelective(TeacherFreetimetemplate record);

    List<TeacherFreetimetemplate> findByTeacherUuidAndWeekOfMonth(@Param("teacherUuid") String teacherUuid, @Param("weekOfMonth") int weekOfMonth);

    int deleteByTeacherUuid(String teacherUuid);

    int batchSave(@Param("list") List<TeacherFreetimetemplate> teacherFreetimetemplateList);
}