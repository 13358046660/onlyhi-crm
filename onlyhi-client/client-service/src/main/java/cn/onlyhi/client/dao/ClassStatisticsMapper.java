package cn.onlyhi.client.dao;

import cn.onlyhi.client.dto.StatisticsClassVo;
import cn.onlyhi.client.po.ClassStatistics;
import cn.onlyhi.client.po.StatisticsClass;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClassStatisticsMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(ClassStatistics record);

    ClassStatistics selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ClassStatistics record);
    List<StatisticsClass> statisticsClass(String today);
    int batchSaveClass(List<ClassStatistics> classStatistics);
    List<ClassStatistics> findClass(StatisticsClassVo vo);
    int updateClassById(Integer id);
    List<ClassStatistics> exportClass(String courseStartDate);
}