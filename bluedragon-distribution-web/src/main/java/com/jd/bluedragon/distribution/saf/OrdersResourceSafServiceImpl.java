package com.jd.bluedragon.distribution.saf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jd.bluedragon.distribution.rest.orders.OrdersResource;
import com.jd.bluedragon.distribution.sorting.domain.OrderDetailEntityResponse;

public class OrdersResourceSafServiceImpl implements OrdersResourceSafService{
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	private OrdersResource ordersResource;
	
	public OrderDetailEntityResponse getOrdersDetails(String boxCode,
			String startTime, String endTime, String createSiteCode,
			String receiveSiteCode) {
		logger.info("com.jd.bluedragon.distribution.saf.OrdersResourceSafServiceImpl.getOrdersDetails---start!");
		return ordersResource.getOrdersDetails(boxCode, startTime, endTime, createSiteCode, receiveSiteCode);
	}

	public void setOrdersResource(OrdersResource ordersResource) {
		this.ordersResource = ordersResource;
	}
	
}
