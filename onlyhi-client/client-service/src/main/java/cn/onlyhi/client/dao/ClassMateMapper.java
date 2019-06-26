package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.ClassMate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClassMateMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(ClassMate record);

    ClassMate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ClassMate record);

    ClassMate selectByUuid(String uuid);

    int updateByUuidSelective(ClassMate record);

    /**
     * 根据房间uuid和用户类型查询
     *
     * @param classRoomUuid
     * @param userType
     * @return
     */
    ClassMate findByClassRoomUuidAndUserType(@Param("classRoomUuid") String classRoomUuid, @Param("userType") String userType);

    /**
     * 根据声网uid和录制Id查询
     *
     * @param agoraUid
     * @param recordId
     * @return
     */
    ClassMate findByAgoraUidAndRecordId(@Param("agoraUid") int agoraUid, @Param("recordId") int recordId);

    /**
     * 根据录制Id查询进入房间的学生和老师信息
     *
     * @param recordId
     * @return
     */
    List<ClassMate> findByRecordId(int recordId);

    List<ClassMate> findByClassRoomUuid(String classRoomUuid);
}