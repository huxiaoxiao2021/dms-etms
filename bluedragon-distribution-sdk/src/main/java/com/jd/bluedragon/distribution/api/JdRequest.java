package com.jd.bluedragon.distribution.api;

public class JdRequest extends JdObject {
    
    private static final long serialVersionUID = 2620618235126101016L;
    
    /**
     * 访问验证
     */
    private String key;
    
    /** 操作人编号 */
    private Integer userCode;
    
    /** 操作人姓名 */
    private String userName;

    /** 操作人所属站点编号 */
    private Integer siteCode;
    
    /** 操作人所属站点名称 */
    private String siteName;
    
    /** 分拣业务类型 '10' 正向 '20' 逆向 '30' 三方 '40' POP */
    private Integer businessType;
    
    /** PAD业务主键ID */
    private Integer id;
    
    /** PDA操作时间 */
    private String operateTime;
    
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
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getOperateTime() {
        return this.operateTime;
    }
    
    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }
    
    @Override
    public String toString() {
        return "JdRequest [userCode=" + this.userCode + ", userName=" + this.userName
                + ", siteCode=" + this.siteCode + ", siteName=" + this.siteName + ", businessType="
                + this.businessType + ", id=" + this.id + ", operateTime=" + this.operateTime + "]";
    }
    
}
