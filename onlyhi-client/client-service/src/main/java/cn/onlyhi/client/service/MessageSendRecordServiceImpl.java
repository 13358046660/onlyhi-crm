package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.MessageSendRecordMapper;
import cn.onlyhi.client.po.MessageSendRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class MessageSendRecordServiceImpl implements MessageSendRecordService {

	@Autowired
	private MessageSendRecordMapper messageSendRecordMapper;

	@Override
	public int save(MessageSendRecord messageSendRecord) {
		return messageSendRecordMapper.insertSelective(messageSendRecord);
	}

}