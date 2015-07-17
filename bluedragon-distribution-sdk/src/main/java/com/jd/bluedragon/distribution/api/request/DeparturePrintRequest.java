package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

public class DeparturePrintRequest implements Serializable{
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -4405757806094538849L;
	/**
	 * 发车批次号
	 */
	
	private Long shieldsCarId;
	/**
	 * 车号
	 */
	
	private String carCode;
	/**
	 * 承运人
	 */
	
	private String sendUser;
	/**
	 * 三方运单号
	 */
	
	private String thirdWaybillCode;
	/**
	 * 开始时间
	 */
	
	private String startTime;
	/**
	 * 结束时间
	 */
	
	private String endTime;
	/**
	 * 承运类型
	 */
	
    private Integer departType;
    /**
	 * 始发地
	 */
    
	private String create_code;
	
	/**
	 * 目的地
	 */
	
	private String receive_code;
	
	/**
	 * 打印状态
	 */
	private Integer print_flage;
	
	public Long getShieldsCarId() {
		return shieldsCarId;
	}
	
	public void setShieldsCarId(Long shieldsCarId) {
		this.shieldsCarId = shieldsCarId;
	}
	
	public String getThirdWaybillCode() {
		return thirdWaybillCode;
	}
	
	public void setThirdWaybillCode(String thirdWaybillCode) {
		this.thirdWaybillCode = thirdWaybillCode;
	}
	
	public String getStartTime() {
		return startTime;
	}
	
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	public String getEndTime() {
		return endTime;
	}
	
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	public String getSendUser() {
		return sendUser;
	}
	
	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}
	
	public String getCarCode() {
		return carCode;
	}
	
	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}
	
	public Integer getDepartType() {
		return departType;
	}
	
	public void setDepartType(Integer departType) {
		this.departType = departType;
	}
	
	public String getCreate_code() {
		return create_code;
	}
	
	public void setCreate_code(String create_code) {
		this.create_code = create_code;
	}
	
	public String getReceive_code() {
		return receive_code;
	}
	
	public void setReceive_code(String receive_code) {
		this.receive_code = receive_code;
	}

	public Integer getPrint_flage() {
		return print_flage;
	}

	public void setPrint_flage(Integer print_flage) {
		this.print_flage = print_flage;
	}
	
}
