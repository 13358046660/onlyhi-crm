package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.AgoraCallLogMapper;
import cn.onlyhi.client.po.AgoraCallLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class AgoraCallLogServiceImpl implements AgoraCallLogService {

	@Autowired
	private AgoraCallLogMapper agoraCallLogMapper;

	@Override
	public int save(AgoraCallLog agoraCallLog) {
		return agoraCallLogMapper.insertSelective(agoraCallLog);
	}

	@Override
	public AgoraCallLog findByUuid(String uuid) {
		return agoraCallLogMapper.selectByUuid(uuid);
	}

	@Override
	public int update(AgoraCallLog agoraCallLog) {
		return agoraCallLogMapper.updateByUuidSelective(agoraCallLog);
	}

}