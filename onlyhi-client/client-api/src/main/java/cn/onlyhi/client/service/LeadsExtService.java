package cn.onlyhi.client.service;


import cn.onlyhi.client.po.LeadsExt;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface LeadsExtService {

    int save(LeadsExt leadsExt);

    /**
     * 根据leadsUuid更新LeadsExt
     *
     * @param leadsExt
     * @return
     */
    int updateLeadsExtByLeadsUuid(LeadsExt leadsExt);

    /**
     * 根据leadsUuid查询
     *
     * @param leadsUuid
     * @return
     */
    LeadsExt findLeadsExtByLeadsUuid(String leadsUuid);
}