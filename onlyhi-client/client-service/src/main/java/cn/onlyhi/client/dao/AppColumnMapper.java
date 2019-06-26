package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.AppColumn;

import java.util.List;

public interface AppColumnMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(AppColumn record);

    AppColumn selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AppColumn record);

    AppColumn selectByUuid(String uuid);

    int updateByUuidSelective(AppColumn record);

    List<AppColumn> findAllAppColumnByDeviceType(int deviceType);

}