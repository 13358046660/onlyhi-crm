package cn.onlyhi.client.service;

import cn.onlyhi.client.po.SysArea;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface SysAreaService {

	int save(SysArea sysArea);


	List<SysArea> findByAreaLevel(Integer areaLevel);

	List<SysArea> findByAreaLevelAndParentCode(Integer areaLevel, String parentCode);

	SysArea findByAreaCode(String areaCode);
}