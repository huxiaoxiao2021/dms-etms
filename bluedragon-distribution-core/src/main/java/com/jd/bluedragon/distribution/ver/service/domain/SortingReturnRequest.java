package com.jd.bluedragon.distribution.ver.service.domain;

import java.io.Serializable;


public class SortingReturnRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    
	/** 错误类型 */
	private String shieldsError;
	
	/**包裹号*/
	private String packageCode;
	
    /** 操作人编号_ERP帐号 */
    private Integer userCode;
    
    /** 操作人姓名 */
    private String userName;
    
    /** 操作人所属站点编号 */
    private Integer siteCode;
    
    /** 操作人所属站点编号 */
    private String siteName;
    
    /** 分拣业务类型 '10' 正向 '20' 逆向 '30' 三方 '40' POP */
    private Integer businessType;
    
    /** PAD业务主键ID */
    private Integer id;
    
    /** PDA操作时间 */
    private String operateTime;

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

	public Integer getUserCode() {
		return userCode;
	}

	public void setUserCode(Integer userCode) {
		this.userCode = userCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public Integer getBusinessType() {
		return businessType;
	}

	public void setBusinessType(Integer businessType) {
		this.businessType = businessType;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}
}
