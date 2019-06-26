package cn.onlyhi.client.service;


import cn.onlyhi.client.dao.CoursewareDirMapper;
import cn.onlyhi.client.dao.CoursewareImageMapper;
import cn.onlyhi.client.dao.CoursewareMapper;
import cn.onlyhi.client.dto.CoursewareDto;
import cn.onlyhi.client.po.Courseware;
import cn.onlyhi.client.po.CoursewareDir;
import cn.onlyhi.client.po.CoursewareImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/3/24.
 */
@Service
public class CoursewareServiceImpl implements CoursewareService {

    @Autowired
    private CoursewareMapper coursewareMapper;
    @Autowired
    private CoursewareImageMapper coursewareImageMapper;
    @Autowired
    private CoursewareDirMapper coursewareDirMapper;

    @Override
    public int save(Courseware courseware) {
        return coursewareMapper.insertSelective(courseware);
    }

    @Override
    public Courseware findByUuid(String uuid) {
        return coursewareMapper.selectByUuid(uuid);
    }

    @Override
    public int update(Courseware courseware) {
        return coursewareMapper.updateByUuidSelective(courseware);
    }

    @Override
    public List<Courseware> findByTeacherUuid(String teacherUuid) {
        return coursewareMapper.selectByTeacherUuid(teacherUuid);
    }

    @Override
    public List<Courseware> findByTeacherUuid(String teacherUuid, String orderBy, String orderSort) {
        return coursewareMapper.findByTeacherUuid(teacherUuid, orderBy, orderSort);
    }

    @Override
    public long countSysCourseware(Map paramMap) {
        return coursewareMapper.selectSysCoursewareCount(paramMap);
    }

    @Override
    public List<CoursewareDto> findSysCourseware(Map paramMap, int pageNo, int pageSize) {
        int startSize = (pageNo - 1) * pageSize;
        return coursewareMapper.selectSysCourseware(paramMap, startSize, pageSize);
    }

    @Override
    public List<CoursewareImage> findImageByCoursewareId(String coursewareId) {
        return coursewareImageMapper.selectByCoursewareId(coursewareId);
    }

    @Override
    public int deleteCourseware(String coursewareId) {
        return coursewareMapper.deleteCourseware(coursewareId);
    }

    @Override
    public long countTeacherCouresewares(Map paramMap) {
        return coursewareMapper.selectTeacherCoursewareCount(paramMap);
    }

    @Override
    public List<String> findByCoursewareName(String fileName, String suffix, String teacherUuid) {
        return coursewareMapper.findLikeByCoursewareNameAndSuffix(fileName, suffix, teacherUuid);
    }

    @Override
    public List<Courseware> findByCoursewareDirUuid(String coursewareDirUuid, String orderBy, String orderSort) {
        return coursewareMapper.findByCoursewareDirUuid(coursewareDirUuid, orderBy, orderSort);
    }

    @Override
    public List<Courseware> findRootByTeacherUuid(String teacherUuid, String orderBy, String orderSort) {
        return coursewareMapper.findRootByTeacherUuid(teacherUuid, orderBy, orderSort);
    }

    @Override
    public int updateCoursewareDir(String coursewareUuid, String coursewareDirUuid) {
        return coursewareMapper.updateCoursewareDir(coursewareUuid, coursewareDirUuid);
    }

    @Transactional
    @Override
    public int deleteDirAndCourseware(String coursewareDirUuid) {
        //删除课件目录
        CoursewareDir coursewareDir = new CoursewareDir();
        coursewareDir.setCoursewareDirUuid(coursewareDirUuid);
        coursewareDir.setStatus(0);
        coursewareDirMapper.updateByUuidSelective(coursewareDir);
        //删除课件目录下文件
        int i = coursewareMapper.deleteByCoursewareDirUuid(coursewareDirUuid);
        return i;
    }

    @Override
    public List<Courseware> findLikeByCoursewareName(String teacherUuid, String coursewareName) {
        return coursewareMapper.findLikeByCoursewareName(teacherUuid, coursewareName);
    }

    @Override
    public Courseware findByCoursewareName(String teacherUuid, String coursewareName) {
        return coursewareMapper.findByCoursewareName(teacherUuid, coursewareName);
    }

    @Override
    public List<CoursewareDto> findTeacherCouresewaresByCreateTime(String startDate, String endDate) {
        return coursewareMapper.findTeacherCouresewaresByCreateTime(startDate, endDate);
    }

    @Override
    public int selectByMD5(String teacherUuid, String streamMD5) {
        return coursewareMapper.selectByMD5(teacherUuid, streamMD5);
    }

}
