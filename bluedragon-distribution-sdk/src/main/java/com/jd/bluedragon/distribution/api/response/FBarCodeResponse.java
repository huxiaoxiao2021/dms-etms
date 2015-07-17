package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

public class FBarCodeResponse extends JdResponse {

	private static final long serialVersionUID = 6421643159029953636L;

	/** 全局唯一ID */
	private Long id;

	/** 创建时间 */
	private String createTime;

	/** 箱号 */
	private String fBarCodeCode;

	/** 箱号 */
	private String fBarCodeCodes;

	/** 创建站点编号 */
	private Integer createSiteCode;

	/** 创建站点名称 */
	private String createSiteName;

	public FBarCodeResponse() {
		super();
	}

	public FBarCodeResponse(Integer code, String message) {
		super(code, message);
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getfBarCodeCode() {
		return this.fBarCodeCode;
	}

	public void setfBarCodeCode(String fBarCodeCode) {
		this.fBarCodeCode = fBarCodeCode;
	}

	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
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

	public String getfBarCodeCodes() {
		return this.fBarCodeCodes;
	}

	public void setfBarCodeCodes(String fBarCodeCodes) {
		this.fBarCodeCodes = fBarCodeCodes;
	}

}
