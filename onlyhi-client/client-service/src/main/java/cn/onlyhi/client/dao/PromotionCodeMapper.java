package cn.onlyhi.client.dao;

import cn.onlyhi.client.po.PromotionCode;
import org.apache.ibatis.annotations.Param;

public interface PromotionCodeMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(PromotionCode record);

    PromotionCode selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PromotionCode record);

    PromotionCode selectByUuid(String uuid);

    int updateByUuidSelective(PromotionCode record);


    PromotionCode findPromotionCodeByCoursePriceUuidAndCode(@Param("coursePriceUuid") String coursePriceUuid, @Param("code") String code);

}