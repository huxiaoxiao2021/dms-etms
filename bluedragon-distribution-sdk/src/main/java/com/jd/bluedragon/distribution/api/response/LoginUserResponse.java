package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * 
 * @ClassName: LoginUserRespnse
 * @Description: 登录账户信息返回实体
 * @author: wuyoude
 * @date: 2019年4月10日 上午10:39:26
 *
 */
public class LoginUserResponse extends JdResponse {

	private static final long serialVersionUID = -7082200092556621496L;

	/**
	 * ERP帐号
	 */
	private String erpAccount;

	/**
	 * ERP登录密码
	 */
	private String password;

	/**
	 * 站点编号
	 */
	private Integer siteCode;

	/**
	 * 站点名称
	 */
	private String siteName;

	/**
	 * 用户ID
	 */
	private Integer staffId;

	/**
	 * 用户名称
	 */
	private String staffName;

	/**
	 * 机构号
	 */
	private Integer orgId;

	/**
	 * 机构名称
	 */
	private String orgName;

	private Integer siteType;

	/**
	 * 站点子类型
	 */
	private Integer subType;

	private String dmsCode;

	private String dmsName;

	/**
	 * 登录人所在的分拣中心编码|绑定的分拣中心编码
	 */
	private Integer dmsSiteCode;
	/**
	 * 登录人所在的分拣中心名称|绑定的分拣中心名称
	 */
	private String dmsSiteName;
	/**
	 * 运行环境
	 */
	private String runningMode;

	/**
	 * 分拣中心ID
	 */
	private Integer dmsId;
	/**
	 * 登录id
	 */
	private Long loginId;
	/**
	 * @return the dmsSiteCode
	 */
	public Integer getDmsSiteCode() {
		return dmsSiteCode;
	}
	/**
	 * @param dmsSiteCode the dmsSiteCode to set
	 */
	public void setDmsSiteCode(Integer dmsSiteCode) {
		this.dmsSiteCode = dmsSiteCode;
	}
	/**
	 * @return the dmsSiteName
	 */
	public String getDmsSiteName() {
		return dmsSiteName;
	}
	/**
	 * @param dmsSiteName the dmsSiteName to set
	 */
	public void setDmsSiteName(String dmsSiteName) {
		this.dmsSiteName = dmsSiteName;
	}
	/**
	 * @return the runningMode
	 */
	public String getRunningMode() {
		return runningMode;
	}
	/**
	 * @param runningMode the runningMode to set
	 */
	public void setRunningMode(String runningMode) {
		this.runningMode = runningMode;
	}

	public LoginUserResponse(){}

	public LoginUserResponse(Integer code, String message) {
		super(code, message);
	}

	public String getErpAccount() {
		return erpAccount;
	}

	public void setErpAccount(String erpAccount) {
		this.erpAccount = erpAccount;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public Integer getStaffId() {
		return staffId;
	}

	public void setStaffId(Integer staffId) {
		this.staffId = staffId;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public Integer getSiteType() {
		return siteType;
	}

	public void setSiteType(Integer siteType) {
		this.siteType = siteType;
	}

	public Integer getSubType() {
		return subType;
	}

	public void setSubType(Integer subType) {
		this.subType = subType;
	}

	public String getDmsCode() {
		return dmsCode;
	}

	public void setDmsCode(String dmsCode) {
		this.dmsCode = dmsCode;
	}

	public String getDmsName() {
		return dmsName;
	}

	public void setDmsName(String dmsName) {
		this.dmsName = dmsName;
	}

	public Integer getDmsId() {
		return dmsId;
	}

	public void setDmsId(Integer dmsId) {
		this.dmsId = dmsId;
	}

	/**
	 * @return the loginId
	 */
	public Long getLoginId() {
		return loginId;
	}
	/**
	 * @param loginId the loginId to set
	 */
	public void setLoginId(Long loginId) {
		this.loginId = loginId;
	}
	public BaseResponse toOldLoginResponse() {
		BaseResponse baseResponse = new BaseResponse(super.getCode(), super.getMessage());
		baseResponse.setErpAccount(this.erpAccount);
		baseResponse.setPassword(this.password);
		if (baseResponse.getCode().equals(JdResponse.CODE_OK)) {
			baseResponse.setSiteCode(this.siteCode);
			baseResponse.setSiteName(this.siteName);
			baseResponse.setStaffId(this.staffId);
			baseResponse.setStaffName(this.staffName);
			baseResponse.setOrgId(this.orgId);
			baseResponse.setOrgName(this.orgName);
			baseResponse.setSiteType(this.siteType);
			baseResponse.setSubType(this.subType);
			baseResponse.setDmsCode(this.dmsCode);
		}
		return baseResponse;
	}

	@Override
	public String toString() {
		return "LoginUserResponse{" +
				"erpAccount='" + erpAccount + '\'' +
				", password='" + password + '\'' +
				", siteCode=" + siteCode +
				", siteName='" + siteName + '\'' +
				", staffId=" + staffId +
				", staffName='" + staffName + '\'' +
				", orgId=" + orgId +
				", orgName='" + orgName + '\'' +
				", siteType=" + siteType +
				", subType=" + subType +
				", dmsCode='" + dmsCode + '\'' +
				", dmsName='" + dmsName + '\'' +
				", dmsSiteCode=" + dmsSiteCode +
				", dmsSiteName='" + dmsSiteName + '\'' +
				", runningMode='" + runningMode + '\'' +
				", dmsId=" + dmsId +
				'}';
	}
}
