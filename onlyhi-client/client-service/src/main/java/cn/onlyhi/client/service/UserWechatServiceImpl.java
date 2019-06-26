package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.UserWechatMapper;
import cn.onlyhi.client.po.UserWechat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class UserWechatServiceImpl implements UserWechatService {

    @Autowired
    private UserWechatMapper userWechatMapper;

    @Override
    public int save(UserWechat userWechat) {
        return userWechatMapper.insertSelective(userWechat);
    }

    @Override
    public UserWechat findByUuid(String uuid) {
        return userWechatMapper.selectByUuid(uuid);
    }

    @Override
    public int update(UserWechat userWechat) {
        return userWechatMapper.updateByUuidSelective(userWechat);
    }

    @Override
    public UserWechat findUserWechatByUid(String uid) {
        return userWechatMapper.findUserWechatByUid(uid);
    }

    @Override
    public UserWechat findUserWechatByPhone(String phone) {
        return userWechatMapper.findUserWechatByPhone(phone);
    }

}