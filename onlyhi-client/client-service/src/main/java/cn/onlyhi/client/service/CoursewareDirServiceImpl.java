package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.CoursewareDirMapper;
import cn.onlyhi.client.po.CoursewareDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class CoursewareDirServiceImpl implements CoursewareDirService {

    @Autowired
    private CoursewareDirMapper coursewareDirMapper;

    @Override
    public int save(CoursewareDir coursewareDir) {
        return coursewareDirMapper.insertSelective(coursewareDir);
    }

    @Override
    public CoursewareDir findByUuid(String uuid) {
        return coursewareDirMapper.selectByUuid(uuid);
    }

    @Override
    public int update(CoursewareDir coursewareDir) {
        return coursewareDirMapper.updateByUuidSelective(coursewareDir);
    }

    @Override
    public List<CoursewareDir> findByTeacherUuid(String teacherUuid) {
        return coursewareDirMapper.findByTeacherUuid(teacherUuid);
    }

    @Override
    public List<String> findMatchCoursewareDirNameByTeacherUuid(String coursewareDirName, String teacherUuid) {
        return coursewareDirMapper.findMatchCoursewareDirNameByTeacherUuid(coursewareDirName, teacherUuid);
    }

    @Override
    public CoursewareDir findByCoursewareDirName(String teacherUuid, String coursewareDirName) {
        return coursewareDirMapper.findByCoursewareDirName(teacherUuid, coursewareDirName);
    }

}