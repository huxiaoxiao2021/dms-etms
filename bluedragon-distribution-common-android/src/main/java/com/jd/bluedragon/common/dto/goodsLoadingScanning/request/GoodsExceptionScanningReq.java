package com.jd.bluedragon.common.dto.goodsLoadingScanning.request;

import java.util.List;

/**
 * 不齐异常扫描请求
 */
public class GoodsExceptionScanningReq {
    private static final long serialVersionUID = 1L;

//    任务号
    private Long taskId;
//    运单号集合
    private List<String> waybillCode;
//    包裹号
    private String packageCode;
//    当前操作人
    private String operator;
//    当前操作人编码
    private String operatorCode;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }
}
