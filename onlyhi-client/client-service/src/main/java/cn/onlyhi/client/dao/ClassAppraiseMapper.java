package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.ClassAppraise;

public interface ClassAppraiseMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(ClassAppraise record);

    ClassAppraise selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ClassAppraise record);

    ClassAppraise selectByUuid(String uuid);

    int updateByUuidSelective(ClassAppraise record);

    ClassAppraise findByCourseUuid(String courseUuid);
}