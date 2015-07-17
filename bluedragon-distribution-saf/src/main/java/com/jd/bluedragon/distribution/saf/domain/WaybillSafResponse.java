package com.jd.bluedragon.distribution.saf.domain;

import java.io.Serializable;

public class WaybillSafResponse<T> implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4682692973393579436L;
	
	public static final Integer CODE_OK = 200;
    public static final String MESSAGE_OK = "OK";
    
    public static final Integer CODE_OK_NULL = 2200;
    public static final String MESSAGE_OK_NULL = "调用服务成功，数据为空";
    
    public static final Integer CODE_PARAM_ERROR = 10000;
    public static final String MESSAGE_PARAM_ERROR = "参数错误";
    
    public static final Integer CODE_SERVICE_ERROR = 20000;
    public static final String MESSAGE_SERVICE_ERROR = "服务异常";

	 /** 相应状态码 */
    private Integer code;
    
    /** 响应消息 */
    private String message;
    
    private T data;
    
    public WaybillSafResponse() {
    }
    
    public WaybillSafResponse(Integer code, String message) {
        super();
        this.code = code;
        this.message = message;
    }
    
	public WaybillSafResponse(Integer code, String message, T data) {
		super();
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
    
}
