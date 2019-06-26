package cn.onlyhi.client.service;


import cn.onlyhi.client.po.AppInfo;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/3/22.
 */
public interface AppInfoService {

    AppInfo findNewestAppInfo(String deviceType);

    int save(AppInfo appInfo);

    AppInfo findByUuid(String uuid);

    /**
     * 根据uuid更新
     * @param appInfo
     * @return
     */
    int update(AppInfo appInfo);

    AppInfo findByDeviceTypeAndUserType(String deviceType, String userType);
}
