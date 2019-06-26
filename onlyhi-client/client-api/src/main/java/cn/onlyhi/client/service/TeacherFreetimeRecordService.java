package cn.onlyhi.client.service;

import cn.onlyhi.client.po.TeacherFreetimeRecord;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface TeacherFreetimeRecordService {

    int save(TeacherFreetimeRecord teacherFreetimeRecord);

    TeacherFreetimeRecord findByTeacherUuidAndFreetimeMonth(String teacherUuid, String freetimeMonth);
}