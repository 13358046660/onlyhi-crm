package cn.onlyhi.client.service;

import cn.onlyhi.client.po.AppColumn;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface AppColumnService {

	int save(AppColumn appColumn);

	AppColumn findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param appColumn
     * @return
     */
	int update(AppColumn appColumn);

	List<AppColumn> findAllAppColumnByDeviceType(int deviceType);


}