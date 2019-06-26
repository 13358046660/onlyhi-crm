package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.LeadsMapper;
import cn.onlyhi.client.dto.LeadsExtendEntity;
import cn.onlyhi.client.po.Leads;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/3/18.
 */
@Service
public class LeadsServiceImpl implements LeadsService {

    @Autowired
    private LeadsMapper leadsMapper;

    @Override
    public int save(Leads leads) {
        return leadsMapper.insertSelective(leads);
    }

    @Override
    public Leads findByUuid(String uuid) {
        return leadsMapper.selectByUuid(uuid);
    }

    @Override
    public int update(Leads leads) {
        return leadsMapper.updateByUuidSelective(leads);
    }

    @Override
    public Leads findLeadsByPhone(String phone) {
        return leadsMapper.selectByPhone(phone);
    }

    @Override
    public int updateSexByUuid(String uuid, Integer sex) {
        return leadsMapper.updateSexByUuid(uuid, sex);
    }

    @Override
    public int updateGradeByUuid(String uuid, String grade) {
        return leadsMapper.updateGradeByUuid(uuid, grade);
    }

    @Override
    public int updateExamAreaByUuid(String uuid, String examArea) {
        return leadsMapper.updateExamAreaByUuid(uuid, examArea);
    }

    @Override
    public int updateSubjectByUuid(String leadsUuid, String subject) {
        return leadsMapper.updateSubjectByUuid(leadsUuid, subject);
    }

    @Override
    public LeadsExtendEntity selectLeadsByPhone(String phone) {
        return leadsMapper.selectLeadsByPhone(phone);
    }
}
