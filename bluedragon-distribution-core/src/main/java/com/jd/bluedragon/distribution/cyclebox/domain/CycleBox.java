package com.jd.bluedragon.distribution.cyclebox.domain;

import java.io.Serializable;
import java.util.List;

public class CycleBox implements Serializable{
    private static final long serialVersionUID = 1L;

    /**
     * 运单号列表
     */
    private List<String> waybillCodeList;

    /**
     * 青流箱数量
     */
    private Integer cycleBoxNum;

    /**
     * 青流箱箱号列表
     */
    private List<String> cycleBoxCodeList;

    /**
     * 流水号
     */
    private String batchCode;

    public List<String> getWaybillCodeList() {
        return waybillCodeList;
    }

    public void setWaybillCodeList(List<String> waybillCodeList) {
        this.waybillCodeList = waybillCodeList;
    }

    public Integer getCycleBoxNum() {
        return cycleBoxNum;
    }

    public void setCycleBoxNum(Integer cycleBoxNum) {
        this.cycleBoxNum = cycleBoxNum;
    }

    public List<String> getCycleBoxCodeList() {
        return cycleBoxCodeList;
    }

    public void setCycleBoxCodeList(List<String> cycleBoxCodeList) {
        this.cycleBoxCodeList = cycleBoxCodeList;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }
}
