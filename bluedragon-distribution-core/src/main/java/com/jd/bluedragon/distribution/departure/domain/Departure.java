package com.jd.bluedragon.distribution.departure.domain;

import java.io.Serializable;
import java.util.List;

import com.jd.bluedragon.distribution.send.domain.SendM;

public class Departure implements Serializable {

	private static final long serialVersionUID = 3127339383409729098L;

	public static final int DEPARTRUE_TYPE_CHUANZHAN = 0; // 传站发车
	public static final int DEPARTRUE_TYPE_ZHIXIAN = 1; // 支线发车
	public static final int DEPARTRUE_TYPE_BRANCH = 3; // 支线正向发车
	public static final int DEPARTRUE_TYPE_TURNTABLE = 2; // 转车发车

	/** 批次号 */
	private List<SendM> SendMs;

	/** 车号 */
	private String carCode;

	/** 封签号 */
	private String shieldsCarCode;

	/** 司机编码 */
	private Integer sendUserCode;

	/** 司机 */
	private String sendUser;

	/** 重量 */
	private Double weight;

	/** 体积 */
	private Double volume;

	/** 承运人类型 */
	private Integer sendUserType;

	/** 发车类型 0 传站发车 1支线发车 */
	private int type;

	/** 转出 车号 */
	private String oldCarCode;

	/** 分拣业务类型 '10' 正向 '20' 逆向 '30' 三方 '40' POP */
	private Integer businessType;

	private Integer runNumber;// 班次
	private String receiveSiteCodes;
	
	/** 运力编码 */
	private String capacityCode;

    public int getRouteType() {
        return routeType;
    }

    public void setRouteType(int routeType) {
        this.routeType = routeType;
    }

    /** 干支线运输 */
    private int routeType;

	public String getOldCarCode() {
		return oldCarCode;
	}

	public void setOldCarCode(String oldCarCode) {
		this.oldCarCode = oldCarCode;
	}

	public Integer getBusinessType() {
		return businessType;
	}

	public void setBusinessType(Integer businessType) {
		this.businessType = businessType;
	}

	public List<SendM> getSendMs() {
		return SendMs;
	}

	public void setSendMs(List<SendM> sendMs) {
		SendMs = sendMs;
	}

	public String getCarCode() {
		return carCode;
	}

	public void setCarCode(String carCode) {
		this.carCode = carCode;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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

	public Integer getSendUserType() {
		return sendUserType;
	}

	public void setSendUserType(Integer sendUserType) {
		this.sendUserType = sendUserType;
	}

	public Integer getRunNumber() {
		return runNumber;
	}

	public void setRunNumber(Integer runNumber) {
		this.runNumber = runNumber;
	}

	public String getCapacityCode() {
		return capacityCode;
	}

	public void setCapacityCode(String capacityCode) {
		this.capacityCode = capacityCode;
	}

	public String getReceiveSiteCodes() {
		return receiveSiteCodes;
	}

	public void setReceiveSiteCodes(String receiveSiteCodes) {
		this.receiveSiteCodes = receiveSiteCodes;
	}

	@Override
	public String toString() {
		return "Departure [SendMs=" + SendMs + ", carCode=" + carCode
				+ ", shieldsCarCode=" + shieldsCarCode + ", sendUserCode="
				+ sendUserCode + ", sendUser=" + sendUser + ", weight="
				+ weight + ", volume=" + volume + ", type=" + type + "]";
	}
}
