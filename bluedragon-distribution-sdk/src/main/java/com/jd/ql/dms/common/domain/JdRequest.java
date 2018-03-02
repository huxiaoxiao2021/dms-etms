package com.jd.ql.dms.common.domain;

public class JdRequest extends JdObject {
    
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
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Long id;
    
    public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getUserCode() {
        return this.userCode;
    }
    
    public void setUserCode(Integer userCode) {
        this.userCode = userCode;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public Integer getSiteCode() {
        return this.siteCode;
    }
    
    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }
    
    public String getSiteName() {
        return this.siteName;
    }
    
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
    
    public Integer getBusinessType() {
        return this.businessType;
    }
    
    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }
    
    @Override
    public String toString() {
        return "JdRequest [userCode=" + this.userCode + ", userName=" + this.userName
                + ", siteCode=" + this.siteCode + ", siteName=" + this.siteName + ", businessType="
                + this.businessType + ", id=" + this.id + "]";
    }
    
}
