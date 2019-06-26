package cn.onlyhi.client.dao;


import cn.onlyhi.client.dto.ClassRecordExtendEntity;
import cn.onlyhi.client.po.ClassRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClassRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(ClassRecord record);

    ClassRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ClassRecord record);

    ClassRecord selectByUuid(String uuid);

    int updateByUuidSelective(ClassRecord record);

    /**
     * 批量插入
     *
     * @param classRecordList
     * @return
     */
    int batchInsertSelective(@Param("list") List<ClassRecord> classRecordList);

    List<ClassRecord> findByClassRoomUuid(String classRoomUuid);

    List<ClassRecordExtendEntity> selectRoomTime(String roomUuid);
}