package cn.onlyhi.client.service;


import cn.onlyhi.client.po.SystemLog;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/12.
 */
public interface SystemLogService {

    int save(SystemLog systemLog);

    SystemLog findByUuid(String uuid);

    /**
     * 根据uuid更新
     * @param systemLog
     * @return
     */
    int update(SystemLog systemLog);
}
