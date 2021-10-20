package com.jd.bluedragon.distribution.board.domain;

import java.io.Serializable;

/**
 * 返回结果-消息对象
 * @author wuyoude
 *
 */
public class ResultMsg implements Serializable{
	private static final long serialVersionUID = -4114836540070471184L;
	
	public ResultMsg() {
		super();
	}
	public ResultMsg(Integer messageType, String messageCode, String message) {
		this();
		this.messageType = messageType;
		this.messageCode = messageCode;
		this.message = message;
	}

	/**
	 * 消息类型
	 */
	private Integer messageType;
	/**
	 * 消息码
	 */
	private String messageCode;
	/**
	 * 消息内容
	 */
	private String message;

	public Integer getMessageType() {
		return messageType;
	}

	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
