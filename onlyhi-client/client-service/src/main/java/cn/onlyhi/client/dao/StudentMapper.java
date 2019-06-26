package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.Student;
import org.apache.ibatis.annotations.Param;

public interface StudentMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(Student record);

    Student selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Student record);

    Student findByLeadsUuid(String leadsUuid);

    int updateGradeByPhone(@Param("grade") String grade, @Param("phone")String phone);
}