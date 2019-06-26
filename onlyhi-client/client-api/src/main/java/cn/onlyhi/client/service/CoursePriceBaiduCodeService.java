package cn.onlyhi.client.service;

import cn.onlyhi.client.po.CoursePriceBaiduCode;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface CoursePriceBaiduCodeService {

	int save(CoursePriceBaiduCode coursePriceBaiduCode);

	CoursePriceBaiduCode findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param coursePriceBaiduCode
     * @return
     */
	int update(CoursePriceBaiduCode coursePriceBaiduCode);

	/**
	 * 根据课时包类型查询百度编码
	 *
	 * @param type
	 * @return
	 */
	List<CoursePriceBaiduCode> findBaiduCodeByType(String type);

	
}