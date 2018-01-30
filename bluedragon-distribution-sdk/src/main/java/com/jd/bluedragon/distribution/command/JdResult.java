package com.jd.bluedragon.distribution.command;

import java.io.Serializable;

/**
 * 
 * @ClassName: JdResult
 * @Description: 处理结果类
 * @author: wuyoude
 * @date: 2018年1月25日 下午11:00:07
 *
 */
public class JdResult<T> implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public static final int CODE_SUC = 200;
	public static final JdMessage MESSAGE_SUC = new JdMessage(CODE_SUC,"操作成功");
	public static final int CODE_FAIL = 400;
	public static final JdMessage MESSAGE_FAIL = new JdMessage(CODE_FAIL,"操作失败");
	public static final int CODE_ERROR = 500;
	public static final JdMessage MESSAGE_ERROR = new JdMessage(CODE_ERROR,"操作异常");
	
	/**
	 * 状态
	 */
	private int code;
	/**
	 * 返回消息编码
	 */
	private int messageCode;
	/**
	 * 返回消息内容
	 */
	private String message;
	/**
	 * 返回结果
	 */
	private T data;
	public JdResult() {
		super();
	}
	public JdResult(int code, int messageCode, String message) {
		super();
		this.code = code;
		this.messageCode = messageCode;
		this.message = message;
	}
	/**
	 * 更改状态为成功
	 */
	public void toSuccess(){
		this.toSuccess(MESSAGE_SUC.getMsgCode(), MESSAGE_SUC.formatMsg());
	}
	/**
	 * 更改状态为成功,并设置返回消息编码和内容
	 * @param messageCode
	 * @param message
	 */
	public void toSuccess(int messageCode,String message){
		this.code = CODE_SUC;
		this.messageCode = messageCode;
		this.message = message;
	}
	/**
	 * 更改状态为失败
	 */
	public void toFail(){
		this.toFail(MESSAGE_FAIL.getMsgCode(), MESSAGE_FAIL.formatMsg());
	}
	/**
	 * 更改状态为失败,并设置返回消息编码和内容
	 * @param messageCode
	 * @param message
	 */
	public void toFail(int messageCode,String message){
		this.code = CODE_FAIL;
		this.messageCode = messageCode;
		this.message = message;
	}
	/**
	 * 更改状态为异常
	 */
	public void toError(){
		this.toError(MESSAGE_ERROR.getMsgCode(), MESSAGE_ERROR.formatMsg());
	}
	/**
	 * 更改状态为异常,并设置返回消息编码和内容
	 * @param messageCode
	 * @param message
	 */
	public void toError(int messageCode,String message){
		this.code = CODE_ERROR;
		this.messageCode = messageCode;
		this.message = message;
	}
	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}
	/**
	 * @return the messageCode
	 */
	public int getMessageCode() {
		return messageCode;
	}
	/**
	 * @param messageCode the messageCode to set
	 */
	public void setMessageCode(int messageCode) {
		this.messageCode = messageCode;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the data
	 */
	public T getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(T data) {
		this.data = data;
	}

}
