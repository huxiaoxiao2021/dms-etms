package com.jd.bluedragon.distribution.areadest.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 龙门架发货关系方案操作流水信息
 * <p>
 * Created by lixin39 on 2017/3/16.
 */
public class AreaDestPlanDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 方案编号
     */
    private Integer planId;

    /**
     * 龙门架编号
     */
    private Integer machineId;

    /**
     * 方案启用时间
     */
    private Date startTime;

    /**
     * 操作站点编号
     */
    private Integer operateSiteCode;

    /**
     * 操作用户编号
     */
    private Integer operateUserCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public Integer getOperateUserCode() {
        return operateUserCode;
    }

    public void setOperateUserCode(Integer operateUserCode) {
        this.operateUserCode = operateUserCode;
    }

}
