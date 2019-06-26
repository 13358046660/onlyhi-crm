package cn.onlyhi.client.service;

import cn.onlyhi.client.po.SysEnum;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface SysEnumService {

	int save(SysEnum sysEnum);

	SysEnum findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param sysEnum
     * @return
     */
	int update(SysEnum sysEnum);


	List<SysEnum> findByEnumType(String enumType);
}