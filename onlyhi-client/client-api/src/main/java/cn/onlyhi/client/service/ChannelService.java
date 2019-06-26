package cn.onlyhi.client.service;


import cn.onlyhi.client.po.Channel;

public interface ChannelService {

    /**
     * 根据adid获取渠道信息
     *
     * @param adid
     * @return
     */
    Channel findChannelByAdid(String adid);

    int save(Channel channel);

    Channel findByUuid(String uuid);

    /**
     * 根据uuid更新
     * @param channel
     * @return
     */
    int update(Channel channel);
}
