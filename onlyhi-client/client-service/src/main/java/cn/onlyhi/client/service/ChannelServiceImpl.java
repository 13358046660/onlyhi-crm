package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.ChannelMapper;
import cn.onlyhi.client.po.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelServiceImpl implements ChannelService {

    @Autowired
    private ChannelMapper channelMapper;

    @Override
    public Channel findChannelByAdid(String adid) {
        return channelMapper.findChannelByAdid(adid);
    }

    @Override
    public int save(Channel channel) {
        return channelMapper.insertSelective(channel);
    }

    @Override
    public Channel findByUuid(String uuid) {
        return channelMapper.selectByUuid(uuid);
    }

    @Override
    public int update(Channel channel) {
        return channelMapper.updateByUuidSelective(channel);
    }

}
