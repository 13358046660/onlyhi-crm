package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.TeacherRecommendMapper;
import cn.onlyhi.client.po.TeacherRecommend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class TeacherRecommendServiceImpl implements TeacherRecommendService {

    @Autowired
    private TeacherRecommendMapper teacherRecommendMapper;

    @Override
    public int save(TeacherRecommend teacherRecommend) {
        return teacherRecommendMapper.insertSelective(teacherRecommend);
    }

    @Override
    public TeacherRecommend findByUuid(String uuid) {
        return teacherRecommendMapper.selectByUuid(uuid);
    }

    @Override
    public int update(TeacherRecommend teacherRecommend) {
        return teacherRecommendMapper.updateByUuidSelective(teacherRecommend);
    }

    @Override
    public List<TeacherRecommend> findAllTeacherRecommendByDeviceType(int deviceType) {
        return teacherRecommendMapper.findAllTeacherRecommendByDeviceType(deviceType);
    }

}