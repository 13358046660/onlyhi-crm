package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.TcTeacherDate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TcTeacherDateMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TcTeacherDate record);

    TcTeacherDate selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TcTeacherDate record);

    int batchSave(@Param("list") List<TcTeacherDate> tcTeacherDateList);

    List<String> findFreetimePeriodByTeacherUuidAndFreetimeDate(@Param("teacherUuid") String teacherUuid, @Param("freetimeDate") String freetimeDate);

    int deleteByTeacherUuidAndTcDate(@Param("teacherUuid") String teacherUuid, @Param("freetimeMonth") String freetimeMonth);
}