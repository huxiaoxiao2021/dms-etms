package com.jd.bluedragon.distribution.consumer.pop;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.request.PopPickupRequest;
import com.jd.bluedragon.distribution.popPickup.service.PopPickupService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("popPickupConsumer")
public class PopPickupConsumer  extends MessageBaseConsumer {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PopPickupService popPickupService;

	public void consume(Message message) {
		// 处理消息体
		PopPickupRequest popPickup = JsonHelper.fromJson(message.getText(),PopPickupRequest.class);
		
		if(popPickup.getPackageBarcode() != null){
			popPickupService.doPickup(popPickup);
		}else{
			this.log.warn("PopPickupMessageConsumer consume : 包裹号为空，数据丢弃:{}", message.getText());
		}
	}

}
