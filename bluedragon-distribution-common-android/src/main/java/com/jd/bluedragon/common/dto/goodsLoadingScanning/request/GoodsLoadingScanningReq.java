package com.jd.bluedragon.common.dto.goodsLoadingScanning.request;

import java.io.Serializable;
import java.util.List;

public class GoodsLoadingScanningReq implements Serializable {

    private static final long serialVersionUID = 1L;

    //包裹号
    private String packageNo;

    //任务号
    private String taskNo;

    //批次号
    private String batchNo;

    //运单号
    private List<String> transBillNoList;

    public String getPackageNo() {
        return packageNo;
    }

    public void setPackageNo(String packageNo) {
        this.packageNo = packageNo;
    }

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public List<String> getTransBillNoList() {
        return transBillNoList;
    }

    public void setTransBillNoList(List<String> transBillNoList) {
        this.transBillNoList = transBillNoList;
    }
}
