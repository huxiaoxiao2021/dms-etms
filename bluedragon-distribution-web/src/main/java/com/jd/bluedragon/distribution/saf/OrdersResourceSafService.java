package com.jd.bluedragon.distribution.saf;

import com.jd.bluedragon.distribution.sorting.domain.OrderDetailEntityResponse;

public interface OrdersResourceSafService {

	public OrderDetailEntityResponse getOrdersDetails(String boxCode,
			String startTime, String endTime, String createSiteCode,
			String receiveSiteCode);
}
