package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.UserFeedbackMapper;
import cn.onlyhi.client.po.UserFeedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class UserFeedbackServiceImpl implements UserFeedbackService {

	@Autowired
	private UserFeedbackMapper userFeedbackMapper;

	@Override
	public int save(UserFeedback userFeedback) {
		return userFeedbackMapper.insertSelective(userFeedback);
	}

	@Override
	public UserFeedback findByUuid(String uuid) {
		return userFeedbackMapper.selectByUuid(uuid);
	}

	@Override
	public int update(UserFeedback userFeedback) {
		return userFeedbackMapper.updateByUuidSelective(userFeedback);
	}

}