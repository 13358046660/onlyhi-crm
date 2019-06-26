package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.PatriarchLeadsRecordMapper;
import cn.onlyhi.client.po.PatriarchLeadsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/8.
 */
@Service
public class PatriarchLeadsRecordServiceImpl implements PatriarchLeadsRecordService {

    @Autowired
    private PatriarchLeadsRecordMapper patriarchLeadsRecordMapper;

    @Override
    public int save(PatriarchLeadsRecord patriarchLeadsRecord) {
        return patriarchLeadsRecordMapper.insertSelective(patriarchLeadsRecord);
    }

    @Override
    public PatriarchLeadsRecord findByUuid(String uuid) {
        return patriarchLeadsRecordMapper.selectByUuid(uuid);
    }

    @Override
    public int update(PatriarchLeadsRecord patriarchLeadsRecord) {
        return patriarchLeadsRecordMapper.updateByUuidSelective(patriarchLeadsRecord);
    }

    @Override
    public PatriarchLeadsRecord findByPhone(String phone) {
        return patriarchLeadsRecordMapper.findByPhone(phone);
    }
}
