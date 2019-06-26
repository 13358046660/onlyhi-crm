package cn.onlyhi.client.service;

import cn.onlyhi.client.dao.CpPayRequestMapper;
import cn.onlyhi.client.dto.OrderListDto;
import cn.onlyhi.client.po.CpPayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/6.
 */
@Service
public class CpPayRequestServiceImpl implements CpPayRequestService {

	@Autowired
	private CpPayRequestMapper cpPayRequestMapper;

	@Override
	public int save(CpPayRequest cpPayRequest) {
		return cpPayRequestMapper.insertSelective(cpPayRequest);
	}

	@Override
	public CpPayRequest findByUuid(String uuid) {
		return cpPayRequestMapper.selectByUuid(uuid);
	}

	@Override
	public int update(CpPayRequest cpPayRequest) {
		return cpPayRequestMapper.updateByUuidSelective(cpPayRequest);
	}

	@Override
	public int countOrderList(String leadsUuid, Integer isPay, Integer status) {
		return cpPayRequestMapper.countOrderList(leadsUuid, isPay, status);
	}

	@Override
	public List<OrderListDto> findOrderList(String leadsUuid, Integer isPay, Integer status, int pageNo, int pageSize) {
		int startSize = (pageNo - 1) * pageSize;
		return cpPayRequestMapper.findOrderList(leadsUuid, isPay, status, startSize, pageSize);
	}

	@Override
	public CpPayRequest findPayRequestByOrderNo(String orderNo) {
		return cpPayRequestMapper.findPayRequestByOrderNo(orderNo);
	}

	@Override
	public OrderListDto findOrderByOrderUuid(String orderUuid) {
		return cpPayRequestMapper.findOrderByOrderUuid(orderUuid);
	}

	@Override
	public CpPayRequest findOrderByChargeId(String chargeId) {
		return cpPayRequestMapper.findOrderByChargeId(chargeId);
	}
	@Override
	public List<CpPayRequest> findOrderListByUserUuid(String userUuid) {
		return cpPayRequestMapper.findOrderListByUserUuid(userUuid);
	}

	@Override
	public int closeNoPayOrder() {
		return cpPayRequestMapper.closeNoPayOrder();
	}

	@Override
	public int closeNoPayOrderByCoursePriceUuidAndLeadsUuid(String coursePriceUuid,String leadsUuid) {
		return cpPayRequestMapper.closeNoPayOrderByCoursePriceUuidAndLeadsUuid(coursePriceUuid,leadsUuid);
	}

	@Override
	public double findCourseTimeByLeadsUuidAndCourseLevel(String leadsUuid, int courseLevel) {
		return cpPayRequestMapper.findCourseTimeByLeadsUuidAndCourseLevel(leadsUuid,courseLevel);
	}
}