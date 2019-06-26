package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.LeadsExtMapper;
import cn.onlyhi.client.dao.LeadsSignMapper;
import cn.onlyhi.client.dao.LeadsSignRecordMapper;
import cn.onlyhi.client.dto.LeadsSignDto;
import cn.onlyhi.client.po.LeadsExt;
import cn.onlyhi.client.po.LeadsSign;
import cn.onlyhi.client.po.LeadsSignRecord;
import cn.onlyhi.common.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class LeadsSignServiceImpl implements LeadsSignService {

    @Autowired
    private LeadsSignMapper leadsSignMapper;
    @Autowired
    private LeadsSignRecordMapper leadsSignRecordMapper;
    @Autowired
    private LeadsExtMapper leadsExtMapper;

    @Override
    public int save(LeadsSign leadsSign) {
        return leadsSignMapper.insertSelective(leadsSign);
    }

    @Transactional
    @Override
    public LeadsSignDto sign(String leadsUuid) {
        LeadsSign leadsSign = leadsSignMapper.findByLeadsUuid(leadsUuid);
        if (leadsSign != null && DateUtil.judgmentDay(leadsSign.getLastSignDate()) == 0) {  //今天已签到
            return null;
        }
        Date currentDate = new Date();
        int integral = 1;//签到积分
        int currentContDays = 1;   //连续天数
        if (leadsSign == null) {    //第一次签到
            leadsSign = new LeadsSign();
            leadsSign.setLeadsUuid(leadsUuid);
            leadsSign.setLastSignDate(currentDate);
            leadsSign.setContDays(currentContDays);
            leadsSignMapper.insertSelective(leadsSign);
        } else {
            Date lastSignDate = leadsSign.getLastSignDate();
            int i = DateUtil.judgmentDay(lastSignDate);
            int contDays = leadsSign.getContDays();
            if (i == -1) {  //签到时间为昨天
                currentContDays = contDays + 1; //连续天数+1
                if (contDays == 4) {    //已连续签到4天
                    integral = 4;   //第五天积分为1+3=4分
                }
                if (contDays == 5) {    //已连续签到5天
                    currentContDays = 1;    //连续天数重新设置为1
                }
            }
            leadsSign.setLastSignDate(currentDate);
            leadsSign.setContDays(currentContDays);
            leadsSignMapper.updateByPrimaryKeySelective(leadsSign);
        }
        //保存签到记录
        LeadsSignRecord leadsSignRecord = new LeadsSignRecord();
        leadsSignRecord.setLeadsUuid(leadsUuid);
        leadsSignRecord.setSignDate(currentDate);
        leadsSignRecord.setIntegral(integral);
        leadsSignRecordMapper.insertSelective(leadsSignRecord);
        //更新leads总积分
        LeadsExt leadsExt = leadsExtMapper.findLeadsExtByLeadsUuid(leadsUuid);
        if (leadsExt == null) {
            leadsExt = new LeadsExt();
            leadsExt.setLeadsUuid(leadsUuid);
            leadsExt.setTotalIntegral(integral);
            leadsExtMapper.insertSelective(leadsExt);
        } else {
            int totalIntegral = leadsExt.getTotalIntegral() + integral;
            LeadsExt leadsExt2 = new LeadsExt();
            leadsExt2.setId(leadsExt.getId());
            leadsExt2.setLeadsUuid(leadsUuid);
            leadsExt2.setTotalIntegral(totalIntegral);
            leadsExtMapper.updateByPrimaryKeySelective(leadsExt2);
        }
        LeadsSignDto leadsSignDto = new LeadsSignDto();
        leadsSignDto.setCurrentIntegral(integral);
        leadsSignDto.setContDays(currentContDays);
        if (currentContDays == 4) {
            leadsSignDto.setTomorrowIntegral(4);
        } else {
            leadsSignDto.setTomorrowIntegral(1);
        }
        return leadsSignDto;
    }

    @Override
    public boolean isSign(String leadsUuid) {
        LeadsSign leadsSign = leadsSignMapper.findByLeadsUuid(leadsUuid);
        if (leadsSign != null && DateUtil.judgmentDay(leadsSign.getLastSignDate()) == 0) {  //今天已签到
            return true;
        }
        return false;
    }

}