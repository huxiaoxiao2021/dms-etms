package com.jd.bluedragon.distribution.print.domain;

import java.util.Date;

/**
 * 
 * @ClassName: RePrintRecordMq
 * @Description: 包裹打印记录mq
 * @author: wuyoude
 * @date: 2019年4月25日 下午4:15:24
 *
 */
public class RePrintRecordMq {
	/**
	 * 业务操作类型
	 */
	private Integer operateType;
    /**
     * 运单号
     */
    private String waybillCode;
    /**
     * 包裹号-为空则为按运单打印
     */
    private String packageCode;
	/**
	 * 打印结果-模板分组(区分B网和C网),TemplateGroupEnum对应的code
	 */
	private String templateGroupCode;
	/**
	 * 模板名称
	 */
	private String templateName;
	/**
	 * 模板版本-默认为0，最后一个版本号
	 */
	private Integer templateVersion = 0;
    /**
     * 操作人
     */
    private Integer userCode;
    
    /**
     * 操作人name
     */
    private String userName;
    
    /**
     * 操作人erp
     */
    private String userErp;
    /**
     * 操作时间
     */
    private Date operateTime;
    /**
     * 站点编号
     */
    private Integer siteCode;

    /**
     * 站点名称
     */
    private String siteName;

	/**
	 * @return the operateType
	 */
	public Integer getOperateType() {
		return operateType;
	}

	/**
	 * @param operateType the operateType to set
	 */
	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}

	/**
	 * @return the waybillCode
	 */
	public String getWaybillCode() {
		return waybillCode;
	}

	/**
	 * @param waybillCode the waybillCode to set
	 */
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	/**
	 * @return the packageCode
	 */
	public String getPackageCode() {
		return packageCode;
	}

	/**
	 * @param packageCode the packageCode to set
	 */
	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	/**
	 * @return the templateGroupCode
	 */
	public String getTemplateGroupCode() {
		return templateGroupCode;
	}

	/**
	 * @param templateGroupCode the templateGroupCode to set
	 */
	public void setTemplateGroupCode(String templateGroupCode) {
		this.templateGroupCode = templateGroupCode;
	}

	/**
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @param templateName the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	/**
	 * @return the templateVersion
	 */
	public Integer getTemplateVersion() {
		return templateVersion;
	}

	/**
	 * @param templateVersion the templateVersion to set
	 */
	public void setTemplateVersion(Integer templateVersion) {
		this.templateVersion = templateVersion;
	}

	/**
	 * @return the userCode
	 */
	public Integer getUserCode() {
		return userCode;
	}

	/**
	 * @param userCode the userCode to set
	 */
	public void setUserCode(Integer userCode) {
		this.userCode = userCode;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the userErp
	 */
	public String getUserErp() {
		return userErp;
	}

	/**
	 * @param userErp the userErp to set
	 */
	public void setUserErp(String userErp) {
		this.userErp = userErp;
	}

	/**
	 * @return the operateTime
	 */
	public Date getOperateTime() {
		return operateTime;
	}

	/**
	 * @param operateTime the operateTime to set
	 */
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	/**
	 * @return the siteCode
	 */
	public Integer getSiteCode() {
		return siteCode;
	}

	/**
	 * @param siteCode the siteCode to set
	 */
	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}

	/**
	 * @return the siteName
	 */
	public String getSiteName() {
		return siteName;
	}

	/**
	 * @param siteName the siteName to set
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
}
