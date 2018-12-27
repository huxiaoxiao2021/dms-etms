package com.jd.bluedragon.distribution.api.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.jd.bluedragon.distribution.api.JdRequest;

public class SealVehicleRequest extends JdRequest {

	private static final long serialVersionUID = -4900034488418807075L;

	/** 封签(箱子|车辆)编号 */
	private String sealCode;

	/** 车辆编号 */
	private String vehicleCode;

	/** 司机编号 */
	private Integer driverCode;

	/** 司机 */
	private String driver;

	/** 创建站点编号 */
	private Integer createSiteCode;

	/** 创建站点名称 */
	private String createSiteName;

	/** 收货站点编号 */
	private Integer receiveSiteCode;

	/** 收货站点名称 */
	private Integer receiveSiteName;

	/** 发货批次号 */
	private String sendCode;

	/** 重量 */
	private Double weight;

	/** 体积 */
	private Double volume;

	/** 件数 */
	private Integer packageNum;

	// ----------------

	/** 封签号 , 可以传多个, 逗号隔开 */
	private String sealCodes;

	/** 批次号 , 可以传多个, 逗号隔开 */
	private String sendCodes;

	public String getSealCodes() {
		return sealCodes;
	}

	public void setSealCodes(String sealCodes) {
		this.sealCodes = sealCodes;
	}

	public String getSendCodes() {
		return sendCodes;
	}

	public List<String> getSealCodeList() {
		if (null == sealCodes) {
			return new ArrayList<String>();
		}
		List<String> sealCodeList = new ArrayList<String>();
		Iterable<String> it = Splitter.on(',').trimResults().omitEmptyStrings().split(sealCodes);
		Set<String> set = Sets.newTreeSet(it);
		for (String s : set) {
			sealCodeList.add(s);
		}
		return sealCodeList;
	}

	public List<String> getSendCodeList() {
		if (null == sendCodes) {
			return new ArrayList<String>();
		}
		List<String> sendCodeList = new ArrayList<String>();
		Iterable<String> it = Splitter.on(',').trimResults().omitEmptyStrings().split(sendCodes);
		Set<String> set = Sets.newTreeSet(it);
		for (String s : set) {
			sendCodeList.add(s);
		}
		return sendCodeList;
	}

	public void setSendCodes(String sendCodes) {
		this.sendCodes = sendCodes;
	}

	public SealVehicleRequest() {
		super();
	}

	public String getSealCode() {
		return this.sealCode;
	}

	public void setSealCode(String sealCode) {
		this.sealCode = sealCode;
	}

	public String getVehicleCode() {
		return this.vehicleCode;
	}

	public void setVehicleCode(String vehicleCode) {
		this.vehicleCode = vehicleCode;
	}

	public Integer getDriverCode() {
		return this.driverCode;
	}

	public void setDriverCode(Integer driverCode) {
		this.driverCode = driverCode;
	}

	public String getDriver() {
		return this.driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public Integer getCreateSiteCode() {
		return this.createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public String getCreateSiteName() {
		return this.createSiteName;
	}

	public void setCreateSiteName(String createSiteName) {
		this.createSiteName = createSiteName;
	}

	public Integer getReceiveSiteCode() {
		return this.receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	public Integer getReceiveSiteName() {
		return this.receiveSiteName;
	}

	public void setReceiveSiteName(Integer receiveSiteName) {
		this.receiveSiteName = receiveSiteName;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
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

	public Integer getPackageNum() {
		return packageNum;
	}

	public void setPackageNum(Integer packageNum) {
		this.packageNum = packageNum;
	}

}
