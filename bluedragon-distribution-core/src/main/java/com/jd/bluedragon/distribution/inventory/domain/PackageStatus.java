package com.jd.bluedragon.distribution.inventory.domain;

import java.util.Date;

public class PackageStatus {
    private static final long serialVersionUID = 1L;

    /**
     * 包裹号
     */
    private String packageCode;
    /**
     * 运单号
     */
    private String waybillCode;
    /**
     * 箱号
     */
    private String boxCode;
    /**
     * 批次号
     */
    private String sendCode;
    /**
     * 始发分拣中心id
     */
    private Integer createSiteCode;
    /**
     * 始发分拣中心名称
     */
    private String createSiteName;
    /**
     * 始发分拣中心类型
     */
    private Integer createSiteType;
    /**
     * 始发分拣中心子类型
     */
    private Integer createSiteSubType;
    /**
     * 目的分拣中心id
     */
    private Integer receiveSiteCode;
    /**
     * 目的分拣中心名称
     */
    private String receiveSiteName;
    /**
     * 目的分拣中心类型
     */
    private Integer receiveSiteType;
    /**
     * 目的分拣中心子类型
     */
    private Integer receiveSiteSubType;
    /**
     * 操作节点
     */
    private Integer operateTypeNode;
    /**
     * 状态节点
     */
    private Integer statusCode;
    /**
     * 状态描述
     */
    private String statusDesc;
    /**
     * 操作人编码
     */
    private Integer operatorCode;
    /**
     * 操作人姓名
     */
    private String operatorName;
    /**
     * 操作时间
     */
    private Date operateTime;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Integer getCreateSiteType() {
        return createSiteType;
    }

    public void setCreateSiteType(Integer createSiteType) {
        this.createSiteType = createSiteType;
    }

    public Integer getCreateSiteSubType() {
        return createSiteSubType;
    }

    public void setCreateSiteSubType(Integer createSiteSubType) {
        this.createSiteSubType = createSiteSubType;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public Integer getReceiveSiteType() {
        return receiveSiteType;
    }

    public void setReceiveSiteType(Integer receiveSiteType) {
        this.receiveSiteType = receiveSiteType;
    }

    public Integer getReceiveSiteSubType() {
        return receiveSiteSubType;
    }

    public void setReceiveSiteSubType(Integer receiveSiteSubType) {
        this.receiveSiteSubType = receiveSiteSubType;
    }

    public Integer getOperateTypeNode() {
        return operateTypeNode;
    }

    public void setOperateTypeNode(Integer operateTypeNode) {
        this.operateTypeNode = operateTypeNode;
    }

    public Integer getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(Integer operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }
}
