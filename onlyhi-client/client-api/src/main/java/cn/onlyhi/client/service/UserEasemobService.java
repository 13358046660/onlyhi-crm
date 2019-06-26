package cn.onlyhi.client.service;

import cn.onlyhi.client.po.UserEasemob;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface UserEasemobService {

	int save(UserEasemob userEasemob);

	UserEasemob findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param userEasemob
     * @return
     */
	int update(UserEasemob userEasemob);


	UserEasemob findByUserUuid(String userUuid);

	List<UserEasemob> findByEasemobUsernameList(List<String> easemobUsernameList, int pageNo, int pageSize);

	int countByEasemobUsernameList(List<String> imUserNameList);
}