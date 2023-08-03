package com.jd.bluedragon.common.dto.jyexpection.request;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/26 20:08
 * @Description: 异常类型校验
 */
public class ExpTypeCheckReq extends ExpBaseReq{

    /**
     * 异常包裹号
     */
    private String barCode;

    /**
     * 异常类型 1：报废   2：破损
     */
    private Integer type;

    /**
     * 破损类型（1：外包装破损 2：内破外破）
     */
    private Integer damageType;

    /**
     * 修复类型（当破损类型为 1时  1：修复 2：更换包装 3：直接下传  当破损类型为2 时 1:无残余价值 2:内物轻微破损 3:内物严重破损）
     */
    private Integer repairType;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
