package com.jd.bluedragon.core.message.consumer;

import com.jd.etms.message.consumer.Consumer;
import com.jd.etms.message.consumer.MessageDto;

public abstract class MessageConsumer implements Consumer {
	
	public abstract void consume(MessageDto message) throws Exception;
	
}
