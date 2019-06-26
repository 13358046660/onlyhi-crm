package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.TeacherFreetimetemplateMapper;
import cn.onlyhi.client.po.TeacherFreetimetemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class TeacherFreetimetemplateServiceImpl implements TeacherFreetimetemplateService {

    @Autowired
    private TeacherFreetimetemplateMapper teacherFreetimetemplateMapper;

    @Override
    public int save(TeacherFreetimetemplate teacherFreetimetemplate) {
        return teacherFreetimetemplateMapper.insertSelective(teacherFreetimetemplate);
    }

    @Override
    public TeacherFreetimetemplate findByUuid(String uuid) {
        return teacherFreetimetemplateMapper.selectByUuid(uuid);
    }

    @Override
    public int update(TeacherFreetimetemplate teacherFreetimetemplate) {
        return teacherFreetimetemplateMapper.updateByUuidSelective(teacherFreetimetemplate);
    }

    @Override
    public List<TeacherFreetimetemplate> findByTeacherUuidAndWeekOfMonth(String teacherUuid, int weekOfMonth) {
        return teacherFreetimetemplateMapper.findByTeacherUuidAndWeekOfMonth(teacherUuid, weekOfMonth);
    }

    @Transactional
    @Override
    public int deleteAndSave(String teacherUuid, List<TeacherFreetimetemplate> teacherFreetimetemplateList) {
        int i = teacherFreetimetemplateMapper.deleteByTeacherUuid(teacherUuid);
        if (teacherFreetimetemplateList != null && teacherFreetimetemplateList.size() > 0) {
            i += teacherFreetimetemplateMapper.batchSave(teacherFreetimetemplateList);
        }
        return i;
    }

}