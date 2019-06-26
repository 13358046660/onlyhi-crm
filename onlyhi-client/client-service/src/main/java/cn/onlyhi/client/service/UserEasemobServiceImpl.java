package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.UserEasemobMapper;
import cn.onlyhi.client.po.UserEasemob;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class UserEasemobServiceImpl implements UserEasemobService {

    @Autowired
    private UserEasemobMapper userEasemobMapper;

    @Override
    public int save(UserEasemob userEasemob) {
        return userEasemobMapper.insertSelective(userEasemob);
    }

    @Override
    public UserEasemob findByUuid(String uuid) {
        return userEasemobMapper.selectByUuid(uuid);
    }

    @Override
    public int update(UserEasemob userEasemob) {
        return userEasemobMapper.updateByUuidSelective(userEasemob);
    }

    @Override
    public UserEasemob findByUserUuid(String userUuid) {
        return userEasemobMapper.findByUserUuid(userUuid);
    }

    @Override
    public List<UserEasemob> findByEasemobUsernameList(List<String> easemobUsernameList, int pageNo, int pageSize) {
        int startSize = (pageNo - 1) * pageSize;
        return userEasemobMapper.findByEasemobUsernameList(easemobUsernameList, startSize, pageSize);
    }

    @Override
    public int countByEasemobUsernameList(List<String> imUserNameList) {
        return userEasemobMapper.countByEasemobUsernameList(imUserNameList);
    }

}