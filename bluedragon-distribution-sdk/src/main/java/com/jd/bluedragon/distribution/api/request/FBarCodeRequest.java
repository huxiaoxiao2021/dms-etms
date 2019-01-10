package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

public class FBarCodeRequest extends JdRequest {

	private static final long serialVersionUID = 8900218370299464985L;

	/** 创建站点编号 */
	private Integer createSiteCode;

	/** 创建站点名称 */
	private String createSiteName;

	/** 数量 */
	private Integer quantity;

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

	public Integer getQuantity() {
		return this.quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "FBarCodeRequest{" +
				"createSiteCode=" + createSiteCode +
				", createSiteName='" + createSiteName + '\'' +
				", quantity=" + quantity +
				'}';
	}
}
