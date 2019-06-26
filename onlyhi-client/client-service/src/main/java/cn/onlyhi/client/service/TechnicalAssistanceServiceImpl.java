package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.TechnicalAssistanceMapper;
import cn.onlyhi.client.po.TechnicalAssistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class TechnicalAssistanceServiceImpl implements TechnicalAssistanceService {

    @Autowired
    private TechnicalAssistanceMapper technicalAssistanceMapper;

    @Override
    public int save(TechnicalAssistance technicalAssistance) {
        return technicalAssistanceMapper.insertSelective(technicalAssistance);
    }

    @Override
    public TechnicalAssistance findByUuid(String uuid) {
        return technicalAssistanceMapper.selectByUuid(uuid);
    }

    @Override
    public int update(TechnicalAssistance technicalAssistance) {
        return technicalAssistanceMapper.updateByUuidSelective(technicalAssistance);
    }

    @Override
    public List<TechnicalAssistance> findByAssistanceUuidAndAssistanceStatus(String assistanceUuid, int assistanceStatus) {
        return technicalAssistanceMapper.findByAssistanceUuidAndAssistanceStatus(assistanceUuid, assistanceStatus);
    }

}