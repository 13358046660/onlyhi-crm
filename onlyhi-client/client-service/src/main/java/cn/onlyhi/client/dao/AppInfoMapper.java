package cn.onlyhi.client.dao;


import cn.onlyhi.client.po.AppInfo;
import org.apache.ibatis.annotations.Param;

public interface AppInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(AppInfo record);

    AppInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AppInfo record);

    AppInfo selectByUuid(String uuid);

    int updateByUuidSelective(AppInfo record);

    AppInfo selectNewest(String deviceType);

    AppInfo findByDeviceTypeAndUserType(@Param("deviceType") String deviceType, @Param("userType") String userType);
}