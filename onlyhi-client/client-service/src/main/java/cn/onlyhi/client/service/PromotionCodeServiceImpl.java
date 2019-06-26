package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.PromotionCodeMapper;
import cn.onlyhi.client.po.PromotionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class PromotionCodeServiceImpl implements PromotionCodeService {

	@Autowired
	private PromotionCodeMapper promotionCodeMapper;

	@Override
	public int save(PromotionCode promotionCode) {
		return promotionCodeMapper.insertSelective(promotionCode);
	}

	@Override
	public PromotionCode findByUuid(String uuid) {
		return promotionCodeMapper.selectByUuid(uuid);
	}

	@Override
	public int update(PromotionCode promotionCode) {
		return promotionCodeMapper.updateByUuidSelective(promotionCode);
	}

	@Override
	public PromotionCode findPromotionCodeByCoursePriceUuidAndCode(String coursePriceUuid, String code) {
		return promotionCodeMapper.findPromotionCodeByCoursePriceUuidAndCode(coursePriceUuid,code);
	}
}