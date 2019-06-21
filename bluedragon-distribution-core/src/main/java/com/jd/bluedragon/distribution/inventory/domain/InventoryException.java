package com.jd.bluedragon.distribution.inventory.domain;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

import java.util.Date;

public class InventoryException extends DbEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 运单号
     */
    private String waybillCode;
    /**
     * 包裹号
     */
    private String packageCode;
    /**
     * 盘点任务号
     */
    private String inventoryTaskId;
    /**
     * 包裹物流状态
     */
    private String latestPackStatus;
    /**
     * 异常类型1：多货 2：少货
     */
    private Integer expType;
    /**
     * 异常描述
     */
    private String expDesc;
    /**
     * 异常状态0:未处理 1:已处理
     */
    private Integer expStatus;
    /**
     * 盘点站点编码
     */
    private Integer inventorySiteCode;
    /**
     * 盘点站点名称
     */
    private String inventorySiteName;
    /**
     * 卡位id
     */
    private Integer directionCode;
    /**
     * 卡位名称
     */
    private String directionName;
    /**
     * 盘点人编码
     */
    private Integer inventoryUserCode;
    /**
     * 盘点人Erp
     */
    private String inventoryUserErp;
    /**
     * 盘点人姓名
     */
    private String inventoryUserName;
    /**
     * 盘点时间
     */
    private Date inventoryTime;
    /**
     * 异常处理人编码
     */
    private Integer expUserCode;
    /**
     * 异常处理人Erp
     */
    private String expUserErp;
    /**
     * 异常处理人姓名
     */
    private String expUserName;
    /**
     * 异常处理时间
     */
    private Date expOperateTime;

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

    public String getInventoryTaskId() {
        return inventoryTaskId;
    }

    public void setInventoryTaskId(String inventoryTaskId) {
        this.inventoryTaskId = inventoryTaskId;
    }

    public String getLatestPackStatus() {
        return latestPackStatus;
    }

    public void setLatestPackStatus(String latestPackStatus) {
        this.latestPackStatus = latestPackStatus;
    }

    public Integer getExpType() {
        return expType;
    }

    public void setExpType(Integer expType) {
        this.expType = expType;
    }

    public String getExpDesc() {
        return expDesc;
    }

    public void setExpDesc(String expDesc) {
        this.expDesc = expDesc;
    }

    public Integer getExpStatus() {
        return expStatus;
    }

    public void setExpStatus(Integer expStatus) {
        this.expStatus = expStatus;
    }

    public Integer getInventorySiteCode() {
        return inventorySiteCode;
    }

    public void setInventorySiteCode(Integer inventorySiteCode) {
        this.inventorySiteCode = inventorySiteCode;
    }

    public String getInventorySiteName() {
        return inventorySiteName;
    }

    public void setInventorySiteName(String inventorySiteName) {
        this.inventorySiteName = inventorySiteName;
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

    public Integer getInventoryUserCode() {
        return inventoryUserCode;
    }

    public void setInventoryUserCode(Integer inventoryUserCode) {
        this.inventoryUserCode = inventoryUserCode;
    }

    public String getInventoryUserErp() {
        return inventoryUserErp;
    }

    public void setInventoryUserErp(String inventoryUserErp) {
        this.inventoryUserErp = inventoryUserErp;
    }

    public String getInventoryUserName() {
        return inventoryUserName;
    }

    public void setInventoryUserName(String inventoryUserName) {
        this.inventoryUserName = inventoryUserName;
    }

    public Date getInventoryTime() {
        return inventoryTime;
    }

    public void setInventoryTime(Date inventoryTime) {
        this.inventoryTime = inventoryTime;
    }

    public Integer getExpUserCode() {
        return expUserCode;
    }

    public void setExpUserCode(Integer expUserCode) {
        this.expUserCode = expUserCode;
    }

    public String getExpUserErp() {
        return expUserErp;
    }

    public void setExpUserErp(String expUserErp) {
        this.expUserErp = expUserErp;
    }

    public String getExpUserName() {
        return expUserName;
    }

    public void setExpUserName(String expUserName) {
        this.expUserName = expUserName;
    }

    public Date getExpOperateTime() {
        return expOperateTime;
    }

    public void setExpOperateTime(Date expOperateTime) {
        this.expOperateTime = expOperateTime;
    }
}
