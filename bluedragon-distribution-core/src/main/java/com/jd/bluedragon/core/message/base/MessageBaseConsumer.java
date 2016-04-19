package com.jd.bluedragon.core.message.base;

import java.util.List;

import com.jd.jmq.common.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jd.jmq.client.consumer.MessageListener;

public abstract class MessageBaseConsumer implements MessageListener{
	private final Log logger = LogFactory.getLog(this.getClass());
	
	public abstract void consume(Message message) throws Exception;
	
	/**
	 * 支持jmq方法
	 */
	public void onMessage(List<Message> jmqMessages) throws Exception {
		for (Message jmqMsg : jmqMessages) {
			//使用自己vo 进行转换
            /*
			Message message = new Message();
			message.setBusinessId(jmqMsg.getBusinessId());
			message.setConnectionSystemId(jmqMsg.getApp());
			message.setContent(jmqMsg.getText());
			message.setDestinationCode(jmqMsg.getTopic());
			*/
			logger.debug(jmqMsg.getText());

			consume(jmqMsg);
		}
	}
}
