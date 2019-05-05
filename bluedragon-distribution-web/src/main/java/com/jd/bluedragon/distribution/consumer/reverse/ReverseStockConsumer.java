package com.jd.bluedragon.distribution.consumer.reverse;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.reverse.service.ReverseReceiveNotifyStockService;
import com.jd.jmq.common.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * 逆向备件库收货推出管0, 21类型通过接口调用推送
 * 11, 13, 15, 16, 18, 19, 42, 56, 61通过mq发送
 * @author huangliang
 *
 */
@Service("reverseStockConsumer")
public class ReverseStockConsumer extends MessageBaseConsumer {
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private ReverseReceiveNotifyStockService reverseReceiveNotifyStockService;
	
	public void consume(Message message) throws Exception {
		Long waybillCode = this.reverseReceiveNotifyStockService.receive(message.getText());
		Boolean result = this.reverseReceiveNotifyStockService.nodifyStock(waybillCode);
		
		this.logger.info(MessageFormat.format("Id[{0}]waybillCode[{1}]处理结果[{2}]",message.getBusinessId(),waybillCode,result));
		if(!result) throw new Exception(waybillCode+"推出管失败");
	}
}
