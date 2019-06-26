package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.TcTabletMapper;
import cn.onlyhi.client.po.TcTablet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class TcTabletServiceImpl implements TcTabletService {

	@Autowired
	private TcTabletMapper tcTabletMapper;

	@Override
	public int save(TcTablet tcTablet) {
		return tcTabletMapper.insertSelective(tcTablet);
	}

	@Override
	public TcTablet findByUuid(String uuid) {
		return tcTabletMapper.selectByUuid(uuid);
	}

	@Override
	public int update(TcTablet tcTablet) {
		return tcTabletMapper.updateByUuidSelective(tcTablet);
	}

	@Override
	public TcTablet findByPhone(String phone) {
		return tcTabletMapper.findByPhone(phone);
	}

}