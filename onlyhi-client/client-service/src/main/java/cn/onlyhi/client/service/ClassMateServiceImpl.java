package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.ClassMateMapper;
import cn.onlyhi.client.po.ClassMate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author wqz
 * <p>
 * Created by wqz on 2017/7/6.
 */
@Service
public class ClassMateServiceImpl implements ClassMateService {

	@Autowired
	private ClassMateMapper classMateMapper;

	@Override
	public int save(ClassMate classMate) {
		return classMateMapper.insertSelective(classMate);
	}

	@Override
	public ClassMate findByUuid(String uuid) {
		return classMateMapper.selectByUuid(uuid);
	}

	@Override
	public int update(ClassMate classMate) {
		return classMateMapper.updateByUuidSelective(classMate);
	}

	@Override
	public ClassMate findByAgoraUidAndRecordId(int agoraUid,int recordId) {
		return classMateMapper.findByAgoraUidAndRecordId(agoraUid,recordId);
	}

	@Override
	public List<ClassMate> findByRecordId(int recordId) {
		return classMateMapper.findByRecordId(recordId);
	}

	@Override
	public List<ClassMate> findByClassRoomUuid(String classRoomUuid) {
		return classMateMapper.findByClassRoomUuid(classRoomUuid);
	}

}