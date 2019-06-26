package cn.onlyhi.client.service;

import cn.onlyhi.client.po.PromotionCode;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface PromotionCodeService {

	int save(PromotionCode promotionCode);

	PromotionCode findByUuid(String uuid);

	/**
     * 根据uuid更新
     * @param promotionCode
     * @return
     */
	int update(PromotionCode promotionCode);

	/**
	 * 根据课时包uuid和优惠码查询优惠码信息
	 *
	 * @param coursePriceUuid
	 * @param code
	 * @return
	 */
	PromotionCode findPromotionCodeByCoursePriceUuidAndCode(String coursePriceUuid, String code);

	
}