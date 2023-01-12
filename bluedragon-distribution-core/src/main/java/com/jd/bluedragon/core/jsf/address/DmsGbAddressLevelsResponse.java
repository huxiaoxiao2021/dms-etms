package com.jd.bluedragon.core.jsf.address;

import java.io.Serializable;
/**
 * 分拣-四级地址
 * @author wuyoude
 *
 */
public class DmsGbAddressLevelsResponse implements Serializable{
	private static final long serialVersionUID = 1L;
    /**
     * jd-编码
     */
    private String jdCode;
    /**
     * 国标编码
     */
    private String gbCode;
    /**
     * 名称
     */
    private String name;
    /**
     * 简称
     */
    private String abbreviationName;
    
	public String getJdCode() {
		return jdCode;
	}
	public void setJdCode(String jdCode) {
		this.jdCode = jdCode;
	}
	public String getGbCode() {
		return gbCode;
	}
	public void setGbCode(String gbCode) {
		this.gbCode = gbCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAbbreviationName() {
		return abbreviationName;
	}
	public void setAbbreviationName(String abbreviationName) {
		this.abbreviationName = abbreviationName;
	}
    
}
