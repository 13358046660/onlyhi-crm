package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.TeacherFreetimeRecord;
import org.apache.ibatis.annotations.Param;

public interface TeacherFreetimeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(TeacherFreetimeRecord record);

    TeacherFreetimeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TeacherFreetimeRecord record);

    TeacherFreetimeRecord findByTeacherUuidAndFreetimeMonth(@Param("teacherUuid") String teacherUuid,@Param("freetimeMonth") String freetimeMonth);
}