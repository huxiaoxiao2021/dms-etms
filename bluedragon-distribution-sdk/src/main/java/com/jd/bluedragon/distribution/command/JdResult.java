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
	
	public static final Integer CODE_SUC = 200;
	public static final JdMessage MESSAGE_SUC = new JdMessage(CODE_SUC,"操作成功");
	public static final Integer CODE_WARN = 300;
	public static final JdMessage MESSAGE_WARN = new JdMessage(CODE_WARN,"操作警告");
	public static final Integer CODE_FAIL = 400;
	public static final JdMessage MESSAGE_FAIL = new JdMessage(CODE_FAIL,"操作失败");
	public static final Integer CODE_ERROR = 500;
	public static final JdMessage MESSAGE_ERROR = new JdMessage(CODE_ERROR,"操作异常");
	
	/**
	 * 状态
	 */
	private Integer code;
	/**
	 * 返回消息编码
	 */
	private Integer messageCode;
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
	public JdResult(Integer code, Integer messageCode, String message) {
		super();
		this.code = code;
		this.messageCode = messageCode;
		this.message = message;
	}
	/**
	 * 判断是否成功
	 * @return
	 */
	public boolean isSucceed(){
		return CODE_SUC.equals(this.code) || CODE_WARN.equals(this.code);
	}

    /**
     * 判断是否失败
     * @return
     */
	public boolean isFailed() {
	    return !isSucceed();
    }
	/**
	 * 判断是否成功
	 * @return
	 */
	public boolean isWarn(){
		return CODE_WARN.equals(this.code);
	}
	/**
	 * 更改状态为成功
	 */
	public void toSuccess(){
		this.toSuccess(CODE_SUC, MESSAGE_SUC.formatMsg());
	}
	/**
	 * 更改状态为成功,并设置返回消息编码和内容
	 * @param message
	 */
	public void toSuccess(String message){
		this.toSuccess(CODE_SUC, message);
	}
	/**
	 * 更改状态为成功,并设置返回消息编码和内容
	 * @param messageCode
	 * @param message
	 */
	public void toSuccess(Integer messageCode,String message){
		this.code = CODE_SUC;
		this.messageCode = messageCode;
		this.message = message;
	}
	/**
	 * 更改状态为警告
	 */
	public void toWarn(){
		this.toWarn(CODE_WARN, MESSAGE_WARN.formatMsg());
	}
	/**
	 * 更改状态为警告,并设置返回消息编码和内容
	 * @param message
	 */
	public void toWarn(String message){
		this.toWarn(CODE_WARN, message);
	}
	/**
	 * 更改状态为警告,并设置返回消息编码和内容
	 * @param messageCode
	 * @param message
	 */
	public void toWarn(Integer messageCode,String message){
		this.code = CODE_WARN;
		this.messageCode = messageCode;
		this.message = message;
	}
	/**
	 * 更改状态为失败
	 */
	public void toFail(){
		this.toFail(CODE_FAIL, MESSAGE_FAIL.formatMsg());
	}
	/**
	 * 更改状态为失败,并设置返回消息内容
	 * @param message
	 */
	public void toFail(String message){
		this.toFail(CODE_FAIL, message);
	}
	/**
	 * 更改状态为失败,并设置返回消息编码和内容
	 * @param messageCode
	 * @param message
	 */
	public void toFail(Integer messageCode,String message){
		this.code = CODE_FAIL;
		this.messageCode = messageCode;
		this.message = message;
	}
	/**
	 * 更改状态为异常
	 */
	public void toError(){
		this.toError(CODE_ERROR, MESSAGE_ERROR.formatMsg());
	}
	/**
	 * 更改状态为异常,并设置返回消息内容
	 * @param message
	 */
	public void toError(String message){
		this.toError(CODE_ERROR, message);
	}
	/**
	 * 更改状态为异常,并设置返回消息编码和内容
	 * @param messageCode
	 * @param message
	 */
	public void toError(Integer messageCode,String message){
		this.code = CODE_ERROR;
		this.messageCode = messageCode;
		this.message = message;
	}
	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
	}
	/**
	 * @return the messageCode
	 */
	public Integer getMessageCode() {
		return messageCode;
	}
	/**
	 * @param messageCode the messageCode to set
	 */
	public void setMessageCode(Integer messageCode) {
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
