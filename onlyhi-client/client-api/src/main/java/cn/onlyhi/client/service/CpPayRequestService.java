package cn.onlyhi.client.service;


import cn.onlyhi.client.dto.OrderListDto;
import cn.onlyhi.client.po.CpPayRequest;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
public interface CpPayRequestService {

    int save(CpPayRequest cpPayRequest);

    CpPayRequest findByUuid(String uuid);

    /**
     * 根据uuid更新
     *
     * @param cpPayRequest
     * @return
     */
    int update(CpPayRequest cpPayRequest);

    /**
     * 获取订单列表总数
     *
     * @param leadsUuid
     * @param isPay
     * @param status
     * @return
     */
    int countOrderList(String leadsUuid, Integer isPay, Integer status);

    /**
     * 分页获取订单列表
     *
     * @param leadsUuid
     * @param isPay
     * @param status
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<OrderListDto> findOrderList(String leadsUuid, Integer isPay, Integer status, int pageNo, int pageSize);

    /**
     * 根据订单号查询订单信息
     *
     * @param orderNo
     * @return
     */
    CpPayRequest findPayRequestByOrderNo(String orderNo);

    /**
     * 根据订单uuid查找订单信息
     *
     * @param orderUuid
     * @return
     */
    OrderListDto findOrderByOrderUuid(String orderUuid);

    /**
     * 根据ping++订单chargeId查询订单信息
     *
     * @param chargeId
     * @return
     */
    CpPayRequest findOrderByChargeId(String chargeId);

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
     * @return
     */
    int closeNoPayOrderByCoursePriceUuidAndLeadsUuid(String coursePriceUuid,String leadsUuid);

    /**
     * 根据leadsUuid和课程级别查询总课时
     *
     * @param leadsUuid
     * @param courseLevel
     * @return
     */
    double findCourseTimeByLeadsUuidAndCourseLevel(String leadsUuid, int courseLevel);
}