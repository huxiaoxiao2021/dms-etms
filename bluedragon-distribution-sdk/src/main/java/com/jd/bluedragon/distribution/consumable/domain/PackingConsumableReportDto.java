package com.jd.bluedragon.distribution.consumable.domain;

import java.util.Date;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.consumable.domain
 * @ClassName: PackingConsumableReportDto
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/2/11 14:42
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class PackingConsumableReportDto {

    /** 运单号 */
    private String waybillCode;

    /** 揽收人机构 */
    private Integer collectionOrgId;

    /** 揽收人机构名称 */
    private String collectionOrgName;

    /** 揽收人站点 */
    private Integer collectionSiteCode;

    /** 揽收人站点名称 */
    private String collectionSiteName;

    /** 揽收人erp */
    private String collectionErp;

    /** 揽收人员工ID **/
    private Integer collectionUserId;

    /** 确认人机构 **/
    private Integer confirmOrgId;

    /** 确认人机构名称 **/
    private String confirmOrgName;

    /** 确认人站点 */
    private Integer confirmSiteCode;

    /** 确认人站点名称 **/
    private String confirmSiteName;

    /** 确认人erp */
    private String confirmErp;

    /** 操作时间 */
    private Date confirmTime;

    /** 包装耗材价格明细文本 **/
    private String packingChargeDes;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getCollectionOrgId() {
        return collectionOrgId;
    }

    public void setCollectionOrgId(Integer collectionOrgId) {
        this.collectionOrgId = collectionOrgId;
    }

    public String getCollectionOrgName() {
        return collectionOrgName;
    }

    public void setCollectionOrgName(String collectionOrgName) {
        this.collectionOrgName = collectionOrgName;
    }

    public Integer getCollectionSiteCode() {
        return collectionSiteCode;
    }

    public void setCollectionSiteCode(Integer collectionSiteCode) {
        this.collectionSiteCode = collectionSiteCode;
    }

    public String getCollectionSiteName() {
        return collectionSiteName;
    }

    public void setCollectionSiteName(String collectionSiteName) {
        this.collectionSiteName = collectionSiteName;
    }

    public String getCollectionErp() {
        return collectionErp;
    }

    public void setCollectionErp(String collectionErp) {
        this.collectionErp = collectionErp;
    }

    public Integer getCollectionUserId() {
        return collectionUserId;
    }

    public void setCollectionUserId(Integer collectionUserId) {
        this.collectionUserId = collectionUserId;
    }

    public Integer getConfirmOrgId() {
        return confirmOrgId;
    }

    public void setConfirmOrgId(Integer confirmOrgId) {
        this.confirmOrgId = confirmOrgId;
    }

    public String getConfirmOrgName() {
        return confirmOrgName;
    }

    public void setConfirmOrgName(String confirmOrgName) {
        this.confirmOrgName = confirmOrgName;
    }

    public Integer getConfirmSiteCode() {
        return confirmSiteCode;
    }

    public void setConfirmSiteCode(Integer confirmSiteCode) {
        this.confirmSiteCode = confirmSiteCode;
    }

    public String getConfirmSiteName() {
        return confirmSiteName;
    }

    public void setConfirmSiteName(String confirmSiteName) {
        this.confirmSiteName = confirmSiteName;
    }

    public String getConfirmErp() {
        return confirmErp;
    }

    public void setConfirmErp(String confirmErp) {
        this.confirmErp = confirmErp;
    }

    public Date getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Date confirmTime) {
        this.confirmTime = confirmTime;
    }

    public String getPackingChargeDes() {
        return packingChargeDes;
    }

    public void setPackingChargeDes(String packingChargeDes) {
        this.packingChargeDes = packingChargeDes;
    }
}
