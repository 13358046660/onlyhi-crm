package cn.onlyhi.client.dao;

import cn.onlyhi.client.dto.TeacherDto;
import cn.onlyhi.client.po.TcTeacher;
import org.apache.ibatis.annotations.Param;

public interface TcTeacherMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TcTeacher record);

    TcTeacher selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TcTeacher record);

    TcTeacher selectByUuid(String uuid);

    int updateByUuidSelective(TcTeacher record);

    /**
     * 根据手机号查询教师信息
     *
     * @param phone
     * @return
     */
    TcTeacher selectByPhone(String phone);

    /**
     * 修改密码
     *
     * @param uuid
     * @param password
     * @return
     */
    int updatePasswordByUuid(@Param("uuid") String uuid, @Param("password") String password);

    TeacherDto findInfoByUuid(String teacherUuid);

}