package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.CoursewareImageMapper;
import cn.onlyhi.client.po.CoursewareImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class CoursewareImageServiceImpl implements CoursewareImageService {

    @Autowired
    private CoursewareImageMapper coursewareImageMapper;

    @Override
    public int save(CoursewareImage coursewareImage) {
        return coursewareImageMapper.insertSelective(coursewareImage);
    }

    @Override
    public CoursewareImage findByUuid(String uuid) {
        return coursewareImageMapper.selectByUuid(uuid);
    }

    @Override
    public int update(CoursewareImage coursewareImage) {
        return coursewareImageMapper.updateByUuidSelective(coursewareImage);
    }

    @Override
    public int batchSave(List<CoursewareImage> coursewareImageList) {
        return coursewareImageMapper.batchInsertSelective(coursewareImageList);
    }

    @Override
    public List<CoursewareImage> findNoMd5() {
        return coursewareImageMapper.findNoMd5();
    }

}