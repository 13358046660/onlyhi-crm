package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.ClassAppraiseStarMapper;
import cn.onlyhi.client.po.ClassAppraiseStar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class ClassAppraiseStarServiceImpl implements ClassAppraiseStarService {

    @Autowired
    private ClassAppraiseStarMapper classAppraiseStarMapper;

    @Override
    public int save(ClassAppraiseStar classAppraiseStar) {
        return classAppraiseStarMapper.insertSelective(classAppraiseStar);
    }

    @Override
    public ClassAppraiseStar findByUuid(String uuid) {
        return classAppraiseStarMapper.selectByUuid(uuid);
    }

    @Override
    public int update(ClassAppraiseStar classAppraiseStar) {
        return classAppraiseStarMapper.updateByUuidSelective(classAppraiseStar);
    }

    @Override
    public List<ClassAppraiseStar> findByStar(int star) {
        return classAppraiseStarMapper.findByStar(star);
    }

    @Override
    public List<String> findContentsByUuids(List<String> classAppraiseStarUuidList) {
        return classAppraiseStarMapper.findContentsByUuids(classAppraiseStarUuidList);
    }

}