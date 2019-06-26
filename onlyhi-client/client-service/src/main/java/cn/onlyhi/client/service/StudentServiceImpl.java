package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.StudentMapper;
import cn.onlyhi.client.po.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/3/18.
 */
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public int save(Student student) {
        return studentMapper.insertSelective(student);
    }

    @Override
    public Student findByLeadsUuid(String LeadsUuid) {
        return studentMapper.findByLeadsUuid(LeadsUuid);
    }

    @Override
    public int updateGradeByPhone(String grade, String phone) {
        return studentMapper.updateGradeByPhone(grade,phone);
    }

}
