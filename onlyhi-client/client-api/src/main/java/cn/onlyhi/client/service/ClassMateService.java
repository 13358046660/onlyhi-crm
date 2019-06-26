package cn.onlyhi.client.service;

import cn.onlyhi.client.po.ClassMate;

import java.util.List;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/7/6.
 */
public interface ClassMateService {

    int save(ClassMate classMate);

    ClassMate findByUuid(String uuid);

    /**
     * 根据uuid更新
     *
     * @param classMate
     * @return
     */
    int update(ClassMate classMate);

    /**
     * 根据声网uid和录制Id查询
     *
     * @param agoraUid
     * @param recordId
     * @return
     */
    ClassMate findByAgoraUidAndRecordId(int agoraUid, int recordId);

    /**
     * 根据录制Id查询进入房间的学生和老师信息
     *
     * @param recordId
     * @return
     */
    List<ClassMate> findByRecordId(int recordId);

    List<ClassMate> findByClassRoomUuid(String classRoomUuid);
}