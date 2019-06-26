package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.Banner;

import java.util.List;

public interface BannerMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(Banner record);

    Banner selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Banner record);

    Banner selectByUuid(String uuid);

    int updateByUuidSelective(Banner record);

    List<Banner> findAllBannerByDeviceType(int deviceType);

}