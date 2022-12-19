package com.jd.bluedragon.distribution.base.controller;

import java.io.Serializable;

public class JmqMessage implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    // 主题
    protected String topic;
    // 业务ID
    protected String businessId;
    // 文本
    protected String text;
    
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getBusinessId() {
		return businessId;
	}
	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
