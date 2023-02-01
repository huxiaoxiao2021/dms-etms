package com.jd.bluedragon.distribution.waybill.domain;

import java.io.Serializable;

public class OperatorData implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/**
     *@see com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum
     * 操作者类型编码
     */
	private Integer operatorTypeCode;
    /**
     * 操作者id
     */
	private String operatorId;
	
	public Integer getOperatorTypeCode() {
		return operatorTypeCode;
	}
	public void setOperatorTypeCode(Integer operatorTypeCode) {
		this.operatorTypeCode = operatorTypeCode;
	}
	public String getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}
	
}
