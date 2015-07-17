package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

public class ShieldsCarErrorRequest extends JdRequest {
	private static final long serialVersionUID = 8900218370299464985L;
	/** 封签号 */
	private String shieldsCode;
	/** 车号 */
	private String carCode;
	/**异常类型*/
	private String shieldsError;
	public String getShieldsCode() {
		return shieldsCode;
	}
	public void setShieldsCode(String shieldsCode) {
		this.shieldsCode = shieldsCode;
	}
	public String getCarCode() {
		return carCode;
	}
	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}
	public String getShieldsError() {
		return shieldsError;
	}
	public void setShieldsError(String shieldsError) {
		this.shieldsError = shieldsError;
	}

}
