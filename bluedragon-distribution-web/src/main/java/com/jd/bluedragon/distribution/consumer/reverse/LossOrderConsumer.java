package com.jd.bluedragon.distribution.consumer.reverse;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.reverse.domain.LossOrder;
import com.jd.bluedragon.distribution.reverse.service.LossOrderService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

	private final Log logger = LogFactory.getLog(this.getClass());

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

                Object orderId = lossOrderMap.get("orderId");
                if (!NumberHelper.isNumber(String.valueOf(orderId))) {
                    logger.warn("消费逆向报损单MQ时orderId非订单号，无法入库：" + orderId + "，消息内容:" + JsonHelper.toJson(lossOrderDetailMap));
                    continue;
                }
                lossOrder.setOrderId(NumberHelper.getLongValue(orderId));

                String productId = lossOrderDetailMap.get("productId").toString();
                //上游给productId设的值默认是SKU，有时会把SKU和productId拼接起来，统一取SKU
                if(!NumberHelper.isNumber(productId)){
                    logger.error("消费逆向报损单MQ时productId非法：" + productId);
                    if(productId.contains("_")){
                        productId = productId.split("_")[0];
                    }else {
                        productId = "0";//如果出现其它的情况，给赋值为0，保证数据可以存进去
                    }
                }
				lossOrder.setLossCode(NumberHelper.getIntegerValue(lossOrderMap.get("id")));
				lossOrder.setUserErp(StringHelper.getStringValue(lossOrderMap.get("userErp")));
				lossOrder.setUserName(StringHelper.getStringValue(lossOrderMap.get("userName")));
				lossOrder.setOrderId(NumberHelper.getLongValue(lossOrderMap.get("orderId")));
				lossOrder.setProductId(NumberHelper.getLongValue(productId));
				lossOrder.setProductName(StringHelper.getStringValue(lossOrderDetailMap.get("productName")));
				lossOrder.setProductQuantity(NumberHelper.getIntegerValue(lossOrderDetailMap.get("productQuantity")));
				lossOrder.setLossQuantity(NumberHelper.getIntegerValue(lossOrderDetailMap.get("lossQuantity")));
				lossOrder.setLossType(NumberHelper.getIntegerValue(lossOrderDetailMap.get("lossType")));
				lossOrder.setLossTime(DateHelper.toDate(NumberHelper.getLongValue(lossOrderMap.get("lossTime"))));

				lossOrders.add(lossOrder);
			}
		}

		return lossOrders;
	}
}
