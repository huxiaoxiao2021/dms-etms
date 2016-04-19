package com.jd.bluedragon.core.message.consumer.pop;

import com.jd.jmq.common.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.request.PopPickupRequest;
import com.jd.bluedragon.distribution.popPickup.service.PopPickupService;
import com.jd.bluedragon.utils.JsonHelper;

@Service("popPickupConsumer")
public class PopPickupConsumer  extends MessageBaseConsumer {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private PopPickupService popPickupService;

	public void consume(Message message) {
		// 处理消息体
		this.logger.info("PopPickupMessageConsumer consume --> 消息Body为【"
											+ message.getText() + "】");
		PopPickupRequest popPickup = JsonHelper.fromJson(message.getText(),PopPickupRequest.class);
		
		if(popPickup.getPackageBarcode() != null){
			popPickupService.doPickup(popPickup);
		}else{
			this.logger.info("PopPickupMessageConsumer consume : 包裹号为空，数据丢弃！");
		}
	}

}
