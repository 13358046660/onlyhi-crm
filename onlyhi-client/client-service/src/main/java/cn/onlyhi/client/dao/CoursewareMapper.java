package cn.onlyhi.client.dao;


import cn.onlyhi.client.dto.CoursewareDto;
import cn.onlyhi.client.po.Courseware;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CoursewareMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(Courseware record);

    Courseware selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Courseware record);

    Courseware selectByUuid(String uuid);

    int updateByUuidSelective(Courseware record);

    List<Courseware> selectByTeacherUuid(String teacherUuid);

    int deleteCourseware(String coursewareId);

    List<CoursewareDto> selectSysCourseware(@Param("paramMap") Map paramMap, @Param("startSize") int startSize, @Param("pageSize") int pageSize);

    long selectSysCoursewareCount(@Param("paramMap") Map paramMap);

    long selectTeacherCoursewareCount(@Param("paramMap") Map paramMap);

    List<String> findLikeByCoursewareNameAndSuffix(@Param("fileName") String fileName, @Param("suffix") String suffix, @Param("teacherUuid") String teacherUuid);

    List<Courseware> findByTeacherUuid(@Param("teacherUuid") String teacherUuid, @Param("orderBy") String orderBy, @Param("orderSort") String orderSort);

    List<Courseware> findByCoursewareDirUuid(@Param("coursewareDirUuid") String coursewareDirUuid, @Param("orderBy") String orderBy, @Param("orderSort") String orderSort);

    List<Courseware> findRootByTeacherUuid(@Param("teacherUuid") String teacherUuid, @Param("orderBy") String orderBy, @Param("orderSort") String orderSort);

    int updateCoursewareDir(@Param("coursewareUuid") String coursewareUuid, @Param("coursewareDirUuid") String coursewareDirUuid);

    int deleteByCoursewareDirUuid(String coursewareDirUuid);

    List<Courseware> findLikeByCoursewareName(@Param("teacherUuid") String teacherUuid, @Param("coursewareName") String coursewareName);

    Courseware findByCoursewareName(@Param("teacherUuid") String teacherUuid, @Param("coursewareName") String coursewareName);

    List<CoursewareDto> findTeacherCouresewaresByCreateTime(@Param("startDate") String startDate, @Param("endDate") String endDate);

    int selectByMD5(@Param("teacherUuid") String teacherUuid, @Param("streamMD5") String streamMD5);
}