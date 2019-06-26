package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.ClientLogfile;

public interface ClientLogfileMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(ClientLogfile record);

    ClientLogfile selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ClientLogfile record);

    ClientLogfile selectByUuid(String uuid);

    int updateByUuidSelective(ClientLogfile record);
}