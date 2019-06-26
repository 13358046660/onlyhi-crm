package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.TeacherFreetimeMapper;
import cn.onlyhi.client.po.TeacherFreetime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class TeacherFreetimeServiceImpl implements TeacherFreetimeService {

    @Autowired
    private TeacherFreetimeMapper teacherFreetimeMapper;

    @Override
    public int save(TeacherFreetime teacherFreetime) {
        return teacherFreetimeMapper.insertSelective(teacherFreetime);
    }

    @Override
    public TeacherFreetime findByUuid(String uuid) {
        return teacherFreetimeMapper.selectByUuid(uuid);
    }

    @Override
    public int update(TeacherFreetime teacherFreetime) {
        return teacherFreetimeMapper.updateByUuidSelective(teacherFreetime);
    }

    @Override
    public int batchSave(List<TeacherFreetime> teacherFreetimeList) {
        return teacherFreetimeMapper.batchSave(teacherFreetimeList);
    }

}