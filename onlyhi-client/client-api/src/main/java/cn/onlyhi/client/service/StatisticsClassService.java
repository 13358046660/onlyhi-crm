package cn.onlyhi.client.service;


import cn.onlyhi.client.dto.StatisticsClassVo;
import cn.onlyhi.client.po.ClassStatistics;

import java.util.List;
import java.util.Map;

public interface StatisticsClassService {

    boolean statisticsClass(String today);
    List<ClassStatistics> findClass(StatisticsClassVo vo);
    int updateClassById(Integer id);
    Map<Map<String, String>, List<Map<String, String>>> exportClass(String courseStartDate);
}