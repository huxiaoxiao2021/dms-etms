package com.jd.bluedragon.distribution.order.ws;

import java.util.Collections;
import java.util.List;

import com.jd.bluedragon.core.base.PreseparateWaybillManager;
import com.jd.bluedragon.distribution.base.service.BaseService;
import jd.oom.client.clientbean.Order;
import jd.oom.client.core.OrderLoadFlag;
import jd.oom.client.orderfile.OrderArchiveInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.utils.OrderServiceHelper;
import com.jd.bluedragon.utils.SpringHelper;
import com.jd.ql.basic.domain.Assort;

@Service("orderWebService")
public class OrderWebService {

	@Autowired
	private  BaseService baseService;

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private PreseparateWaybillManager preseparateWaybillManager;

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

			try {
				waybill.setSiteCode(preseparateWaybillManager.getPreseparateSiteId(String.valueOf(orderId)));
				logger.info("快生从预分拣获取预分拣站点"+orderId +"-"+waybill.getSiteCode());
			}catch (Exception ex){
				waybill.setSiteCode(order.getPartnerId());
				logger.error("快生预分拣接口异常"+orderId,ex);
			}
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
			waybill.setDistributeStoreId(order.getStoreId());
			waybill.setDistributeStoreName(order.getStoreName());
			// region  从rdeis中获取省市县，若无则从基础资料获取，并插入redis中
			waybill.setProvinceNameId(order.getProvince());
			waybill.setProvinceName(GetCityName(waybill.getProvinceNameId()));
			waybill.setCityNameId(order.getCity());
			waybill.setCityName(GetCityName(waybill.getCityNameId()));
			waybill.setCountryNameId(order.getCounty());
			waybill.setCountryName(GetCityName(waybill.getCountryNameId()));
			// endregion
			return waybill;
		}

		return null;
	}

	private String GetCityName(Integer id) {
		String temp = null;
		if (id != null) {
			Assort ass = baseService.getAssortById(id);
			if (ass != null)
				temp = ass.getAssName();
		}
		return temp;
	}


}