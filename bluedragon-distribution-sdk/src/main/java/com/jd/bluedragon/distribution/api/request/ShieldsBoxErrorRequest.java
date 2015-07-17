package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

public class ShieldsBoxErrorRequest extends JdRequest {
	private static final long serialVersionUID = 8900218370299464985L;
	/** 封签号 */
	private String shieldsCode;
	/** 箱号 */
	private String boxCode;
	/**异常类型*/
	private String shieldsError;
	public String getShieldsCode() {
		return shieldsCode;
	}
	public void setShieldsCode(String shieldsCode) {
		this.shieldsCode = shieldsCode;
	}
	public String getBoxCode() {
		return boxCode;
	}
	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}
	public String getShieldsError() {
		return shieldsError;
	}
	public void setShieldsError(String shieldsError) {
		this.shieldsError = shieldsError;
	}
}
