package com.jd.bluedragon.distribution.consumer.reverse;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.reverse.domain.LossOrder;
import com.jd.bluedragon.distribution.reverse.service.LossOrderService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service("lossOrderConsumer")
public class LossOrderConsumer extends MessageBaseConsumer {

	@Autowired
	private LossOrderService lossOrderService;

	public void consume(Message message) {
		List<LossOrder> lossOrders = this.getLossOrders(message);
		for (LossOrder lossOrder : lossOrders) {
			this.lossOrderService.add(lossOrder);
		}
	}

	@SuppressWarnings({ "unchecked" })
	public List<LossOrder> getLossOrders(Message message) {
		if (message == null || message.getText() == null) {
			return Collections.emptyList();
		}

		List<LossOrder> lossOrders = new ArrayList<LossOrder>();

		Object[] array = JsonHelper.jsonToArray(message.getText(), Object[].class);
		for (Object element : array) {
			Map<String, Object> lossOrderMap = (Map<String, Object>) element;
			if (lossOrderMap == null) {
				continue;
			}

			List<Map<String, Object>> lossOrderDetails = (List<Map<String, Object>>) lossOrderMap
			        .get("productList");
			if (lossOrderDetails == null) {
				continue;
			}

			for (Map<String, Object> lossOrderDetailMap : lossOrderDetails) {
				LossOrder lossOrder = new LossOrder();
				lossOrder.setLossCode(NumberHelper.getIntegerValue(lossOrderMap.get("id")));
				lossOrder.setUserErp(StringHelper.getStringValue(lossOrderMap.get("userErp")));
				lossOrder.setUserName(StringHelper.getStringValue(lossOrderMap.get("userName")));
				lossOrder.setOrderId(NumberHelper.getLongValue(lossOrderMap.get("orderId")));
				lossOrder.setProductId(NumberHelper.getLongValue(lossOrderDetailMap
				        .get("productId")));
				lossOrder.setProductName(StringHelper.getStringValue(lossOrderDetailMap
				        .get("productName")));
				lossOrder.setProductQuantity(NumberHelper.getIntegerValue(lossOrderDetailMap
				        .get("productQuantity")));
				lossOrder.setLossQuantity(NumberHelper.getIntegerValue(lossOrderDetailMap
				        .get("lossQuantity")));
				lossOrder.setLossType(NumberHelper.getIntegerValue(lossOrderDetailMap
				        .get("lossType")));
				lossOrder.setLossTime(DateHelper.toDate(NumberHelper.getLongValue(lossOrderMap
				        .get("lossTime"))));

				lossOrders.add(lossOrder);
			}
		}

		return lossOrders;
	}
}
