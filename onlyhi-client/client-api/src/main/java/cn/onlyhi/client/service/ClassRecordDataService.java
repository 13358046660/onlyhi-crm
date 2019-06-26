package cn.onlyhi.client.service;

import cn.onlyhi.client.po.ClassRecordData;

import java.util.List;

/**
 * @Author wangqianzhi
 * <p>
 * Created by wangqianzhi on 2017/7/6.
 */
public interface ClassRecordDataService {

	int save(ClassRecordData classRecordData);

	ClassRecordData findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param classRecordData
     * @return
     */
	int update(ClassRecordData classRecordData);


	int batchSave(List<ClassRecordData> classRecordDataList);

	List<ClassRecordData> findByClassRoomUuid(String classRoomUuid);

	List<ClassRecordData> findByClassRoomUuidAndRecordRole(String classRoomUuid, int recordRole);

	int batchUpadte(List<ClassRecordData> classRecordDataList);
}