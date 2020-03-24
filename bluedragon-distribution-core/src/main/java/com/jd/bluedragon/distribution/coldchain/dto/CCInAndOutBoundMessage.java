package com.jd.bluedragon.distribution.coldchain.dto;

/**
 * 冷链入库出库消息体
 * 冷链消费，分拣生产
 */
public class CCInAndOutBoundMessage {

    /**
     * 运单号
     */
    private String waybillNo;

    /**
     * 包裹号
     */
    private String packageNo;

    /**
     * 机构ID（区域ID）
     */
    private String orgId;

    /**
     * 机构名称（区域名称）
     */
    private String orgName;

    /**
     * 站点id
     */
    private String siteId;

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 操作人erp
     */
    private String operateERP;

    /**
     * 操作时间, 格式yyyy-MM-dd HH:mm:ss
     */
    private String operateTime;

    /**
     * 操作类型（验货-1、冷链入库-2、冷链出库-3、发货-4）
     */
    private Integer operateType;

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public String getPackageNo() {
        return packageNo;
    }

    public void setPackageNo(String packageNo) {
        this.packageNo = packageNo;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
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
