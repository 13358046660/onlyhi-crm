package cn.onlyhi.client.service;

import cn.onlyhi.client.po.UserNeteaseim;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface UserNeteaseimService {

	int save(UserNeteaseim userNeteaseim);

	UserNeteaseim findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param userNeteaseim
     * @return
     */
	int update(UserNeteaseim userNeteaseim);


	UserNeteaseim findByUserUuid(String userUuid);

	/**
	 * 根据用户uuid（与app约定的存手机号phone）更新对应的网易云信token
	 * @param userNeteaseim
	 * @return
	 */
	int updateByUserUuid(UserNeteaseim userNeteaseim);
}