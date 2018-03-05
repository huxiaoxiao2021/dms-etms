package com.jd.bluedragon.distribution.command;
/**
 * 
 * @ClassName: JdMessage
 * @Description: 消息定义实体
 * @author: wuyoude
 * @date: 2018年1月28日 下午2:32:43
 *
 */
public class JdMessage {
	/**
	 * 消息编码
	 */
	private int msgCode;
	/**
	 * 消息格式
	 */
	private String msgFormat;
	/**
	 * 默认设置消息格式为：msgCode-消息内容
	 * @param msgCode
	 */
	public JdMessage(int msgCode) {
		super();
		this.msgCode = msgCode;
		this.msgFormat = msgCode+"-%s";
	}
	/**
	 * 默认设置消息格式为：msgCode-msgFormat
	 * @param msgCode
	 */
	public JdMessage(int msgCode, String msgFormat) {
		super();
		this.msgCode = msgCode;
		if(msgFormat != null){
			this.msgFormat = msgCode+"-" + msgFormat;
		}
	}
	/**
	 * 格式化消息
	 * @param args
	 * @return
	 */
	public String formatMsg(Object ... args){
		return String.format(msgFormat, args);
	}

	/**
	 * @return the msgCode
	 */
	public int getMsgCode() {
		return msgCode;
	}

	/**
	 * @param msgCode the msgCode to set
	 */
	public void setMsgCode(int msgCode) {
		this.msgCode = msgCode;
	}

	/**
	 * @return the msgFormat
	 */
	public String getMsgFormat() {
		return msgFormat;
	}

	/**
	 * @param msgFormat the msgFormat to set
	 */
	public void setMsgFormat(String msgFormat) {
		this.msgFormat = msgFormat;
	}
}
