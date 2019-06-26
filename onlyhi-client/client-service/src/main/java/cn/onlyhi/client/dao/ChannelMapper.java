package cn.onlyhi.client.dao;


import cn.onlyhi.client.po.Channel;

public interface ChannelMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(Channel record);

    Channel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Channel record);

    Channel selectByUuid(String uuid);

    int updateByUuidSelective(Channel record);

    Channel findChannelByAdid(String adid);
}