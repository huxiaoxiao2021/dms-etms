package com.jd.bluedragon.core.message.base;

import java.util.List;

import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.jmq.common.message.Message;
import com.jd.jspliter.jmq.consumer.EnvMessageListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;

public abstract class MessageBaseConsumer extends EnvMessageListener {
	private final Log logger = LogFactory.getLog(this.getClass());
	
	public abstract void consume(Message message) throws Exception;

	//JMQ Split UAT标识开关
	@Value("${jmq.split.uat.switch:false}")
	@Override
	public void setUat(String uat) {
		super.setUat(uat);
	}

	/**
	 * 支持jmq方法
	 */
	@Override
	public void onMessages(List<Message> jmqMessages) throws Exception {
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
