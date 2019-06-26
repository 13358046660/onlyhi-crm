package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.PushMessageMapper;
import cn.onlyhi.client.po.PushMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/5/23.
 */
@Service
public class PushMessageServiceImpl implements PushMessageService {

    @Autowired
    private PushMessageMapper pushMessageMapper;

    @Override
    public int save(PushMessage pushMessage) {
        return pushMessageMapper.insertSelective(pushMessage);
    }

    @Override
    public PushMessage findByLeadsUuid(String leadsUuid) {
        return pushMessageMapper.findByLeadsUuid(leadsUuid);
    }

    @Override
    public List<PushMessage> findByLeadsUuids(List<String> leadsUuids) {
        return pushMessageMapper.findByLeadsUuids(leadsUuids);
    }

    @Override
    public PushMessage findByLeadsUuidOrDeviceToken(String leadsUuid, String deviceToken) {
        return pushMessageMapper.findByLeadsUuidOrDeviceToken(leadsUuid, deviceToken);
    }

    @Override
    public int update(PushMessage pushMessage) {
        return pushMessageMapper.updateByUuidSelective(pushMessage);
    }

    @Override
    public PushMessage findByUuid(String uuid) {
        return pushMessageMapper.selectByUuid(uuid);
    }

}
