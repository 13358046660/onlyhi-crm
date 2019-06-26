package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.ClientLogfileMapper;
import cn.onlyhi.client.po.ClientLogfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class ClientLogfileServiceImpl implements ClientLogfileService {

	@Autowired
	private ClientLogfileMapper clientLogfileMapper;

	@Override
	public int save(ClientLogfile clientLogfile) {
		return clientLogfileMapper.insertSelective(clientLogfile);
	}

	@Override
	public ClientLogfile findByUuid(String uuid) {
		return clientLogfileMapper.selectByUuid(uuid);
	}

	@Override
	public int update(ClientLogfile clientLogfile) {
		return clientLogfileMapper.updateByUuidSelective(clientLogfile);
	}

}