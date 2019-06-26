package cn.onlyhi.client.service;


import cn.onlyhi.client.po.PatriarchLeadsRecord;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/8.
 */
public interface PatriarchLeadsRecordService {
    int save(PatriarchLeadsRecord patriarchLeadsRecord);

    PatriarchLeadsRecord findByUuid(String uuid);

    /**
     * 根据uuid更新
     * @param patriarchLeadsRecord
     * @return
     */
    int update(PatriarchLeadsRecord patriarchLeadsRecord);

    /**
     * 根据家长手机号查询绑定信息(最新的)
     *
     * @param phone
     * @return
     */
    PatriarchLeadsRecord findByPhone(String phone);
}
