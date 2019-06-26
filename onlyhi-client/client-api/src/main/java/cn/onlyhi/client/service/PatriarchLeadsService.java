package cn.onlyhi.client.service;


import cn.onlyhi.client.po.PatriarchLeads;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/5.
 */
public interface PatriarchLeadsService {

    /**
     * 根据家长手机号查询绑定信息
     *
     * @param phone
     * @return
     */
    PatriarchLeads findByPhone(String phone);

    /**
     * 根据leadsUuid查找
     *
     * @param leadsUuid
     * @return
     */
    PatriarchLeads findByLeadsUuid(String leadsUuid);

    int update(PatriarchLeads patriarchLeads);

    int save(PatriarchLeads patriarchLeads);

    PatriarchLeads findByUuid(String uuid);

    int deleteByPatriarchUuid(String patriarchUuid);
}
