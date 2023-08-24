package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class CancelSendScanReq extends BaseReq implements Serializable {


    private static final long serialVersionUID = -5809332610524693231L;


    /**
     * 混扫任务编码
     */
    private String mixScanTaskCode;

    /**
     * 取消时扫描的号
     */
    private String barCode;

    /**
     * 按照什么类型维度去取消 1包裹 2运单 3箱号 5板号 （非必填）
     */
    private Integer type;

    public String getMixScanTaskCode() {
        return mixScanTaskCode;
    }

    public void setMixScanTaskCode(String mixScanTaskCode) {
        this.mixScanTaskCode = mixScanTaskCode;
    }

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
}
