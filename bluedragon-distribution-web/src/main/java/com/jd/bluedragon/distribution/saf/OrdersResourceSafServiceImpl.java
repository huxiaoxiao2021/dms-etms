package com.jd.bluedragon.distribution.saf;

import com.jd.bluedragon.distribution.rest.orders.OrdersResource;
import com.jd.bluedragon.distribution.sorting.domain.OrderDetailEntityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrdersResourceSafServiceImpl implements OrdersResourceSafService{
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private OrdersResource ordersResource;
	
	public OrderDetailEntityResponse getOrdersDetails(String boxCode,
			String startTime, String endTime, String createSiteCode,
			String receiveSiteCode) {
		log.debug("com.jd.bluedragon.distribution.saf.OrdersResourceSafServiceImpl.getOrdersDetails---start!");
		return ordersResource.getOrdersDetails(boxCode, startTime, endTime, createSiteCode, receiveSiteCode);
	}

	public void setOrdersResource(OrdersResource ordersResource) {
		this.ordersResource = ordersResource;
	}
	
}
