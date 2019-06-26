package cn.onlyhi.client.service;

import cn.onlyhi.client.po.TcPayGrade;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface TcPayGradeService {

    int save(TcPayGrade tcPayGrade);

    TcPayGrade findById(Long payGrade);
}