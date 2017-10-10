package com.jd.bluedragon.core.message.base;

import java.util.List;

import com.jd.bluedragon.core.message.MessageException;

@Deprecated
public class MessageBaseConsumerFactory extends MessageBaseAbstractFactory {
	
	List<MessageBaseConfig> msgConfigs;

	public List<MessageBaseConfig> getMsgConfigs() {
		return msgConfigs;
	}

	public void setMsgConfigs(List<MessageBaseConfig> msgConfigs) {
		this.msgConfigs = msgConfigs;
	}
	
    public MessageBaseConsumer createMessageConsumer(String destination, String systemId) {
    	
        if (destination == null) {
            throw new MessageException("Message destination must not be null.");
        }

        if (systemId == null) {
            throw new MessageException("Message consumer must not be null.");
        }

        //遍历所有配置,返回对应的消费类
        for(MessageBaseConfig config : msgConfigs){
        	if(destination.equals(config.getDestination())&&systemId.equals(config.getSystemId()))
        		return config.getConsumer();
        }
        
       throw new MessageException("Message consumer not found.");
    }

}
