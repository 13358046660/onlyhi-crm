package cn.onlyhi.client.service;


import cn.onlyhi.client.po.Patriarch;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/5.
 */
public interface PatriarchService {

    int save(Patriarch patriarch);

    Patriarch findByphone(String phone);

    int updatePasswordByUuid(String patriarchUuid, String newPassword);

    Patriarch findByUuid(String uuid);

    /**
     * 根据uuid更新
     * @param patriarch
     * @return
     */
    int update(Patriarch patriarch);

}
