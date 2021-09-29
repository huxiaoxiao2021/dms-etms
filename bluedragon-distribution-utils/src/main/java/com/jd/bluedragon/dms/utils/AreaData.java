package com.jd.bluedragon.dms.utils;

import java.io.Serializable;
/**
 * 七大区域对象
 * Created by wuyoude on 2021/09/25
 */
public class AreaData implements Serializable{

	private static final long serialVersionUID = 1L;
	
    private Integer code;
    private String name;
    
    AreaData(){
    	
    }
    AreaData(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
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
