package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.CoursewareDir;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CoursewareDirMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(CoursewareDir record);

    CoursewareDir selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CoursewareDir record);

    CoursewareDir selectByUuid(String uuid);

    int updateByUuidSelective(CoursewareDir record);

    List<CoursewareDir> findByTeacherUuid(String teacherUuid);

    List<String> findMatchCoursewareDirNameByTeacherUuid(@Param("coursewareDirName") String coursewareDirName, @Param("teacherUuid") String teacherUuid);

    CoursewareDir findByCoursewareDirName(@Param("teacherUuid") String teacherUuid, @Param("coursewareDirName") String coursewareDirName);
}