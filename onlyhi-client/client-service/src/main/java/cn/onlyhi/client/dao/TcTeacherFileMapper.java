package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.TcTeacherFile;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TcTeacherFileMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TcTeacherFile record);

    TcTeacherFile selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TcTeacherFile record);

    List<TcTeacherFile> findByTeacherId(Long teacherId);

    int deleteByTeacherId(Long teacherId);

    int batchSave(@Param("list") List<TcTeacherFile> teacherFileList);

    TcTeacherFile findByTeacherIdAndPurpose(@Param("teacherId") Long teacherId, @Param("purpose") int purpose);
}