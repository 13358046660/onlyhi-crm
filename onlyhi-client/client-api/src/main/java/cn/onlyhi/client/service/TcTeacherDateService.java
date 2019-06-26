package cn.onlyhi.client.service;

import cn.onlyhi.client.po.TcTeacherDate;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface TcTeacherDateService {

    int save(TcTeacherDate tcTeacherDate);

    int deleteAndSave(String teacherUuid, String freetimeMonth, List<TcTeacherDate> tcTeacherDateList);

    List<String> findFreetimePeriodByTeacherUuidAndFreetimeDate(String teacherUuid, String freetimeDate);
}