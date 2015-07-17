package com.jd.bluedragon.distribution.departure.domain;

import java.io.Serializable;
import java.util.List;

public class CapacityCodeResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5976346839412299653L;

	/** 相应状态码 */
    private Integer code;

    /** 响应消息 */
    private String message;
    
    private List<CapacityDomain> data;

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

	public List<CapacityDomain> getData() {
		return data;
	}

	public void setData(List<CapacityDomain> data) {
		this.data = data;
	}
	
    
}
