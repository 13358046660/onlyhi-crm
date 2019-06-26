package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.TcTeacherFileMapper;
import cn.onlyhi.client.po.TcTeacherFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class TcTeacherFileServiceImpl implements TcTeacherFileService {

    @Autowired
    private TcTeacherFileMapper tcTeacherFileMapper;

    @Override
    public int save(TcTeacherFile tcTeacherFile) {
        return tcTeacherFileMapper.insertSelective(tcTeacherFile);
    }

    @Override
    public List<TcTeacherFile> findByTeacherId(Long teacherId) {
        return tcTeacherFileMapper.findByTeacherId(teacherId);
    }

    @Override
    public TcTeacherFile findByTeacherIdAndPurpose(Long teacherId, int purpose) {
        return tcTeacherFileMapper.findByTeacherIdAndPurpose(teacherId, purpose);
    }

}