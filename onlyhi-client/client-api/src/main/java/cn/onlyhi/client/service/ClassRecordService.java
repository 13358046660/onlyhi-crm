package cn.onlyhi.client.service;

import cn.onlyhi.client.dto.ClassRecordExtendEntity;
import cn.onlyhi.client.po.ClassRecord;

import java.util.List;

/**
 * @Author wqz
 * <p>
 * Created by wangqianzhi on 2017/6/22.
 */
public interface ClassRecordService {

    int save(ClassRecord classRecord);

    ClassRecord findByUuid(String uuid);

    /**
     * 根据uuid更新
     *
     * @param classRecord
     * @return
     */
    int update(ClassRecord classRecord);

    /**
     * 批量保存
     *
     * @param classRecordList
     * @return
     */
    int batchSave(List<ClassRecord> classRecordList);

    List<ClassRecord> findByClassRoomUuid(String classRoomUuid);
    List<ClassRecordExtendEntity> selectRoomTime(String roomUuid);
}
