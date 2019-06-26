package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.UserDeviceInformation;

public interface UserDeviceInformationMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(UserDeviceInformation record);

    UserDeviceInformation selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserDeviceInformation record);
}