package com.jd.bluedragon.common.dto.jyexpection.request;

import java.math.BigDecimal;

/**
 * 破损任务处理请求参数
 */
public class ExpDamageDetailReq extends ExpBaseReq {
    /**
     * 业务主键
     */
    private String bizId;
    /**
     * 物品照片， 多个逗号分隔
     */
    private String actualImageUrl;
    /**
     * 存储类型 0暂存 1提交
     */
    private Integer saveType;
    /**
     * 重量
     */
    private BigDecimal weight;
    /**
     * 破损类型（1：外包装破损 2：内破外破）
     */
    private Integer damageType;
    /**
     * 修复类型（当破损类型为 1时  1：修复 2：更换包装 3：直接下传  当破损类型为2 时 1:无残余价值 2:内物轻微破损 3:内物严重破损）
     */
    private Integer repairType;


    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getActualImageUrl() {
        return actualImageUrl;
    }

    public void setActualImageUrl(String actualImageUrl) {
        this.actualImageUrl = actualImageUrl;
    }

    public Integer getSaveType() {
        return saveType;
    }

    public void setSaveType(Integer saveType) {
        this.saveType = saveType;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
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
}
