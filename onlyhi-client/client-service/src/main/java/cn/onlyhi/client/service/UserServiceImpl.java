package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.UserMapper;
import cn.onlyhi.client.dto.UserDto;
import cn.onlyhi.client.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public int save(User user) {
        return userMapper.insertSelective(user);
    }

    @Override
    public User findByUuid(String uuid) {
        return userMapper.selectByUuid(uuid);
    }

    @Override
    public int update(User user) {
        return userMapper.updateByUuidSelective(user);
    }

    @Override
    public User findByLoginName(String loginName) {
        return userMapper.findByLoginName(loginName);
    }

    @Override
    public List<UserDto> findAllMonitor() {
        return userMapper.findAllMonitor();
    }

    @Override
    public List<UserDto> findQCMonitor() {
        return userMapper.findQCMonitor();
    }

    @Override
    public List<User> findByRoleAlias(String alias) {
        return userMapper.findByRoleAlias(alias);
    }
    @Override
    public UserDto findUserByUuid(String curUuid) {
        return userMapper.findUserByUuid(curUuid);}
}