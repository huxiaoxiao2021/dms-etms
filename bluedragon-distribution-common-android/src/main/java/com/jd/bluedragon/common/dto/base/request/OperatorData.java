package com.jd.bluedragon.common.dto.base.request;

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
    /**
     * 设备编码
     */
    private String machineCode;
    /**
     * 格口编码
     */
    private String chuteCode;
    /**
     * 供件台编号/供件台dws设备编码
     */
    private String supplyNo;
    /**
     * 网格key:关联场地网格业务主键
     */
    private String workGridKey;
	
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
	public String getMachineCode() {
		return machineCode;
	}
	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}
	public String getChuteCode() {
		return chuteCode;
	}
	public void setChuteCode(String chuteCode) {
		this.chuteCode = chuteCode;
	}
	public String getWorkGridKey() {
		return workGridKey;
	}
	public String getSupplyNo() {
		return supplyNo;
	}
	public void setSupplyNo(String supplyNo) {
		this.supplyNo = supplyNo;
	}	
	public void setWorkGridKey(String workGridKey) {
		this.workGridKey = workGridKey;
	}
	
}