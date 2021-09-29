package com.jd.bluedragon.common.dto.abnormal;

import java.io.Serializable;

public class SpecialScene implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Integer code;
	private String name;
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
