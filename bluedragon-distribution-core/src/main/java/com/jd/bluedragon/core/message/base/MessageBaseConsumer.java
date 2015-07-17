package com.jd.bluedragon.core.message.base;

import com.jd.etms.message.Consumer;
import com.jd.etms.message.Message;

public abstract class MessageBaseConsumer implements Consumer {
	
	public abstract void consume(Message message) throws Exception;
	
}
