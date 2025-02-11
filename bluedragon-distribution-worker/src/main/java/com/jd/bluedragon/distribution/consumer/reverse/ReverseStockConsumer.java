package com.jd.bluedragon.distribution.consumer.reverse;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveNotifyStockService;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 逆向备件库收货推出管0, 21类型通过接口调用推送
 * 11, 13, 15, 16, 18, 19, 42, 56, 61通过mq发送
 * @author huangliang
 *
 */
@Service("reverseStockConsumer")
public class ReverseStockConsumer extends MessageBaseConsumer {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ReverseReceiveNotifyStockService reverseReceiveNotifyStockService;
	
	public void consume(Message message) throws Exception {
		//取的就是订单号 不是运单号 不要被参数名所迷惑
		Long waybillCode = this.reverseReceiveNotifyStockService.receive(message.getText());
		Boolean result = this.reverseReceiveNotifyStockService.nodifyStock(waybillCode);
		
		this.log.info("Id:{}, 处理结果：{}" ,message.getBusinessId(), result);
		if(!result) throw new Exception(waybillCode+"推出管失败");
	}
}
