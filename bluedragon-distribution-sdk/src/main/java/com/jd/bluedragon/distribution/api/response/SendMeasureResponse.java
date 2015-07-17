package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * PDA
 * @author zhuchao
 *
 */
public class SendMeasureResponse extends JdResponse {

	private static final long serialVersionUID = -5898848410542839979L;

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
	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}
	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}	
	
}
