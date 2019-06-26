package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.LeadsLogMapper;
import cn.onlyhi.client.po.LeadsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/4/6.
 */
@Service
public class LeadsLogServiceImpl implements LeadsLogService {

    @Autowired
    private LeadsLogMapper leadsLogMapper;

    @Override
    public int save(LeadsLog leadsLog) {
        return leadsLogMapper.insertSelective(leadsLog);
    }
}
