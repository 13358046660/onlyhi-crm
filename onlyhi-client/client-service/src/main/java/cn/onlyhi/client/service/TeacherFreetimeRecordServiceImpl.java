package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.TeacherFreetimeRecordMapper;
import cn.onlyhi.client.po.TeacherFreetimeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class TeacherFreetimeRecordServiceImpl implements TeacherFreetimeRecordService {

    @Autowired
    private TeacherFreetimeRecordMapper teacherFreetimeRecordMapper;

    @Override
    public int save(TeacherFreetimeRecord teacherFreetimeRecord) {
        return teacherFreetimeRecordMapper.insertSelective(teacherFreetimeRecord);
    }

    @Override
    public TeacherFreetimeRecord findByTeacherUuidAndFreetimeMonth(String teacherUuid, String freetimeMonth) {
        return teacherFreetimeRecordMapper.findByTeacherUuidAndFreetimeMonth(teacherUuid, freetimeMonth);
    }

}