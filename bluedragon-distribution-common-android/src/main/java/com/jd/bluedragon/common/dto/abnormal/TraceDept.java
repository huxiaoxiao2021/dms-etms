package com.jd.bluedragon.common.dto.abnormal;

import java.io.Serializable;
import java.util.Date;
/**
 * 部门-对应com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.TraceDept
 * @author wuyoude
 *
 */
public class TraceDept implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String code;
	private String name;
	private Date firstOperateTime;
	private Boolean isDealDept;
	private Boolean isPickDept;
    private Integer type;
	
	public TraceDept() {
		super();
	}
	
	public TraceDept(String code, String name) {
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

	public Date getFirstOperateTime() {
		return firstOperateTime;
	}

	public void setFirstOperateTime(Date firstOperateTime) {
		this.firstOperateTime = firstOperateTime;
	}

	public Boolean getIsDealDept() {
		return isDealDept;
	}

	public void setIsDealDept(Boolean isDealDept) {
		this.isDealDept = isDealDept;
	}

	public Boolean getIsPickDept() {
		return isPickDept;
	}

	public void setIsPickDept(Boolean isPickDept) {
		this.isPickDept = isPickDept;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
}
