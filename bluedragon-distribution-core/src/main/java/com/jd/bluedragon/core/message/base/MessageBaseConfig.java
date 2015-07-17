package com.jd.bluedragon.core.message.base;

public class MessageBaseConfig {
	private String destination;
	private String systemId;
	private MessageBaseConsumer consumer;
	private String desc;

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public MessageBaseConsumer getConsumer() {
		return consumer;
	}

	public void setConsumer(MessageBaseConsumer consumer) {
		this.consumer = consumer;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
