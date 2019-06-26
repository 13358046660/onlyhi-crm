package cn.onlyhi.client.service;

import cn.onlyhi.client.po.ClientLogfile;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface ClientLogfileService {

	int save(ClientLogfile clientLogfile);

	ClientLogfile findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param clientLogfile
     * @return
     */
	int update(ClientLogfile clientLogfile);

	
}