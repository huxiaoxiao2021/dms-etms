package com.jd.bluedragon.distribution.ver.domain;

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
}
