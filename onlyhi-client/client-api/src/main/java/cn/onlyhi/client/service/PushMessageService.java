package cn.onlyhi.client.service;


import cn.onlyhi.client.po.PushMessage;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/5/23.
 */
public interface PushMessageService {

    int save(PushMessage pushMessage);

    PushMessage findByLeadsUuid(String leadsUuid);

    List<PushMessage> findByLeadsUuids(List<String> leadsUuids);

    PushMessage findByLeadsUuidOrDeviceToken(String leadsUuid, String deviceToken);

    /**
     * 根据uuid更新
     * @param pushMessage
     * @return
     */
    int update(PushMessage pushMessage);

    PushMessage findByUuid(String uuid);
}
