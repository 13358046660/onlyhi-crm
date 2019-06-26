package cn.onlyhi.client.service;

import cn.onlyhi.client.po.TcTeacherFile;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface TcTeacherFileService {

    int save(TcTeacherFile tcTeacherFile);

    List<TcTeacherFile> findByTeacherId(Long teacherId);

    /**
     * 根据老师的id和用户查找文件
     *
     * @param teacherId
     * @param purpose
     * @return
     */
    TcTeacherFile findByTeacherIdAndPurpose(Long teacherId, int purpose);
}