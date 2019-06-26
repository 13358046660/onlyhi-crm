package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.UserQqMapper;
import cn.onlyhi.client.po.UserQq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class UserQqServiceImpl implements UserQqService {

    @Autowired
    private UserQqMapper userQqMapper;

    @Override
    public int save(UserQq userQq) {
        return userQqMapper.insertSelective(userQq);
    }

    @Override
    public UserQq findByUuid(String uuid) {
        return userQqMapper.selectByUuid(uuid);
    }

    @Override
    public int update(UserQq userQq) {
        return userQqMapper.updateByUuidSelective(userQq);
    }

    @Override
    public UserQq findUserQqByUid(String uid) {
        return userQqMapper.findUserQqByUid(uid);
    }

    @Override
    public UserQq findUserQQByPhone(String phone) {
        return userQqMapper.findUserQQByPhone(phone);
    }

}