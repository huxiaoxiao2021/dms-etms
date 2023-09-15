package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/4 18:18
 * @Description
 */
public class ShuttleTaskSealCarQueryRes implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    private Double weight;

    private Double volume;
    /**
     * 件数
     */
    private Integer itemNum;
    /**
     * 托盘数
     */
    private Integer palletCount;
    /**
     * 运力编码
     */
    private String transportCode;
    /**
     * 出发时间
     */
    private String departureTimeStr;
    /**
     * 已封航空任务数量
     */
    private Integer taskNum;
    /**
     * 封签码数量
     */
    private List<String> sealCodes;
    /**
     * 批次号
     */
    private List<String> bindBatchCodes;


    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Integer getItemNum() {
        return itemNum;
    }

    public void setItemNum(Integer itemNum) {
        this.itemNum = itemNum;
    }

    public Integer getPalletCount() {
        return palletCount;
    }

    public void setPalletCount(Integer palletCount) {
        this.palletCount = palletCount;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public String getDepartureTimeStr() {
        return departureTimeStr;
    }

    public void setDepartureTimeStr(String departureTimeStr) {
        this.departureTimeStr = departureTimeStr;
    }

    public Integer getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(Integer taskNum) {
        this.taskNum = taskNum;
    }

    public List<String> getSealCodes() {
        return sealCodes;
    }

    public void setSealCodes(List<String> sealCodes) {
        this.sealCodes = sealCodes;
    }

    public List<String> getBindBatchCodes() {
        return bindBatchCodes;
    }

    public void setBindBatchCodes(List<String> bindBatchCodes) {
        this.bindBatchCodes = bindBatchCodes;
    }
}
