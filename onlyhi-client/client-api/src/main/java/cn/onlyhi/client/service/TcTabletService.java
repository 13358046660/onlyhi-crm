package cn.onlyhi.client.service;

import cn.onlyhi.client.po.TcTablet;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface TcTabletService {

	int save(TcTablet tcTablet);

	TcTablet findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param tcTablet
     * @return
     */
	int update(TcTablet tcTablet);


	TcTablet findByPhone(String phone);
}