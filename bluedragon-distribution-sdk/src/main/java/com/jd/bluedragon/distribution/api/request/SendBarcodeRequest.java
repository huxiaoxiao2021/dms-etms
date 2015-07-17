package com.jd.bluedragon.distribution.api.request;

import com.google.common.base.Objects;
import com.jd.bluedragon.distribution.api.JdRequest;

public class SendBarcodeRequest extends JdRequest {

	private static final long serialVersionUID = 8900218370299464985L;

	/** 批次号编号 */
	private String sendBarcode;

	/** 创建站点编号 */
	private Integer createSiteCode;

	/** 创建站点名称 */
	private String createSiteName;

	/** 接收站点编号 */
	private Integer receiveSiteCode;

	/** 接收站点名称 */
	private String receiveSiteName;


	public String getSendBarcode() {
		return sendBarcode;
	}

	public void setSendBarcode(String sendBarcode) {
		this.sendBarcode = sendBarcode;
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

	public String getReceiveSiteName() {
		return this.receiveSiteName;
	}

	public void setReceiveSiteName(String receiveSiteName) {
		this.receiveSiteName = receiveSiteName;
	}

	public String toString() {
		return Objects.toStringHelper(SendBarcodeRequest.class).addValue(this.sendBarcode)
				.addValue(this.createSiteCode).addValue(this.createSiteName).addValue(this.receiveSiteCode)
				.addValue(this.receiveSiteName).toString();
	}

}
