package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.MessageSendTemplateMapper;
import cn.onlyhi.client.po.MessageSendTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class MessageSendTemplateServiceImpl implements MessageSendTemplateService {

	@Autowired
	private MessageSendTemplateMapper messageSendTemplateMapper;

	@Override
	public MessageSendTemplate findByPurpose(int purpose) {
		return messageSendTemplateMapper.findByPurpose(purpose);
	}

	@Override
	public int save(MessageSendTemplate messageSendTemplate) {
		return messageSendTemplateMapper.insertSelective(messageSendTemplate);
	}

	@Override
	public MessageSendTemplate findByUuid(String uuid) {
		return messageSendTemplateMapper.selectByUuid(uuid);
	}

	@Override
	public int update(MessageSendTemplate messageSendTemplate) {
		return messageSendTemplateMapper.updateByUuidSelective(messageSendTemplate);
	}
}