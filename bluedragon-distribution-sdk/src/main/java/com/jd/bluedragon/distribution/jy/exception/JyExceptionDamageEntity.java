package com.jd.bluedragon.distribution.jy.exception;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 异常-破损 entity
 */
public class JyExceptionDamageEntity implements Serializable {


    /**
     * 自增主键
     */
    private Long id;

    /**
     * 业务id
     */
    private String bizId;

    /**
     * 站点code
     */
    private Integer siteCode;

    /**
     * 站点name
     */
    private String siteName;


    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 保存状态 0：暂存 1 ：提交待客服反馈 2，提交客服已反馈
     */
    private Integer saveType;
    /**
     * 修复前体积
     */
    private BigDecimal volumeRepairBefore;
    /**
     * 修复后体积
     */
    private BigDecimal volumeRepairAfter;
    /**
     * 修复前重量
     */
    private BigDecimal weightRepairBefore;

    /**
     * 修复后重量
     */
    private BigDecimal weightRepairAfter;

    /**
     * 破损类型（1：外包装破损 2：内破外破）
     */
    private Integer damageType;

    /**
     * 修复类型（当破损类型为 1：修复 2：更换包装 3：直接下传  当破损类型为2 时 1:无残余价值 2:内物轻微破损 3:内物严重破损）
     */
    private Integer repairType;

    /**
     * 客服反馈类型（1：修复下传 2：直接下传 3：更换包装下传 4：报废 5：逆向退回）
     */
    private Integer feedBackType;

    /**
     * 创建人erp
     */
    private String createErp;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private String updateErp;

    /**
     *
     */
    private Date updateTime;

    /**
     * 时间戳
     */
    private Date ts;

    /**
     * 删除标识: 1: 使用 0 删除
     */
    private Integer yn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getSaveType() {
        return saveType;
    }

    public void setSaveType(Integer saveType) {
        this.saveType = saveType;
    }

    public BigDecimal getWeightRepairBefore() {
        return weightRepairBefore;
    }

    public void setWeightRepairBefore(BigDecimal weightRepairBefore) {
        this.weightRepairBefore = weightRepairBefore;
    }

    public BigDecimal getWeightRepairAfter() {
        return weightRepairAfter;
    }

    public void setWeightRepairAfter(BigDecimal weightRepairAfter) {
        this.weightRepairAfter = weightRepairAfter;
    }


    public Integer getDamageType() {
        return damageType;
    }

    public void setDamageType(Integer damageType) {
        this.damageType = damageType;
    }

    public Integer getRepairType() {
        return repairType;
    }

    public void setRepairType(Integer repairType) {
        this.repairType = repairType;
    }

    public Integer getFeedBackType() {
        return feedBackType;
    }

    public void setFeedBackType(Integer feedBackType) {
        this.feedBackType = feedBackType;
    }

    public String getCreateErp() {
        return createErp;
    }

    public void setCreateErp(String createErp) {
        this.createErp = createErp;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateErp() {
        return updateErp;
    }

    public void setUpdateErp(String updateErp) {
        this.updateErp = updateErp;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public BigDecimal getVolumeRepairBefore() {
        return volumeRepairBefore;
    }

    public void setVolumeRepairBefore(BigDecimal volumeRepairBefore) {
        this.volumeRepairBefore = volumeRepairBefore;
    }

    public BigDecimal getVolumeRepairAfter() {
        return volumeRepairAfter;
    }

    public void setVolumeRepairAfter(BigDecimal volumeRepairAfter) {
        this.volumeRepairAfter = volumeRepairAfter;
    }
}
