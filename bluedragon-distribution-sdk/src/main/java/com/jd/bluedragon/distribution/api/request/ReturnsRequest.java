package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 *  PDA 分拣退货
 */
public class ReturnsRequest  extends JdRequest{

	private static final long serialVersionUID = -397121711735666348L;

	/** 错误类型 */
	private String shieldsError;
	
	/**包裹号*/
	private String packageCode;

	public String getShieldsError() {
		return shieldsError;
	}

	public void setShieldsError(String shieldsError) {
		this.shieldsError = shieldsError;
	}

	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}


}
