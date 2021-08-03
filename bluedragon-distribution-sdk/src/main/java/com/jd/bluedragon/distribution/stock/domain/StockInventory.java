package com.jd.bluedragon.distribution.stock.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 库存盘点
 *
 * @author hujiping
 * @date 2021/6/4 2:12 下午
 */
public class StockInventory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 盘点状态 0：未盘点 1：已盘点 2：被他人盘点
     */
    private Integer inventoryStatus;

    /**
     * 波次编码
     */
    private String waveCode;

    /**
     * 波次开始时间
     */
    private Date waveBeginTime;

    /**
     * 波次结束时间
     */
    private Date waveEndTime;

    /**
     * 盘点时间
     */
    private Date inventoryTime;

    /**
     * 操作人分拣中心ID
     */
    private Integer operateSiteCode;

    /**
     * 操作人分拣中心名称
     */
    private String operateSiteName;

    /**
     * 操作人ID
     */
    private Integer operateUserId;

    /**
     * 操作人ERP
     */
    private String operateUserErp;

    /**
     * 操作人姓名
     */
    private String operateUserName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人ERP
     */
    private String updateUserErp;

    /**
     * 删除标识: 1 未删除, 0 已删除
     */
    private Integer yn;

    /**
     * 数据库时间
     */
    private Date ts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(Integer inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    public String getWaveCode() {
        return waveCode;
    }

    public void setWaveCode(String waveCode) {
        this.waveCode = waveCode;
    }

    public Date getWaveBeginTime() {
        return waveBeginTime;
    }

    public void setWaveBeginTime(Date waveBeginTime) {
        this.waveBeginTime = waveBeginTime;
    }

    public Date getWaveEndTime() {
        return waveEndTime;
    }

    public void setWaveEndTime(Date waveEndTime) {
        this.waveEndTime = waveEndTime;
    }

    public Date getInventoryTime() {
        return inventoryTime;
    }

    public void setInventoryTime(Date inventoryTime) {
        this.inventoryTime = inventoryTime;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public Integer getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(Integer operateUserId) {
        this.operateUserId = operateUserId;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}
