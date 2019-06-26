package cn.onlyhi.client.service;

import cn.onlyhi.client.dto.LeadsSignDto;
import cn.onlyhi.client.po.LeadsSign;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface LeadsSignService {

    int save(LeadsSign leadsSign);

    LeadsSignDto sign(String leadsUuid);

    boolean isSign(String leadsUuid);
}