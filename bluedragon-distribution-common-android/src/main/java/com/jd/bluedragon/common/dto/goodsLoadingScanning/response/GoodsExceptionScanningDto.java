package com.jd.bluedragon.common.dto.goodsLoadingScanning.response;

import java.util.List;

public class GoodsExceptionScanningDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    //    任务号
    private Long taskId;

    //    运单号
    private String waybillCode;

    //已装车包裹数量
    private Integer loadAmount;

    //未装车包裹数量
    private Integer unloadAmount;

    //强发包裹数量
    private Integer forceAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getLoadAmount() {
        return loadAmount;
    }

    public void setLoadAmount(Integer loadAmount) {
        this.loadAmount = loadAmount;
    }

    public Integer getUnloadAmount() {
        return unloadAmount;
    }

    public void setUnloadAmount(Integer unloadAmount) {
        this.unloadAmount = unloadAmount;
    }

    public Integer getForceAmount() {
        return forceAmount;
    }

    public void setForceAmount(Integer forceAmount) {
        this.forceAmount = forceAmount;
    }
}
