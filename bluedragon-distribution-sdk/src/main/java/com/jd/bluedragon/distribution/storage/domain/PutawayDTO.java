package com.jd.bluedragon.distribution.storage.domain;

import java.io.Serializable;

/**
 * 上架操作入参
 *
 * 2018年8月21日10:00:17
 *
 * 刘铎
 */
public class PutawayDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 条码
     * 运单号/包裹号
     */
    private String barCode;

    /**
     * 储位号
     */
    private String storageCode;

    /**
     * 操作分拣中心ID
     */
    private Integer createSiteCode;

    /**
     * 操作分拣中心名称
     */
    private String createSiteName;

    /**
     * 操作分拣中心类型
     */
    private Integer createSiteType;

    /**
     * 所属机构ID
     */
    private Integer orgId;

    /**
     * 所属机构
     */
    private String orgName;

    /**
     * 操作人ERP编号
     */
    private Integer operatorId;

    /**
     * 操作人ERP编号
     */
    private String operatorErp;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作时间
     */
    private Long operateTime;

    /**
     * 暂存来源
     * */
    private Integer storageSource;

    /**
     * 是否强制暂存
     * */
    private Boolean forceStorage;

    public Integer getStorageSource() {
        return storageSource;
    }

    public void setStorageSource(Integer storageSource) {
        this.storageSource = storageSource;
    }

    public Boolean getForceStorage() {
        return forceStorage;
    }

    public void setForceStorage(Boolean forceStorage) {
        this.forceStorage = forceStorage;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getStorageCode() {
        return storageCode;
    }

    public void setStorageCode(String storageCode) {
        this.storageCode = storageCode;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
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

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
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
