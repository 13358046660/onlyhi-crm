package cn.onlyhi.client.service;

import cn.onlyhi.client.po.ClassAppraiseStar;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface ClassAppraiseStarService {

	int save(ClassAppraiseStar classAppraiseStar);

	ClassAppraiseStar findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param classAppraiseStar
     * @return
     */
	int update(ClassAppraiseStar classAppraiseStar);

	List<ClassAppraiseStar> findByStar(int star);

	/**
	 * 查询评价项列表
	 * @param classAppraiseStarUuidList
	 * @return
     */
	List<String> findContentsByUuids(List<String> classAppraiseStarUuidList);
}