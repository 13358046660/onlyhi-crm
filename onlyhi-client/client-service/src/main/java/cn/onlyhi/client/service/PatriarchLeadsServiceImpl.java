package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.PatriarchLeadsMapper;
import cn.onlyhi.client.dao.PatriarchLeadsRecordMapper;
import cn.onlyhi.client.po.PatriarchLeads;
import cn.onlyhi.client.po.PatriarchLeadsRecord;
import cn.onlyhi.common.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/5.
 */
@Service
public class PatriarchLeadsServiceImpl implements PatriarchLeadsService {

    @Autowired
    private PatriarchLeadsMapper patriarchLeadsMapper;
    @Autowired
    private PatriarchLeadsRecordMapper patriarchLeadsRecordMapper;

    @Override
    public PatriarchLeads findByPhone(String phone) {
        return patriarchLeadsMapper.findByPhone(phone);
    }

    @Override
    public PatriarchLeads findByLeadsUuid(String leadsUuid) {
        return patriarchLeadsMapper.findByLeadsUuid(leadsUuid);
    }

    @Override
    @Transactional
    public int update(PatriarchLeads patriarchLeads) {
        savePatriarchLeadsRecord(patriarchLeads);
        return patriarchLeadsMapper.updateByPrimaryKeySelective(patriarchLeads);
    }

    @Override
    @Transactional
    public int save(PatriarchLeads patriarchLeads) {
        savePatriarchLeadsRecord(patriarchLeads);
        return patriarchLeadsMapper.insertSelective(patriarchLeads);
    }

    @Override
    public PatriarchLeads findByUuid(String uuid) {
        return patriarchLeadsMapper.selectByUuid(uuid);
    }

    @Transactional
    @Override
    public int deleteByPatriarchUuid(String patriarchUuid) {
        patriarchLeadsMapper.deleteByPatriarchUuid(patriarchUuid);
        int i = patriarchLeadsRecordMapper.deleteByPatriarchUuid(patriarchUuid);
        return i;
    }

    private void savePatriarchLeadsRecord(PatriarchLeads patriarchLeads) {
        PatriarchLeadsRecord patriarchLeadsRecord = new PatriarchLeadsRecord();
        patriarchLeadsRecord.setPatriarchLeadsRecordUuid(UUIDUtil.randomUUID2());
        patriarchLeadsRecord.setPatriarchUuid(patriarchLeads.getPatriarchUuid());
        patriarchLeadsRecord.setPatriarchPhone(patriarchLeads.getPatriarchPhone());
        patriarchLeadsRecord.setLeadsPhone(patriarchLeads.getLeadsPhone());
        patriarchLeadsRecord.setLeadsUuid(patriarchLeads.getLeadsUuid());
        patriarchLeadsRecordMapper.insertSelective(patriarchLeadsRecord);
    }
}
