package com.jd.bluedragon.core.message.base;

import com.jd.jmq.common.message.Message;
import com.jd.jspliter.jmq.consumer.EnvMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public abstract class MessageBaseConsumer extends EnvMessageListener {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
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
			if(checkMessage(jmqMsg)){
				if(log.isDebugEnabled()){
					this.log.debug("[{}-{}]:[{}]",jmqMsg.getTopic(),jmqMsg.getBusinessId(),jmqMsg.getText());
				}
				consume(jmqMsg);
			}
		}
	}
	/**
	 * 校验消息体
	 * @param jmqMsg
	 * @return
	 */
	protected boolean checkMessage(Message jmqMsg){
        if(jmqMsg == null || null == jmqMsg.getText() || "".equals(jmqMsg.getText()) ){
        	this.log.warn("[{}-{}]:消息体为空！",jmqMsg.getTopic(),jmqMsg.getBusinessId());
            return false;
        }
		return true;
	}
}
