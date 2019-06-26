package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.TcPayGrade;

public interface TcPayGradeMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TcPayGrade record);

    TcPayGrade selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TcPayGrade record);
}