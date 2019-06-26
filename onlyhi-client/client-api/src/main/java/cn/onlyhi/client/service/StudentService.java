package cn.onlyhi.client.service;


import cn.onlyhi.client.po.Student;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/3/18.
 */
public interface StudentService {

    int save(Student student);

    /**
     * 根据LeadsUuid获取学生信息
     *
     * @param LeadsUuid
     * @return
     */
    Student findByLeadsUuid(String LeadsUuid);
    /**
     * 学生端根据手机号更新年级
     *
     * @param grade student
     * @return
     */
    int updateGradeByPhone(String grade,String phone);
}
