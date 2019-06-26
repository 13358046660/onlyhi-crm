package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.AppInfoMapper;
import cn.onlyhi.client.po.AppInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/3/22.
 */
@Service
public class AppInfoServiceImpl implements AppInfoService {

    @Autowired
    private AppInfoMapper appInfoMapper;

    @Override
    public AppInfo findNewestAppInfo(String deviceType) {
        return appInfoMapper.selectNewest(deviceType);
    }

    @Override
    public int save(AppInfo appInfo) {
        return appInfoMapper.insertSelective(appInfo);
    }

    @Override
    public AppInfo findByUuid(String uuid) {
        return appInfoMapper.selectByUuid(uuid);
    }

    @Override
    public int update(AppInfo appInfo) {
        return appInfoMapper.updateByUuidSelective(appInfo);
    }

    @Override
    public AppInfo findByDeviceTypeAndUserType(String deviceType, String userType) {
        return appInfoMapper.findByDeviceTypeAndUserType(deviceType, userType);
    }

}
