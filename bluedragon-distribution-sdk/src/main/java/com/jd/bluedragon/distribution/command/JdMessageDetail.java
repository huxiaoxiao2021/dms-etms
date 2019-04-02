package com.jd.bluedragon.distribution.command;

import java.io.Serializable;

/**
 * 
 * @ClassName: MessageDetail
 * @Description: 详细信息
 * @author: wuyoude
 * @date: 2019年3月4日 下午6:03:32
 *
 */
public class JdMessageDetail implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 无参构造器
	 */
	public JdMessageDetail(){
		super();
	}
	/**
	 * 有参构造器
	 * @param messageKey
	 * @param messageContent
	 */
	public JdMessageDetail(String messageKey, String messageContent) {
		super();
		this.messageKey = messageKey;
		this.messageContent = messageContent;
	}

	/**
	 * 信息的key
	 */
	private String messageKey;
	/**
	 * 信息内容
	 */
	private String messageContent;
	/**
	 * @return the messageKey
	 */
	public String getMessageKey() {
		return messageKey;
	}
	/**
	 * @param messageKey the messageKey to set
	 */
	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}
	/**
	 * @return the messageContent
	 */
	public String getMessageContent() {
		return messageContent;
	}
	/**
	 * @param messageContent the messageContent to set
	 */
	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}
}
