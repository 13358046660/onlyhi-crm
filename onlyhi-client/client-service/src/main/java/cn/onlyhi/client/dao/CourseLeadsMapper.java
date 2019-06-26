package cn.onlyhi.client.dao;

import cn.onlyhi.client.dto.CourseDto;
import cn.onlyhi.client.dto.CourseLeadsDto;
import cn.onlyhi.client.po.CourseLeads;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseLeadsMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(CourseLeads record);

    CourseLeads selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CourseLeads record);

    CourseLeads selectByUuid(String uuid);

    int updateByUuidSelective(CourseLeads record);

    /**
     * 查询同一课程的学生列表
     *
     * @param courseUuid
     * @return
     */
    List<CourseLeadsDto> findByCourseUuid(String courseUuid);

    /**
     * 查询leads的一对多课程数
     *
     * @param leadsUuid
     * @return
     */
    int countNoEndCourseByLeadsUuid(String leadsUuid);

    /**
     * 查询leads的一对多课程列表
     *
     * @param leadsUuid
     * @param startSize
     * @param pageSize
     * @return
     */
    List<CourseDto> findNoEndCourseByLeadsUuid(@Param("leadsUuid") String leadsUuid, @Param("startSize") int startSize, @Param("pageSize") Integer pageSize);
}