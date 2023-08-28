package com.jd.bluedragon.common.dto.base.request;

import java.io.Serializable;
import java.util.Date;

public class CurrentOperate implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *操作单位编号
     */
    private int siteCode;

    /**
     *操作单位名称
     */
    private String siteName;

    /**
     *操作时间
     */
    private Date operateTime;

    /**
     * 当前所在区域编号
     */
    private Integer orgId;

    /**
     * 当前所在区域名称
     */
    private String orgName;

    /**
     *分拣中心编码
     */
    private String dmsCode;
    /**
     *@see com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum
     * 操作者类型编码
     */
	private Integer operatorTypeCode;
    /**
     * 操作者id
     */
	private String operatorId;

    /**
     * 省区编码
     */
    private String provinceAgencyCode;

    /**
     * 省区名称
     */
    private String provinceAgencyName;

    /**
     * 枢纽编码
     */
    private String areaHubCode;
    /**
     * 枢纽名称
     */
    private String areaHubName;

    public CurrentOperate() {
    }

    public CurrentOperate(int siteCode, String siteName, Date operateTime) {
        this.siteCode = siteCode;
        this.siteName = siteName;
        this.operateTime = operateTime;
    }

    public int getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(int siteCode) {
        this.siteCode = siteCode;
    }

    public String getDmsCode() {
        return dmsCode;
    }

    public void setDmsCode(String dmsCode) {
        this.dmsCode = dmsCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
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

	public Integer getOperatorTypeCode() {
		return operatorTypeCode;
	}

	public void setOperatorTypeCode(Integer operatorTypeCode) {
		this.operatorTypeCode = operatorTypeCode;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

    public String getProvinceAgencyCode() {
        return provinceAgencyCode;
    }

    public void setProvinceAgencyCode(String provinceAgencyCode) {
        this.provinceAgencyCode = provinceAgencyCode;
    }

    public String getProvinceAgencyName() {
        return provinceAgencyName;
    }

    public void setProvinceAgencyName(String provinceAgencyName) {
        this.provinceAgencyName = provinceAgencyName;
    }

    public String getAreaHubCode() {
        return areaHubCode;
    }

    public void setAreaHubCode(String areaHubCode) {
        this.areaHubCode = areaHubCode;
    }

    public String getAreaHubName() {
        return areaHubName;
    }

    public void setAreaHubName(String areaHubName) {
        this.areaHubName = areaHubName;
    }
}
