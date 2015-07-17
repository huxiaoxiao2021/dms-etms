package com.jd.bluedragon.distribution.departure.domain;

import java.io.Serializable;

public class SendMeasure implements Serializable {
	
	private static final long serialVersionUID = -7363081376874152328L;
	
	/** 重量  */
	private Double weight;
	
	/** 体积  */
	private Double volume;
	
    /** 操作单位编码 */
    private Integer receiveSiteCode;
	
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
	@Override
	public String toString() {
		return "WeightAndVolume [weight=" + weight + ", volume=" + volume + "]";
	}
	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}
	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}
	
}
