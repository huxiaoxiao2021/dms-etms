package com.jd.bluedragon.common.dto.jyexpection.request;

import java.math.BigDecimal;
import java.util.List;

/**
 * 破损任务处理请求参数
 */
public class ExpDamageDetailReq extends ExpBaseReq {
    /**
     * 业务主键
     */
    private String bizId;
    /**
     * 修复前、包装前）图片地址
     */
    private List<String> actualImageUrlList;
    /**
     * （修复后、包装后）图片地址
     */
    private List<String> dealImageUrlList;
    /**
     * 保存状态 0：暂存 1 ：提交待客服反馈 2，提交客服已反馈
     */
    private Integer saveType;

    /**
     * 破损类型（1：外包装破损 2：内破外破）
     */
    private Integer damageType;
    /**
     * 修复类型（当破损类型为 1时  1：修复 2：更换包装 3：直接下传  当破损类型为2 时 1:无残余价值 2:内物轻微破损 3:内物严重破损）
     */
    private Integer repairType;
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

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getSaveType() {
        return saveType;
    }

    public void setSaveType(Integer saveType) {
        this.saveType = saveType;
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

    public List<String> getDealImageUrlList() {
        return dealImageUrlList;
    }

    public void setDealImageUrlList(List<String> dealImageUrlList) {
        this.dealImageUrlList = dealImageUrlList;
    }

    public List<String> getActualImageUrlList() {
        return actualImageUrlList;
    }

    public void setActualImageUrlList(List<String> actualImageUrlList) {
        this.actualImageUrlList = actualImageUrlList;
    }
}
