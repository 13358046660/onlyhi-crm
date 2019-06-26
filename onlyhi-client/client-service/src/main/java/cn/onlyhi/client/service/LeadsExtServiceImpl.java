package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.LeadsExtMapper;
import cn.onlyhi.client.po.LeadsExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class LeadsExtServiceImpl implements LeadsExtService {

    @Autowired
    private LeadsExtMapper leadsExtMapper;

    @Override
    public int save(LeadsExt leadsExt) {
        return leadsExtMapper.insertSelective(leadsExt);
    }

    @Override
    public int updateLeadsExtByLeadsUuid(LeadsExt leadsExt) {
        return leadsExtMapper.updateLeadsExtByLeadsUuidSelective(leadsExt);
    }

    @Override
    public LeadsExt findLeadsExtByLeadsUuid(String leadsUuid) {
        return leadsExtMapper.findLeadsExtByLeadsUuid(leadsUuid);
    }
}