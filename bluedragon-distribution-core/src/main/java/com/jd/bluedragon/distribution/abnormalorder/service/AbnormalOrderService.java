package com.jd.bluedragon.distribution.abnormalorder.service;

import java.util.HashMap;

import com.jd.bluedragon.distribution.abnormalorder.domain.AbnormalOrder;
import com.jd.bluedragon.distribution.api.response.RefundReason;

public interface AbnormalOrderService {
	
	HashMap<String/*运单号*/,Integer/*操作结果*/> pushNewDataFromPDA(AbnormalOrder[] abnormalOrders);
	
	AbnormalOrder queryAbnormalOrderByOrderId(String orderId);
	
	RefundReason[] queryRefundReason();
	
	void updateResult(AbnormalOrder abnormalOrder);
	
	void clear();
}
