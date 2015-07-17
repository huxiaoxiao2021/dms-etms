package com.jd.bluedragon.distribution.send.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Msg")
public class Msg{
	
	private String MsgRouter;
	private String MsgTopic;
	private String MsgBody;
	
	public Msg() {
	}
	
	public Msg(String router, String topic, String body) {
		this.MsgBody = body;
		this.MsgRouter = router;
		this.MsgTopic = topic;
	}
	
	public String getMsgRouter() {
		return MsgRouter;
	}
	
	public void setMsgRouter(String msgRouter) {
		MsgRouter = msgRouter;
	}
	
	public String getMsgTopic() {
		return MsgTopic;
	}
	
	public void setMsgTopic(String msgTopic) {
		MsgTopic = msgTopic;
	}
	
	public String getMsgBody() {
		return MsgBody;
	}
	
	public void setMsgBody(String msgBody) {
		MsgBody = msgBody;
	}
}
