package cn.onlyhi.client.service;

import cn.onlyhi.client.po.UserFeedback;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface UserFeedbackService {

	int save(UserFeedback userFeedback);

	UserFeedback findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param userFeedback
     * @return
     */
	int update(UserFeedback userFeedback);

	
}