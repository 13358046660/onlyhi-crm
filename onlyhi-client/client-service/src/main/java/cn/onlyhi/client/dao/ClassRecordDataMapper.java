package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.ClassRecordData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClassRecordDataMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(ClassRecordData record);

    ClassRecordData selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ClassRecordData record);

    ClassRecordData selectByUuid(String uuid);

    int updateByUuidSelective(ClassRecordData record);

    int batchSave(@Param("list") List<ClassRecordData> classRecordDataList);

    List<ClassRecordData> findByClassRoomUuid(String classRoomUuid);

    List<ClassRecordData> findByClassRoomUuidAndRecordRole(@Param("classRoomUuid") String classRoomUuid, @Param("recordRole") int recordRole);

    int batchUpadte(@Param("list") List<ClassRecordData> classRecordDataList);
}