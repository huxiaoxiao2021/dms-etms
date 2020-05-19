package com.jd.bluedragon.distribution.log;

import java.io.Serializable;

public class BusinessLogDto implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 业务主键
	 */
	private String businessKey;
	/**
	 * 操作类型（OperateTypeEnum对应的code值）
	 */
	private Integer operateType;
	/**
	 * 操作人
	 */
	private String operateUser;
	/**
	 * 操作时间(yyyy-mm-dd hh:mm:ss sss)
	 */
	private String operateTime;
	/**
	 * 操作内容
	 */
	private String operateContent;
	/**
	 * 耗时（ms）
	 */
	private Long costTime;
	
	public String getBusinessKey() {
		return businessKey;
	}
	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}
	public Integer getOperateType() {
		return operateType;
	}
	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}
	public String getOperateUser() {
		return operateUser;
	}
	public void setOperateUser(String operateUser) {
		this.operateUser = operateUser;
	}
	public String getOperateTime() {
		return operateTime;
	}
	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}
	public String getOperateContent() {
		return operateContent;
	}
	public void setOperateContent(String operateContent) {
		this.operateContent = operateContent;
	}
	public Long getCostTime() {
		return costTime;
	}
	public void setCostTime(Long costTime) {
		this.costTime = costTime;
	}
}
