package cn.onlyhi.client.service;

import cn.onlyhi.client.po.TcTeacherSchool;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface TcTeacherSchoolService {

	int save(TcTeacherSchool tcTeacherSchool);

	TcTeacherSchool findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param tcTeacherSchool
     * @return
     */
	int update(TcTeacherSchool tcTeacherSchool);


	List<TcTeacherSchool> findAll();
}