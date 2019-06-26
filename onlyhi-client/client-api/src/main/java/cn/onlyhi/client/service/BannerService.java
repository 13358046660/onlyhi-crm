package cn.onlyhi.client.service;

import cn.onlyhi.client.po.Banner;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface BannerService {

	int save(Banner banner);

	Banner findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param banner
     * @return
     */
	int update(Banner banner);

	List<Banner> findAllBannerByDeviceType(int deviceType);



}