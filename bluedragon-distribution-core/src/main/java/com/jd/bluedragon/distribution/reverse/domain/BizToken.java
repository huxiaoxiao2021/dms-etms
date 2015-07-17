package com.jd.bluedragon.distribution.reverse.domain;

public class BizToken implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9214296842941960951L;

	private String bizType;
	
	private String callCode;
	
	private String uuid;

	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}

	public String getCallCode() {
		return callCode;
	}

	public void setCallCode(String callCode) {
		this.callCode = callCode;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
