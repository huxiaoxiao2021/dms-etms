package com.jd.bluedragon.distribution.business.entity;
/**
 * 商家地址维护状态
 * @author wuyoude
 *
 */
public enum BusinessReturnAdressStatusEnum {
	
	NO(0,"未维护"),
	YES(1,"已维护");
	
	private BusinessReturnAdressStatusEnum(Integer statusCode, String statusName) {
		this.statusCode = statusCode;
		this.statusName = statusName;
	}
	private Integer statusCode;
	private String statusName;
	/**
	 * @return the statusCode
	 */
	public Integer getStatusCode() {
		return statusCode;
	}
	/**
	 * @return the statusName
	 */
	public String getStatusName() {
		return statusName;
	}
}
