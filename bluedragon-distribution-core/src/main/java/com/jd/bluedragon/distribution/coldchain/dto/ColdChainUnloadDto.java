package com.jd.bluedragon.distribution.coldchain.dto;

public class ColdChainUnloadDto {
    /**
     * 机构ID（区域ID）
     */
    private String orgId;

    /**
     * 机构名称（区域名称 ）
     */
    private String orgName;

    /**
     * 分拣中心ID
     */
    private String siteId;

    /**
     * 分拣中心名称
     */
    private String siteName;

    /**
     * 卸货时间，格式yyyy-MM-dd HH:mm:ss
     */
    private String unloadTime;

    /**
     * 操作人ERP
     */
    private String operateERP;

    /**
     * 车牌号
     */
    private String vehicleNo;

    /**
     * 车型ID
     */
    private String vehicleModelNo;

    /**
     * 车型名称
     */
    private String vehicleModelName;

    /**
     * 温度
     */
    private String temp;

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

    public String getUnloadTime() {
        return unloadTime;
    }

    public void setUnloadTime(String unloadTime) {
        this.unloadTime = unloadTime;
    }

    public String getOperateERP() {
        return operateERP;
    }

    public void setOperateERP(String operateERP) {
        this.operateERP = operateERP;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getVehicleModelNo() {
        return vehicleModelNo;
    }

    public void setVehicleModelNo(String vehicleModelNo) {
        this.vehicleModelNo = vehicleModelNo;
    }

    public String getVehicleModelName() {
        return vehicleModelName;
    }

    public void setVehicleModelName(String vehicleModelName) {
        this.vehicleModelName = vehicleModelName;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
