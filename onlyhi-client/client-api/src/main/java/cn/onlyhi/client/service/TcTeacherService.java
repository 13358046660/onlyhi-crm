package cn.onlyhi.client.service;

import cn.onlyhi.client.dto.SaveTeacherInfoDto;
import cn.onlyhi.client.dto.TeacherDto;
import cn.onlyhi.client.po.TcTeacher;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface TcTeacherService {

    int save(TcTeacher tcTeacher);

    TcTeacher findByUuid(String uuid);

    /**
     * 根据uuid更新
     *
     * @param tcTeacher
     * @return
     */
    int update(TcTeacher tcTeacher);

    /**
     * 根据手机号查询教师信息
     *
     * @param phone
     * @return
     */
    TcTeacher findTeacherByPhone(String phone);

    /**
     * 修改密码
     *
     * @param uuid
     * @param password
     * @return
     */
    int updatePasswordByUuid(String uuid, String password);

    TeacherDto findInfoByUuid(String teacherUuid);

    int infoSave(SaveTeacherInfoDto saveTeacherInfoDto);

}