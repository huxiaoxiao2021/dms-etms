package com.jd.bluedragon.distribution.api.request;

import java.util.List;

import com.jd.bluedragon.distribution.api.JdRequest;

public class DepartureRequest extends JdRequest {

	private static final long serialVersionUID = 3127339383409729098L;

	private List<DepartureSendRequest> sends;

	/** 波次号 */
	private String batchKey; //波次号

	/** 封签号 */
	private String shieldsCarCode; // 可以为空

	private Long sendCarSealsID;

	/** 重量 */
	private Double weight; // 可以为空

	/** 体积 */
	private Double volume; // 可以为空

	/** 承运人类型 */
	private Integer sendUserType;//

	/** 车号 */
	private String carCode; // 可以为空

	/** 发货司机 */
	private String sendUser;
	/** 发货司机编码 */
	private String sendUserCode;
	/** 发车类型 0 传站发车 1支线发车 */
	private Integer type;
	/** 转出 车号 */
	
	private String oldCarCode; // 可以为空
    private Integer runNumber;//班次
	private String runNumberName;//班次名称

	/** 运力编码 */
	private String capacityCode;

    /** 干支线运输 */
    private int routeType;

	/** 运单号(第三方) */
	private String thirdWaybillCode;

	public String getBatchKey() {
		return batchKey;
	}

	public void setBatchKey(String batchKey) {
		this.batchKey = batchKey;
	}

	public int getRouteType() {
        return routeType;
    }

    public void setRouteType(int routeType) {
        this.routeType = routeType;
    }


    public String getOldCarCode() {
		return oldCarCode;
	}

	public void setOldCarCode(String oldCarCode) {
		this.oldCarCode = oldCarCode;
	}

	public String getShieldsCarCode() {
		return shieldsCarCode;
	}

	public void setShieldsCarCode(String shieldsCarCode) {
		this.shieldsCarCode = shieldsCarCode;
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

	public List<DepartureSendRequest> getSends() {
		return sends;
	}

	public void setSends(List<DepartureSendRequest> sends) {
		this.sends = sends;
	}

	public Long getSendCarSealsID() {
		return sendCarSealsID;
	}

	public void setSendCarSealsID(Long sendCarSealsID) {
		this.sendCarSealsID = sendCarSealsID;
	}

	public Integer getSendUserType() {
		return sendUserType;
	}

	public void setSendUserType(Integer sendUserType) {
		this.sendUserType = sendUserType;
	}

	public String getCarCode() {
		return carCode;
	}

	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}

	public String getSendUser() {
		return sendUser;
	}

	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}

	public String getSendUserCode() {
		return sendUserCode;
	}

	public void setSendUserCode(String sendUserCode) {
		this.sendUserCode = sendUserCode;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getRunNumber() {
		return runNumber;
	}

	public void setRunNumber(Integer runNumber) {
		this.runNumber = runNumber;
	}

	public String getRunNumberName() {
		return runNumberName;
	}

	public void setRunNumberName(String runNumberName) {
		this.runNumberName = runNumberName;
	}

	public String getCapacityCode() {
		return capacityCode;
	}

	public void setCapacityCode(String capacityCode) {
		this.capacityCode = capacityCode;
	}

	public String getThirdWaybillCode() {
		return thirdWaybillCode;
	}

	public void setThirdWaybillCode(String thirdWaybillCode) {
		this.thirdWaybillCode = thirdWaybillCode;
	}

	@Override
	public String toString() {
		String result = "DepartureRequest [sends=" + sends
				+ ", shieldsCarCode=" + shieldsCarCode + ", sendCarSealsID="
				+ sendCarSealsID + ", weight=" + weight + ", volume=" + volume
				+ ",carCode=" + carCode + ",sendUser=" + sendUser
				+ ",sendUserCode=" + sendUserCode + ",type=" + type + "]";
		return result;
	}
}
