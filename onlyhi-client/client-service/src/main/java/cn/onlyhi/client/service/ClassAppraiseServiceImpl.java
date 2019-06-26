package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.ClassAppraiseMapper;
import cn.onlyhi.client.po.ClassAppraise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class ClassAppraiseServiceImpl implements ClassAppraiseService {

    @Autowired
    private ClassAppraiseMapper classAppraiseMapper;

    @Override
    public int save(ClassAppraise classAppraise) {
        return classAppraiseMapper.insertSelective(classAppraise);
    }

    @Override
    public ClassAppraise findByUuid(String uuid) {
        return classAppraiseMapper.selectByUuid(uuid);
    }

    @Override
    public int update(ClassAppraise classAppraise) {
        return classAppraiseMapper.updateByUuidSelective(classAppraise);
    }

    @Override
    public ClassAppraise findByCourseUuid(String courseUuid) {
        return classAppraiseMapper.findByCourseUuid(courseUuid);
    }

}