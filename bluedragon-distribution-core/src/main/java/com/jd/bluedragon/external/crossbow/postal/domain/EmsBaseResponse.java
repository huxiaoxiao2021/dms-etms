package com.jd.bluedragon.external.crossbow.postal.domain;

import java.io.Serializable;

/**
 * <p>
 *     响应基础返回对象
 *
 * @author wuyoude
 **/
public class EmsBaseResponse implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 接收方标识
	 */
	private String receiveID;
	/**
	 * 调用接口的执行结果
	 */
	private Boolean responseState;
	/**
	 * 错误描述信息
	 */
	private String errorDesc;
	/**
	 * 判断是否成功
	 * @return
	 */
	public boolean checkSucceed() {
		return Boolean.TRUE.equals(responseState);
	}
	
	public String getReceiveID() {
		return receiveID;
	}
	public void setReceiveID(String receiveID) {
		this.receiveID = receiveID;
	}
	public Boolean getResponseState() {
		return responseState;
	}
	public void setResponseState(Boolean responseState) {
		this.responseState = responseState;
	}
	public String getErrorDesc() {
		return errorDesc;
	}
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}
	
	
}
