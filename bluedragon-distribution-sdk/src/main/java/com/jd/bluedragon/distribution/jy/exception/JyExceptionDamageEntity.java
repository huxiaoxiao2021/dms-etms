package com.jd.bluedragon.distribution.jy.exception;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 异常-破损 entity
 */
public class JyExceptionDamageEntity {


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
     * 保存状态 0：暂存 1 ：保存
     */
    private Boolean saveType;

    /**
     * 修复前长度
     */
    private BigDecimal lengthRepairBefore;

    /**
     * 修复后长度
     */
    private BigDecimal lengthRepairAfter;

    /**
     * 修复前高度
     */
    private BigDecimal heightRepairBefore;

    /**
     * 修复后高度
     */
    private BigDecimal heightRepairAfter;

    /**
     * 修复前宽度
     */
    private BigDecimal widthRepairBefore;

    /**
     * 修复后宽度
     */
    private BigDecimal widthRepairAfter;


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
    private Boolean damageType;

    /**
     * 修复类型（当破损类型为 1时  1：修复 2：更换包装 3：直接下传  当破损类型为2 时 1:无残余价值 2:内物轻微破损 3:内物严重破损）
     */
    private Boolean repairType;

    /**
     * 客服反馈类型（1：修复下传 2：直接下传 3：更换包装下传 4：报废 5：逆向退回）
     */
    private Boolean feedBackType;

    /**
     * 创建人 erp
     */
    private String createErp;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人 erp
     */
    private String updateErp;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 时间戳
     */
    private Date ts;

    /**
     * 删除标识
     */
    private Boolean yn;

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

    public Boolean getSaveType() {
        return saveType;
    }

    public void setSaveType(Boolean saveType) {
        this.saveType = saveType;
    }


    public BigDecimal getLengthRepairBefore() {
        return lengthRepairBefore;
    }

    public void setLengthRepairBefore(BigDecimal lengthRepairBefore) {
        this.lengthRepairBefore = lengthRepairBefore;
    }

    public BigDecimal getLengthRepairAfter() {
        return lengthRepairAfter;
    }

    public void setLengthRepairAfter(BigDecimal lengthRepairAfter) {
        this.lengthRepairAfter = lengthRepairAfter;
    }

    public BigDecimal getHeightRepairBefore() {
        return heightRepairBefore;
    }

    public void setHeightRepairBefore(BigDecimal heightRepairBefore) {
        this.heightRepairBefore = heightRepairBefore;
    }

    public BigDecimal getHeightRepairAfter() {
        return heightRepairAfter;
    }

    public void setHeightRepairAfter(BigDecimal heightRepairAfter) {
        this.heightRepairAfter = heightRepairAfter;
    }

    public BigDecimal getWidthRepairBefore() {
        return widthRepairBefore;
    }

    public void setWidthRepairBefore(BigDecimal widthRepairBefore) {
        this.widthRepairBefore = widthRepairBefore;
    }

    public BigDecimal getWidthRepairAfter() {
        return widthRepairAfter;
    }

    public void setWidthRepairAfter(BigDecimal widthRepairAfter) {
        this.widthRepairAfter = widthRepairAfter;
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


    public Boolean getDamageType() {
        return damageType;
    }

    public void setDamageType(Boolean damageType) {
        this.damageType = damageType;
    }

    public Boolean getRepairType() {
        return repairType;
    }

    public void setRepairType(Boolean repairType) {
        this.repairType = repairType;
    }

    public Boolean getFeedBackType() {
        return feedBackType;
    }

    public void setFeedBackType(Boolean feedBackType) {
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

    public Boolean getYn() {
        return yn;
    }

    public void setYn(Boolean yn) {
        this.yn = yn;
    }
}