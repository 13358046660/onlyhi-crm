package cn.onlyhi.client.service;

import cn.onlyhi.client.po.TeacherFreetime;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface TeacherFreetimeService {

    int save(TeacherFreetime teacherFreetime);

    TeacherFreetime findByUuid(String uuid);

    /**
     * 根据uuid更新
     *
     * @param teacherFreetime
     * @return
     */
    int update(TeacherFreetime teacherFreetime);

    int batchSave(List<TeacherFreetime> teacherFreetimeList);

}