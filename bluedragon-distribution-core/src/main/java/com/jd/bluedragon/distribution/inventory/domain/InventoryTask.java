package com.jd.bluedragon.distribution.inventory.domain;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

import java.util.Date;

/**
 * 盘点任务
 */
public class InventoryTask extends DbEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 盘点任务号
     */
    private String inventoryTaskId;

    /**
     * 机构编号
     */
    private Integer orgId;
    /**
     * 机构名称
     */
    private String orgName;

    /**
     * 始发分拣中心id
     */
    private Integer createSiteCode;
    /**
     * 始发分拣中心名称
     */
    private String createSiteName;
    /**
     * 卡位id
     */
    private Integer directionCode;
    /**
     * 卡位名称
     */
    private String directionName;
    /**
     * 操作人编码
     */
    private Integer createUserCode;
    /**
     * 操作人erp
     */
    private String createUserErp;
    /**
     * 操作人姓名
     */
    private String createUserName;
    /**
     * 任务创建时间
     */
    private Date createTime;
    /**
     * 任务结束时间
     */
    private Date endTime;
    /**
     * 任务状态 1：进行中 2：已完成
     */
    private Integer status;
    /**
     * 任务协助类型 0：创建 1：协助
     */
    private Integer cooperateType;
    /**
     * 盘点运单数量
     */
    private Integer waybillSum;
    /**
     * 盘点包裹数量
     */
    private Integer packageSum;
    /**
     * 盘点异常数量
     */
    private Integer exceptionSum;

    private Integer inventoryScope;

    public String getInventoryTaskId() {
        return inventoryTaskId;
    }

    public void setInventoryTaskId(String inventoryTaskId) {
        this.inventoryTaskId = inventoryTaskId;
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

    public Integer getDirectionCode() {
        return directionCode;
    }

    public void setDirectionCode(Integer directionCode) {
        this.directionCode = directionCode;
    }

    public String getDirectionName() {
        return directionName;
    }

    public void setDirectionName(String directionName) {
        this.directionName = directionName;
    }

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCooperateType() {
        return cooperateType;
    }

    public void setCooperateType(Integer cooperateType) {
        this.cooperateType = cooperateType;
    }

    public Integer getWaybillSum() {
        return waybillSum;
    }

    public void setWaybillSum(Integer waybillSum) {
        this.waybillSum = waybillSum;
    }

    public Integer getPackageSum() {
        return packageSum;
    }

    public void setPackageSum(Integer packageSum) {
        this.packageSum = packageSum;
    }

    public Integer getExceptionSum() {
        return exceptionSum;
    }

    public void setExceptionSum(Integer exceptionSum) {
        this.exceptionSum = exceptionSum;
    }

    public Integer getInventoryScope() {
        return inventoryScope;
    }

    public void setInventoryScope(Integer inventoryScope) {
        this.inventoryScope = inventoryScope;
    }
}
