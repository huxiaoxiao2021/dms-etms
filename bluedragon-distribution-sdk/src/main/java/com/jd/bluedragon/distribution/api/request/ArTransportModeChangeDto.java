package com.jd.bluedragon.distribution.api.request;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName ArTransportModeChangeDto
 * @date 2019/7/11
 */
public class ArTransportModeChangeDto {

    /**
     * 转发方式 (航空转陆运 或 航空转高铁)
     */
    private Integer transformType;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 异常类型 (航空违禁品)
     */
    private Integer abnormalType;

    /**
     * 一级原因 (航空转陆运 或 航空转高铁)
     */
    private Integer firstLevel;

    /**
     * 二级原因 (航空违禁品)
     */
    private Integer secondLevel;

    /**
     * 三级原因
     */
    private Integer thirdLevel;

    /**
     * 提交人ERP
     */
    private String operatorErp;

    /**
     * 提交部门编码
     */
    private Integer siteCode;

    /**
     * 提交部门名称
     */
    private String siteName;

    /**
     * 提交的区域ID
     */
    private Integer areaId;

    /**
     * 提交的区域名称
     */
    private String areaName;

    /**
     * 操作时间
     */
    private String operateTime;

    /**
     * 青龙业主号
     */
    private String customerCode;

    /**
     * 青龙运单寄件人
     */
    private String consignerPin;

    /**
     * 寄件人姓名
     */
    private String consigner;

    /**
     * 寄件人ID
     */
    private Integer consignerId;

    /**
     * 寄件人手机号
     */
    private String consignerMobile;

    /**
     * 揽收站点ID
     */
    private Integer pickupSiteId;

    /**
     * 揽收站点名称
     */
    private String pickupSiteName;

    /**
     * B商家ID
     */
    private Integer busiId;

    /**
     * B商家名称
     */
    private String busiName;

    /**
     * 托寄物名称
     */
    private String consignmentName;

    public Integer getTransformType() {
        return transformType;
    }

    public void setTransformType(Integer transformType) {
        this.transformType = transformType;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getAbnormalType() {
        return abnormalType;
    }

    public void setAbnormalType(Integer abnormalType) {
        this.abnormalType = abnormalType;
    }

    public Integer getFirstLevel() {
        return firstLevel;
    }

    public void setFirstLevel(Integer firstLevel) {
        this.firstLevel = firstLevel;
    }

    public Integer getSecondLevel() {
        return secondLevel;
    }

    public void setSecondLevel(Integer secondLevel) {
        this.secondLevel = secondLevel;
    }

    public Integer getThirdLevel() {
        return thirdLevel;
    }

    public void setThirdLevel(Integer thirdLevel) {
        this.thirdLevel = thirdLevel;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
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

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getConsignerPin() {
        return consignerPin;
    }

    public void setConsignerPin(String consignerPin) {
        this.consignerPin = consignerPin;
    }

    public String getConsigner() {
        return consigner;
    }

    public void setConsigner(String consigner) {
        this.consigner = consigner;
    }

    public Integer getConsignerId() {
        return consignerId;
    }

    public void setConsignerId(Integer consignerId) {
        this.consignerId = consignerId;
    }

    public String getConsignerMobile() {
        return consignerMobile;
    }

    public void setConsignerMobile(String consignerMobile) {
        this.consignerMobile = consignerMobile;
    }

    public Integer getPickupSiteId() {
        return pickupSiteId;
    }

    public void setPickupSiteId(Integer pickupSiteId) {
        this.pickupSiteId = pickupSiteId;
    }

    public String getPickupSiteName() {
        return pickupSiteName;
    }

    public void setPickupSiteName(String pickupSiteName) {
        this.pickupSiteName = pickupSiteName;
    }

    public Integer getBusiId() {
        return busiId;
    }

    public void setBusiId(Integer busiId) {
        this.busiId = busiId;
    }

    public String getBusiName() {
        return busiName;
    }

    public void setBusiName(String busiName) {
        this.busiName = busiName;
    }

    public String getConsignmentName() {
        return consignmentName;
    }

    public void setConsignmentName(String consignmentName) {
        this.consignmentName = consignmentName;
    }
}
