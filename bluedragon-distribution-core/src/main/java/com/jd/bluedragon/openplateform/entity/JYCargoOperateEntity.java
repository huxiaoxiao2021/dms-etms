package com.jd.bluedragon.openplateform.entity;

import com.jd.bluedragon.distribution.open.entity.OperatorInfo;
import com.jd.bluedragon.distribution.open.entity.RequestProfile;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.openplateform.entity
 * @ClassName: JYCargoOperateEntity
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/5 17:46
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class JYCargoOperateEntity {

    /**
     * 数据调用信息
     */
    private RequestProfile requestProfile;

    /**
     * 操作人信息
     */
    private OperatorInfo operatorInfo;

    /**
     * 操作单号
     */
    private String barcode;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 批次号
     */
    private String sendCode;

    /**
     * 数据操作类型
     */
    private String dataOperateType;

    /**
     * 流向-始发地信息
     */
    private Integer createSiteId;
    private String createSiteCode;
    private String createSiteName;

    /**
     * 流向-目的地信息
     */
    private Integer receiveSiteId;
    private String receiveSiteCode;
    private String receiveSiteName;

    public RequestProfile getRequestProfile() {
        return requestProfile;
    }

    public void setRequestProfile(RequestProfile requestProfile) {
        this.requestProfile = requestProfile;
    }

    public OperatorInfo getOperatorInfo() {
        return operatorInfo;
    }

    public void setOperatorInfo(OperatorInfo operatorInfo) {
        this.operatorInfo = operatorInfo;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
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

    public String getDataOperateType() {
        return dataOperateType;
    }

    public void setDataOperateType(String dataOperateType) {
        this.dataOperateType = dataOperateType;
    }

    public Integer getCreateSiteId() {
        return createSiteId;
    }

    public void setCreateSiteId(Integer createSiteId) {
        this.createSiteId = createSiteId;
    }

    public String getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(String createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Integer getReceiveSiteId() {
        return receiveSiteId;
    }

    public void setReceiveSiteId(Integer receiveSiteId) {
        this.receiveSiteId = receiveSiteId;
    }

    public String getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(String receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }
}
