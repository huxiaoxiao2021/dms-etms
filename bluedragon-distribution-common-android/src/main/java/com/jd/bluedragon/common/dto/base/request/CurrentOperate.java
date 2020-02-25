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
}
