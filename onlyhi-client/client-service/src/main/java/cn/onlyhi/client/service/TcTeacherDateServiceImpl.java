package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.TcTeacherDateMapper;
import cn.onlyhi.client.po.TcTeacherDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class TcTeacherDateServiceImpl implements TcTeacherDateService {

    @Autowired
    private TcTeacherDateMapper tcTeacherDateMapper;

    @Override
    public int save(TcTeacherDate tcTeacherDate) {
        return tcTeacherDateMapper.insertSelective(tcTeacherDate);
    }

    @Transactional
    @Override
    public int deleteAndSave(String teacherUuid, String freetimeMonth, List<TcTeacherDate> tcTeacherDateList) {
//        tcTeacherDateMapper.deleteByTeacherUuidAndFreetimeMonth(teacherUuid, freetimeMonth);
        tcTeacherDateMapper.deleteByTeacherUuidAndTcDate(teacherUuid, freetimeMonth);
        int i = tcTeacherDateMapper.batchSave(tcTeacherDateList);
        return i;
    }

    @Override
    public List<String> findFreetimePeriodByTeacherUuidAndFreetimeDate(String teacherUuid, String freetimeDate) {
        return tcTeacherDateMapper.findFreetimePeriodByTeacherUuidAndFreetimeDate(teacherUuid, freetimeDate);
    }

}