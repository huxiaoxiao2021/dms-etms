package com.jd.bluedragon.common.dto.goodsLoadingScanning.request;

import java.util.List;

/**
 * 不齐异常扫描请求
 */
public class GoodsExceptionScanningReq {
    private static final long serialVersionUID = 1L;

//    任务号
    private String taskId;

//    批次号
    private String batchCode;

//    运单号集合
    private List<String> waybillCode;

//    包裹号
    private String packageCode;

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

    public List<String> getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(List<String> waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }
}
