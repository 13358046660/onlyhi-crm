package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.ClassRecordDataMapper;
import cn.onlyhi.client.po.ClassRecordData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class ClassRecordDataServiceImpl implements ClassRecordDataService {

    @Autowired
    private ClassRecordDataMapper classRecordDataMapper;

    @Override
    public int save(ClassRecordData classRecordData) {
        return classRecordDataMapper.insertSelective(classRecordData);
    }

    @Override
    public ClassRecordData findByUuid(String uuid) {
        return classRecordDataMapper.selectByUuid(uuid);
    }

    @Override
    public int update(ClassRecordData classRecordData) {
        return classRecordDataMapper.updateByUuidSelective(classRecordData);
    }

    @Override
    public int batchSave(List<ClassRecordData> classRecordDataList) {
        return classRecordDataMapper.batchSave(classRecordDataList);
    }

    @Override
    public List<ClassRecordData> findByClassRoomUuid(String classRoomUuid) {
        return classRecordDataMapper.findByClassRoomUuid(classRoomUuid);
    }

    @Override
    public List<ClassRecordData> findByClassRoomUuidAndRecordRole(String classRoomUuid, int recordRole) {
        return classRecordDataMapper.findByClassRoomUuidAndRecordRole(classRoomUuid, recordRole);
    }

    @Override
    public int batchUpadte(List<ClassRecordData> classRecordDataList) {
        return classRecordDataMapper.batchUpadte(classRecordDataList);
    }

}