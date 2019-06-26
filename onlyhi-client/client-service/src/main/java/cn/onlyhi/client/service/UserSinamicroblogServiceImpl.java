package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.UserSinamicroblogMapper;
import cn.onlyhi.client.po.UserSinamicroblog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class UserSinamicroblogServiceImpl implements UserSinamicroblogService {

    @Autowired
    private UserSinamicroblogMapper userSinamicroblogMapper;

    @Override
    public int save(UserSinamicroblog userSinamicroblog) {
        return userSinamicroblogMapper.insertSelective(userSinamicroblog);
    }

    @Override
    public UserSinamicroblog findByUuid(String uuid) {
        return userSinamicroblogMapper.selectByUuid(uuid);
    }

    @Override
    public int update(UserSinamicroblog userSinamicroblog) {
        return userSinamicroblogMapper.updateByUuidSelective(userSinamicroblog);
    }

    @Override
    public UserSinamicroblog findUserSinamicroblogByUid(String uid) {
        return userSinamicroblogMapper.findUserSinamicroblogByUid(uid);
    }

    @Override
    public UserSinamicroblog findUserSinamicroblogByPhone(String phone) {
        return userSinamicroblogMapper.findUserSinamicroblogByPhone(phone);
    }

}