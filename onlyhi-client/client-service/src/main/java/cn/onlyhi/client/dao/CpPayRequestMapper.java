package cn.onlyhi.client.dao;

import cn.onlyhi.client.dto.OrderListDto;
import cn.onlyhi.client.po.CpPayRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CpPayRequestMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(CpPayRequest record);

    CpPayRequest selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CpPayRequest record);

    CpPayRequest selectByUuid(String uuid);

    int updateByUuidSelective(CpPayRequest record);

    /**
     * 查询userUuid的所有已支付的百度分期订单
     *
     * @param userUuid
     * @return
     */
    List<CpPayRequest> findOrderListByUserUuid(String userUuid);

    /**
     * 关闭今天之前的未支付订单
     *
     * @return
     */
    int closeNoPayOrder();

    /**
     * 关闭课时包用户旧的未支付未关闭订单
     *
     * @param coursePriceUuid
     * @param leadsUuid
     * @return
     */
    int closeNoPayOrderByCoursePriceUuidAndLeadsUuid(@Param("coursePriceUuid") String coursePriceUuid, @Param("leadsUuid") String leadsUuid);

    CpPayRequest findPayRequestByOrderNo(String orderNo);

    OrderListDto findOrderByOrderUuid(String orderUuid);

    CpPayRequest findOrderByChargeId(String chargeId);

    int countOrderList(@Param("leadsUuid") String leadsUuid, @Param("isPay") Integer isPay, @Param("status") Integer status);

    List<OrderListDto> findOrderList(@Param("leadsUuid") String leadsUuid, @Param("isPay") Integer isPay, @Param("status") Integer status, @Param("startSize") int startSize, @Param("pageSize") int pageSize);

    /**
     * 根据leadsUuid和课程级别查询总课时
     *
     * @param leadsUuid
     * @param courseLevel
     * @return
     */
    double findCourseTimeByLeadsUuidAndCourseLevel(@Param("leadsUuid") String leadsUuid, @Param("courseLevel") int courseLevel);
}