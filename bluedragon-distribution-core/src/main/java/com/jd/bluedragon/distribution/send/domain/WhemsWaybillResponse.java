package com.jd.bluedragon.distribution.send.domain;

import java.util.List;


public class WhemsWaybillResponse {
    
    /** 相应状态码 */
    private Integer code;

    /** 响应消息 */
    private String message;
    
    private List<WhemsWaybillEntity> data;
    

	public WhemsWaybillResponse(Integer code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	
	public WhemsWaybillResponse(Integer code, String message,
			List<WhemsWaybillEntity> data) {
		super();
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public WhemsWaybillResponse() {
		// TODO Auto-generated constructor stub
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

	public List<WhemsWaybillEntity> getData() {
		return data;
	}

	public void setData(List<WhemsWaybillEntity> data) {
		this.data = data;
	}
    
}
