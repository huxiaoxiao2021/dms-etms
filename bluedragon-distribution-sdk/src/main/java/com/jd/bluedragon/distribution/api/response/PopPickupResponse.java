package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

public class PopPickupResponse<T> extends JdResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5776774999565144013L;
	public static final Integer CODE_NOT_MATCH = 30001;
	public static final String MESSAGE_NOT_MATCH = "没有匹配的数据";
	
	private Integer packageNumbers;
	private String popBusinessName;
	private String popBusinessCode;
	
	private T data;
	
	public PopPickupResponse() {
		
	}

	public PopPickupResponse(Integer code, String message, T data) {
		super(code, message);
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public PopPickupResponse(Integer codeOk, String message) {
		super(codeOk,message);
	}

	public Integer getPackageNumbers() {
		return packageNumbers;
	}

	public void setPackageNumbers(Integer packageNumbers) {
		this.packageNumbers = packageNumbers;
	}



	public String getPopBusinessName() {
		return popBusinessName;
	}

	public void setPopBusinessName(String popBusinessName) {
		this.popBusinessName = popBusinessName;
	}

	public String getPopBusinessCode() {
		return popBusinessCode;
	}

	public void setPopBusinessCode(String popBusinessCode) {
		this.popBusinessCode = popBusinessCode;
	}

}
