package com.jd.bluedragon.distribution.weightVolume.domain;

import java.io.Serializable;

public class OverLengthAndWeightType implements Serializable {
	
	private static final long serialVersionUID = 7974615223846579568L;
	
	private String code;
	private String name;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
