package com.jd.bluedragon.common.dto.goodsLoadingScanning.response;

import java.util.List;

public class GoodsExceptionScanningDto {

    private static final long serialVersionUID = 1L;

    private Long id;

    //    任务号
    private String taskId;

    //    批次号
    private String batchCode;

    //    运单号
    private String waybillCode;

    //已装车包裹数量
    private String loadAmount;

    //未装车包裹数量
    private String unloadAmount;

    //强发包裹数量
    private String forceAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getLoadAmount() {
        return loadAmount;
    }

    public void setLoadAmount(String loadAmount) {
        this.loadAmount = loadAmount;
    }

    public String getUnloadAmount() {
        return unloadAmount;
    }

    public void setUnloadAmount(String unloadAmount) {
        this.unloadAmount = unloadAmount;
    }

    public String getForceAmount() {
        return forceAmount;
    }

    public void setForceAmount(String forceAmount) {
        this.forceAmount = forceAmount;
    }
}
