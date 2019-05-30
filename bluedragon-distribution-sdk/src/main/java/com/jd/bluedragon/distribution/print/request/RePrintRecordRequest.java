package com.jd.bluedragon.distribution.print.request;

import java.io.Serializable;

/**
 * 
 * @ClassName: RePrintRecordRequest
 * @Description: 包裹补打记录-请求
 * @author: wuyoude
 * @date: 2019年4月22日 下午4:25:24
 *
 */
public class RePrintRecordRequest implements Serializable{
	private static final long serialVersionUID = 1L;
    /**
     * 访问验证
     */
    private String key;
    
    /** 操作人编号_ERP帐号 */
    private Integer userCode;
    
    /** 操作人姓名 */
    private String userName;
    
    /** 操作人所属站点编号 */
    private Integer siteCode;
    
    /** 操作人所属站点编号 */
    private String siteName;
    
    private Integer businessType;
    
	private Long id;
	
	/**
	 * 运单号
	 */
	private String waybillCode;
	/**
	 * 包裹号
	 */
	private String packageCode;
	
	/**
	 * 打印结果-模板分组(区分B网和C网),TemplateGroupEnum对应的code
	 */
	private String templateGroupCode;

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
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

	/**
	 * @return the businessType
	 */
	public Integer getBusinessType() {
		return businessType;
	}

	/**
	 * @param businessType the businessType to set
	 */
	public void setBusinessType(Integer businessType) {
		this.businessType = businessType;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
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
	
}
