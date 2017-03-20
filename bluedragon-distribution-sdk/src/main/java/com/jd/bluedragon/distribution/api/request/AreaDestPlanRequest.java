package com.jd.bluedragon.distribution.api.request;

/**
 * 龙门架发货关系方案请求
 * <p>
 * Created by lixin39 on 2017/03/18.
 */
public class AreaDestPlanRequest {

    /**
     * 分拣中心编号
     */
    private Integer operateSiteCode;

    /**
     * 龙门架编号
     */
    private Integer machineId;

    /**
     * 方案名称
     */
    private String planName;

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    @Override
    public String toString() {
        return "AreaDestPlanRequest{" +
                "operateSiteCode=" + operateSiteCode +
                ", machineId=" + machineId +
                ", planName='" + planName + '\'' +
                '}';
    }
}
