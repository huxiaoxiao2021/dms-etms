package com.jd.bluedragon.distribution.external.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumerFactory;
import com.jd.etms.message.Consumer;
import com.jd.etms.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

public class MessageConumerServiceImpl implements Consumer {
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	private MessageBaseConsumerFactory consumerFactory;

	public void setConsumerFactory(MessageBaseConsumerFactory consumerFactory) {
		this.consumerFactory = consumerFactory;
	}
	@JProfiler(jKey = "DMSWEB.MessageConumerServiceImpl.consume", mState = {JProEnum.TP})
	public void consume(Message message) throws Exception {
		this.logger.info("jsf Message:" + this.getMessage(message));
		
		if (message == null || message.getDestinationCode() == null || message.getConnectionSystemId() == null
				|| message.getContent() == null) {
			return;
		}
		
		MessageBaseConsumer messageBaseConsumer = this.consumerFactory.createMessageConsumer(
				message.getDestinationCode(), message.getConnectionSystemId());
		messageBaseConsumer.consume(message);
	}
	
	public String getMessage(Message message) {
		return "Destination:" + message.getDestinationCode() + ", Consumer:" + message.getConnectionSystemId()
				+ ", Id:" + message.getId() + ", Content:" + message.getContent() + ", Type:"
				+ message.getDestinationType();
	}
}
