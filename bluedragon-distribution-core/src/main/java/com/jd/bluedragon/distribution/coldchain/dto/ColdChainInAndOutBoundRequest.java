package com.jd.bluedragon.distribution.coldchain.dto;

/**
 * 冷链操作 出入库请求
 */
public class ColdChainInAndOutBoundRequest {

    /**
     * 条码
     */
    private String barCode;

    /**
     * 机构ID
     */
    private Integer orgId;

    /**
     * 机构名称
     */
    private String orgName;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 操作人erp
     */
    private String operateERP;

    /**
     * 操作时间
     */
    private String operateTime;

    /**
     * 操作类型(1 - 入库 ， 2 - 出库)
     */
    private Integer operateType;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
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

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getOperateERP() {
        return operateERP;
    }

    public void setOperateERP(String operateERP) {
        this.operateERP = operateERP;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }
}
