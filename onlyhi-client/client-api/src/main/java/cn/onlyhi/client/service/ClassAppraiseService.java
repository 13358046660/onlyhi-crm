package cn.onlyhi.client.service;

import cn.onlyhi.client.po.ClassAppraise;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface ClassAppraiseService {

    int save(ClassAppraise classAppraise);

    ClassAppraise findByUuid(String uuid);

    /**
     * 根据uuid更新
     *
     * @param classAppraise
     * @return
     */
    int update(ClassAppraise classAppraise);

    ClassAppraise findByCourseUuid(String courseUuid);
}