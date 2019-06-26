package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.ClassAppraiseStar;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClassAppraiseStarMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(ClassAppraiseStar record);

    ClassAppraiseStar selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ClassAppraiseStar record);

    ClassAppraiseStar selectByUuid(String uuid);

    int updateByUuidSelective(ClassAppraiseStar record);

    List<ClassAppraiseStar> findByStar(int star);

    List<String> findContentsByUuids(@Param("list") List<String> classAppraiseStarUuidList);
}