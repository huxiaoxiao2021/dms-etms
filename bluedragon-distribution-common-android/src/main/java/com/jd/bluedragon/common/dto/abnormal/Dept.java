package com.jd.bluedragon.common.dto.abnormal;

import java.io.Serializable;
/**
 * 部门-对应com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.Dept
 * @author wuyoude
 *
 */
public class Dept implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String code;
	private String name;
	
	public Dept() {
		super();
	}
	
	public Dept(String code, String name) {
		this();
		this.code = code;
		this.name = name;
	}

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
