package com.jd.bluedragon.common.dto.abnormal.request;

import java.io.Serializable;

public class DeptQueryRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 区域编码
	 */
	private Integer regionId;
	/**
	 * 部门类型编码
	 */
	private String deptTypeCode;
	/**
	 * 部门名称
	 */
	private String deptName;
	
	public Integer getRegionId() {
		return regionId;
	}
	public void setRegionId(Integer regionId) {
		this.regionId = regionId;
	}
	public String getDeptTypeCode() {
		return deptTypeCode;
	}
	public void setDeptTypeCode(String deptTypeCode) {
		this.deptTypeCode = deptTypeCode;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	
}
