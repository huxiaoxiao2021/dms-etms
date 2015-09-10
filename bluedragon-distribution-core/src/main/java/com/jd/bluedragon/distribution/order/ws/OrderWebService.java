package com.jd.bluedragon.distribution.order.ws;

import java.util.Collections;
import java.util.List;

import jd.oom.client.clientbean.Order;
import jd.oom.client.core.OrderLoadFlag;
import jd.oom.client.orderfile.OrderArchiveInfo;

import org.springframework.stereotype.Service;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.utils.OrderServiceHelper;
import com.jd.bluedragon.utils.SpringHelper;

@Service("orderWebService")
public class OrderWebService {

	public List<jd.oom.client.clientbean.OrderDetail> getOrderDetailById(long orderId) {
		jd.oom.client.clientbean.ServiceSoap oomServiceSoap = (jd.oom.client.clientbean.ServiceSoap) SpringHelper
		        .getBean("oomServiceSoap");
		jd.oom.client.clientbean.ArrayOfOrderDetail orderDetail = oomServiceSoap
		        .getOrderDetailById(orderId);

		if (orderDetail.getOrderDetail() != null && orderDetail.getOrderDetail().size() > 0) {
			return orderDetail.getOrderDetail();
		}

		return Collections.emptyList();
	}

	public List<jd.oom.client.orderfile.OrderDetail> getHistoryOrderDetailById(long orderId) {
		jd.oom.client.orderfile.ServiceSoap oomOrderFileServerSoap = (jd.oom.client.orderfile.ServiceSoap) SpringHelper
		        .getBean("oomOrderFileServerSoap");

		OrderArchiveInfo orderArchiveInfo = oomOrderFileServerSoap.getOrderArchiveInfo(orderId,
		        OrderLoadFlag.getLoadFlag("全部"));

		if (orderArchiveInfo != null && orderArchiveInfo.getOrder() != null
		        && orderArchiveInfo.getOrder().getDetails() != null) {
			return orderArchiveInfo.getOrder().getDetails().getOrderDetail();
		}

		return Collections.emptyList();
	}

	public Waybill getHisWaybillByOrderId(long orderId) {
		jd.oom.client.orderfile.ServiceSoap oomOrderFileServerSoap = (jd.oom.client.orderfile.ServiceSoap) SpringHelper
		        .getBean("oomOrderFileServerSoap");

		OrderArchiveInfo orderArchiveInfo = oomOrderFileServerSoap.getOrderArchiveInfo(orderId,
		        OrderLoadFlag.getLoadFlag("全部"));

		if (orderArchiveInfo != null && orderArchiveInfo.getOrder() != null) {
			jd.oom.client.orderfile.Order order = orderArchiveInfo.getOrder();
			Waybill waybill = new Waybill();
			waybill.setWaybillCode(String.valueOf(orderId));
			waybill.setSiteCode(order.getPartnerId());
			waybill.setSiteName(order.getIdPickSiteName());
			waybill.setPaymentType(order.getIdPaymentType());
			waybill.setSendPay(order.getSendPay());
			if (order.getWeight() != null) {
				waybill.setWeight(order.getWeight().doubleValue());
			}
			waybill.setAddress(order.getAddress());
			waybill.setOrgId(order.getIdCompanyBranch());
			waybill.setStoreId(order.getStoreId());
			waybill.setType(order.getOrderType());
			waybill.setShipmentType(order.getIdShipmentType());
			return waybill;
		}

		return null;
	}

	public Order getOrder(long orderId) {
		jd.oom.client.clientbean.ServiceSoap oomServiceSoap = (jd.oom.client.clientbean.ServiceSoap) SpringHelper
		        .getBean("oomServiceSoap");
		return oomServiceSoap.getOrderById(orderId, false, OrderLoadFlag.getLoadFlag("全部"));
	}

	public jd.oom.client.orderfile.Order getHistoryOrder(long orderId) {
		jd.oom.client.orderfile.ServiceSoap oomOrderFileServerSoap = (jd.oom.client.orderfile.ServiceSoap) SpringHelper
		        .getBean("oomOrderFileServerSoap");
		
		OrderArchiveInfo orderArchiveInfo = oomOrderFileServerSoap.getOrderArchiveInfo(orderId,
		        OrderLoadFlag.getLoadFlag("全部"));
		if (orderArchiveInfo != null){
			return orderArchiveInfo.getOrder();
		}
		
		return null;
	}
	
	public Waybill getWaybillByOrderId(long orderId) {
		jd.oom.client.clientbean.ServiceSoap oomServiceSoap = (jd.oom.client.clientbean.ServiceSoap) SpringHelper
		        .getBean("oomServiceSoap");

		jd.oom.client.clientbean.Order order = oomServiceSoap.getOrderById(orderId, false,
		        OrderServiceHelper.getFlag1());

		if (order != null) {
			Waybill waybill = new Waybill();
			waybill.setWaybillCode(String.valueOf(orderId));
			waybill.setSiteCode(order.getPartnerId());
			waybill.setSiteName(order.getIdPickSiteName());
			waybill.setPaymentType(order.getIdPaymentType());
			waybill.setSendPay(order.getSendPay());
			if (order.getWeight() != null) {
				waybill.setWeight(order.getWeight().doubleValue());
			}
			waybill.setAddress(order.getAddress());
			waybill.setOrgId(order.getIdCompanyBranch());
			waybill.setStoreId(order.getStoreId());
			waybill.setType(order.getOrderType());
			waybill.setShipmentType(order.getIdShipmentType());
			waybill.setReceiverTel(order.getPhone());
			waybill.setReceiverMobile(order.getMobile());
			waybill.setReceiverName(order.getCustomerName());
			return waybill;
		}

		return null;
	}

}