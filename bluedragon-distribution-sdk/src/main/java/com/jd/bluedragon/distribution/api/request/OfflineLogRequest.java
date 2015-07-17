package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

public class OfflineLogRequest extends JdRequest {
	private static final long serialVersionUID = 1214718370214764985L;
	private Integer taskType;
	private String packageCode;
	private String waybillCode;
	private String boxCode;
	private Integer receiveSiteCode;
	private String sealBoxCode;
	private String shieldsCarCode;
	private String carCode;
	private Integer sendUserCode;
	private String sendUser;
	private String batchCode;
	private String turnoverBoxCode;
	private Double weight;
	private Double volume;
	private String exceptionType;
	private Integer operateType;

	public Integer getOperateType() {
		return operateType;
	}

	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}

	public Integer getTaskType() {
		return taskType;
	}

	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	public String getSealBoxCode() {
		return sealBoxCode;
	}

	public void setSealBoxCode(String sealBoxCode) {
		this.sealBoxCode = sealBoxCode;
	}

	public String getShieldsCarCode() {
		return shieldsCarCode;
	}

	public void setShieldsCarCode(String shieldsCarCode) {
		this.shieldsCarCode = shieldsCarCode;
	}

	public String getCarCode() {
		return carCode;
	}

	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}

	public Integer getSendUserCode() {
		return sendUserCode;
	}

	public void setSendUserCode(Integer sendUserCode) {
		this.sendUserCode = sendUserCode;
	}

	public String getSendUser() {
		return sendUser;
	}

	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public String getTurnoverBoxCode() {
		return turnoverBoxCode;
	}

	public void setTurnoverBoxCode(String turnoverBoxCode) {
		this.turnoverBoxCode = turnoverBoxCode;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public String getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}
	@Override
	public String toString(){
		StringBuilder  str= new StringBuilder("{");
		str.append("batchCode"+this.batchCode).append(",");
		str.append("boxCode"+this.boxCode).append(",");
		str.append("carCode"+this.carCode).append(",");
		str.append("exceptionType"+this.exceptionType).append(",");
		str.append("packageCode"+this.packageCode).append(",");
		str.append("sealBoxCode"+this.sealBoxCode).append(",");
		str.append("sendUser"+this.sendUser).append(",");
		str.append("shieldsCarCode"+this.shieldsCarCode).append(",");
		str.append("turnoverBoxCode"+this.turnoverBoxCode).append(",");
		str.append("waybillCode"+this.waybillCode).append("}");
		return str.toString();
	}

}
