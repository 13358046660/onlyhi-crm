package cn.onlyhi.client.dao;


import cn.onlyhi.client.po.CoursewareImage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CoursewareImageMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(CoursewareImage record);

    CoursewareImage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CoursewareImage record);

    CoursewareImage selectByUuid(String uuid);

    int updateByUuidSelective(CoursewareImage record);

    List<CoursewareImage> selectByCoursewareId(String coursewareId);

    /**
     * 批量插入
     *
     * @param coursewareImageList
     * @return
     */
    int batchInsertSelective(@Param("list") List<CoursewareImage> coursewareImageList);

    List<CoursewareImage> findNoMd5();
}