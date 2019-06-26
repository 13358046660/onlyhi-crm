package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.ClassRecordMapper;
import cn.onlyhi.client.dto.ClassRecordExtendEntity;
import cn.onlyhi.client.po.ClassRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/22.
 */
@Service
public class ClassRecordServiceImpl implements ClassRecordService {

    @Autowired
    private ClassRecordMapper classRecordMapper;

    @Override
    public int save(ClassRecord classRecord) {
        return classRecordMapper.insertSelective(classRecord);
    }

    @Override
    public ClassRecord findByUuid(String uuid) {
        return classRecordMapper.selectByUuid(uuid);
    }

    @Override
    public int update(ClassRecord classRecord) {
        return classRecordMapper.updateByUuidSelective(classRecord);
    }

    @Override
    public int batchSave(List<ClassRecord> classRecordList) {
        return classRecordMapper.batchInsertSelective(classRecordList);
    }

    @Override
    public List<ClassRecord> findByClassRoomUuid(String classRoomUuid) {
        return classRecordMapper.findByClassRoomUuid(classRoomUuid);
    }

    @Override
    public List<ClassRecordExtendEntity> selectRoomTime(String roomUuid) {
        return  classRecordMapper.selectRoomTime(roomUuid);
    }
}
