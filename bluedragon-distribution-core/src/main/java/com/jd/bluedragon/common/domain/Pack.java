package com.jd.bluedragon.common.domain;

import java.io.Serializable;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-7 上午10:29:46
 *
 * 包裹
 */
public class Pack implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 包裹ID
	 */
	private Long packId;
	
	/**
	 * 包裹序号(第几个包裹)
	 */
	private Integer packSerial;
	
	/**
	 * 包裹号
	 */
	private String packCode;
	
	/**
	 * 运单号
	 */
	private String waybillCode;
	
	/**
	 * 包裹重量
	 */
	private String weight;
	/**
	 * 记录运单称重流水中类型为2的重量信息
	 */
	private String pWeight;
	
	/** 是否打印包裹 */
	private int isPrintPack;

	/**
	 * 新包裹号
	 */
	private String packageCode;

	/**
	 * 包裹序号（1/1）
	 */
	public String packageIndex;

	/**
	 * 包裹重量（带单位）
	 */
	public String packageWeight;

	/**
	 * 包裹号后缀
	 */
	public String packageSuffix;

	public Integer getPackSerial() {
		return packSerial;
	}

	public void setPackSerial(Integer packSerial) {
		this.packSerial = packSerial;
	}

	public Long getPackId() {
		return packId;
	}

	public void setPackId(Long packId) {
		this.packId = packId;
	}

	public String getPackCode() {
		return packCode;
	}

	public void setPackCode(String packCode) {
		this.packCode = packCode;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public int getIsPrintPack() {
		return isPrintPack;
	}

	public void setIsPrintPack(int isPrintPack) {
		this.isPrintPack = isPrintPack;
	}

	/**
	 * @return the pWeight
	 */
	public String getpWeight() {
		return pWeight;
	}

	/**
	 * @param pWeight the pWeight to set
	 */
	public void setpWeight(String pWeight) {
		this.pWeight = pWeight;
	}

	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	public String getPackageIndex() {
		return packageIndex;
	}

	public void setPackageIndex(String packageIndex) {
		this.packageIndex = packageIndex;
	}

	public String getPackageWeight() {
		return packageWeight;
	}

	public void setPackageWeight(String packageWeight) {
		this.packageWeight = packageWeight;
	}

	public String getPackageSuffix() {
		return packageSuffix;
	}

	public void setPackageSuffix(String packageSuffix) {
		this.packageSuffix = packageSuffix;
	}
}
