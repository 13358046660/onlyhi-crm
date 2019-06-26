package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.BannerMapper;
import cn.onlyhi.client.po.Banner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class BannerServiceImpl implements BannerService {

	@Autowired
	private BannerMapper bannerMapper;

	@Override
	public int save(Banner banner) {
		return bannerMapper.insertSelective(banner);
	}

	@Override
	public Banner findByUuid(String uuid) {
		return bannerMapper.selectByUuid(uuid);
	}

	@Override
	public int update(Banner banner) {
		return bannerMapper.updateByUuidSelective(banner);
	}

	@Override
	public List<Banner> findAllBannerByDeviceType(int deviceType) {
		return bannerMapper.findAllBannerByDeviceType(deviceType);
	}

}