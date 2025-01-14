package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;
import com.jd.bluedragon.distribution.api.domain.OperatorData;

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
	/**
	 * 航空单号
	 */
	private String airNo;
	/**
	 * 运力资源名
	 */
	private String transName;
	/**
	 * 铁路序号
	 */
	private String railwayNo;

	/** 航标发货标示*/
	private Integer transporttype;
	/**
	 * 数量
	 */
	private Integer num;
	/**
	 * 备注信息
	 */
	private String demo;

	/**
	 * 离线任务数据来源
	 */
	private Integer bizSource;

	/**
	 * 用于验证离线数据的加密字段
	 */
	private String encrypt;

	/**
	 * 操作信息对象
	 */
	private OperatorData operatorData;

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

	public Integer getTransporttype() {
		return transporttype;
	}

	public void setTransporttype(Integer transporttype) {
		this.transporttype = transporttype;
	}

	@Override
	public String toString(){
		StringBuilder  str= new StringBuilder("{");
		str.append("batchCode:"+this.batchCode).append(",");
		str.append("boxCode:"+this.boxCode).append(",");
		str.append("carCode:"+this.carCode).append(",");
		str.append("exceptionType:"+this.exceptionType).append(",");
		str.append("packageCode:"+this.packageCode).append(",");
		str.append("sealBoxCode:"+this.sealBoxCode).append(",");
		str.append("sendUser:"+this.sendUser).append(",");
		str.append("shieldsCarCode:"+this.shieldsCarCode).append(",");
		str.append("turnoverBoxCode:"+this.turnoverBoxCode).append(",");
		str.append("transporttype:"+this.transporttype).append(",");
		str.append("waybillCode:"+this.waybillCode).append("}");
		return str.toString();
	}

	/**
	 * @return the airNo
	 */
	public String getAirNo() {
		return airNo;
	}

	/**
	 * @param airNo the airNo to set
	 */
	public void setAirNo(String airNo) {
		this.airNo = airNo;
	}

	/**
	 * @return the transName
	 */
	public String getTransName() {
		return transName;
	}

	/**
	 * @param transName the transName to set
	 */
	public void setTransName(String transName) {
		this.transName = transName;
	}

	/**
	 * @return the railwayNo
	 */
	public String getRailwayNo() {
		return railwayNo;
	}

	/**
	 * @param railwayNo the railwayNo to set
	 */
	public void setRailwayNo(String railwayNo) {
		this.railwayNo = railwayNo;
	}

	/**
	 * @return the num
	 */
	public Integer getNum() {
		return num;
	}

	/**
	 * @param num the num to set
	 */
	public void setNum(Integer num) {
		this.num = num;
	}

	/**
	 * @return the demo
	 */
	public String getDemo() {
		return demo;
	}

	/**
	 * @param demo the demo to set
	 */
	public void setDemo(String demo) {
		this.demo = demo;
	}

	public Integer getBizSource() {
		return bizSource;
	}

	public void setBizSource(Integer bizSource) {
		this.bizSource = bizSource;
	}

	public String getEncrypt() {
		return encrypt;
	}

	public void setEncrypt(String encrypt) {
		this.encrypt = encrypt;
	}

	public OperatorData getOperatorData() {
		return operatorData;
	}

	public void setOperatorData(OperatorData operatorData) {
		this.operatorData = operatorData;
	}
}
