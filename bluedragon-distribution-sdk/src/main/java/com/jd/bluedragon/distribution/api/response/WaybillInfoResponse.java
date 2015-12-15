package com.jd.bluedragon.distribution.api.response;

import java.io.Serializable;

public class WaybillInfoResponse implements Serializable{
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = -4755707246939641954L;

	/** 相应状态码 */
    private Integer code;
    
    /** 响应消息 */
    private String message;
    
    private String jsonData;
    
	public WaybillInfoResponse(Integer code, String message, String jsonData) {
		super();
		this.code = code;
		this.message = message;
		this.jsonData = jsonData;
	}


	public WaybillInfoResponse(Integer code, String message) {
		super();
		this.code = code;
		this.message = message;
	}


	public WaybillInfoResponse() {
		super();
	}


	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
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
	
}
