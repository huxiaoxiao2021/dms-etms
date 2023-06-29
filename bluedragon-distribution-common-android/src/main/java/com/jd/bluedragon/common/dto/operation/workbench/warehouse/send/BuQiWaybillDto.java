package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import java.io.Serializable;

public class BuQiWaybillDto implements Serializable {


    private static final long serialVersionUID = -5809332610524693231L;


    private String waybillCode;
    /**
     * 已扫
     */
    private Integer scanNum;
    /**
     * 总数
     */
    private Integer totalNum;


    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getScanNum() {
        return scanNum;
    }

    public void setScanNum(Integer scanNum) {
        this.scanNum = scanNum;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }
}
