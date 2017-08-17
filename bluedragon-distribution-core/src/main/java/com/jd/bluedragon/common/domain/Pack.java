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
	 * 包裹序号
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
}
