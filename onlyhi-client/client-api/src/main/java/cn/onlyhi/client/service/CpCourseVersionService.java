package cn.onlyhi.client.service;

import cn.onlyhi.client.po.CpCourseVersion;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface CpCourseVersionService {

	int save(CpCourseVersion cpCourseVersion);

	CpCourseVersion findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param cpCourseVersion
     * @return
     */
	int update(CpCourseVersion cpCourseVersion);

	
}