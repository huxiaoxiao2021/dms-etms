package com.jd.bluedragon.distribution.fastRefund.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Result")
public class QrderCancelResult {

	Boolean flag;
	String errorMessage;
	Integer errorCode;/*0：参数错误；1：订单取消受理中,不能重复取消；2：订单已取消,不能重复取消*/
	
	public static final int CODE_PARAM = 0;
	public static final int CODE_REPEAT = 1;
	public static final int CODE_CANCEL = 2;
	
	public Boolean getFlag() {
		return flag;
	}
	public void setFlag(Boolean flag) {
		this.flag = flag;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public Integer getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}
}
