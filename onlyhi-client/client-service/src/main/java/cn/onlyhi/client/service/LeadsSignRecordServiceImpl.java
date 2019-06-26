package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.LeadsSignRecordMapper;
import cn.onlyhi.client.po.LeadsSignRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class LeadsSignRecordServiceImpl implements LeadsSignRecordService {

	@Autowired
	private LeadsSignRecordMapper leadsSignRecordMapper;

	@Override
	public int save(LeadsSignRecord leadsSignRecord) {
		return leadsSignRecordMapper.insertSelective(leadsSignRecord);
	}

}