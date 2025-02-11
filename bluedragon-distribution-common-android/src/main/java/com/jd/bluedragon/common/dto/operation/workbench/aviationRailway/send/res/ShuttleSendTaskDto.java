package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/15 18:31
 * @Description
 */
public class ShuttleSendTaskDto implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    private String bizId;

    private String detailBizId;

    private String vehicleNumber;

    private Integer nextSiteId;

    private String nextSiteName;

    /**
     * 任务数
     */
    private Integer taskNum;
    /**
     * 总件数
     */
    private Integer totalItemNum;


    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getDetailBizId() {
        return detailBizId;
    }

    public void setDetailBizId(String detailBizId) {
        this.detailBizId = detailBizId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Integer getNextSiteId() {
        return nextSiteId;
    }

    public void setNextSiteId(Integer nextSiteId) {
        this.nextSiteId = nextSiteId;
    }

    public String getNextSiteName() {
        return nextSiteName;
    }

    public void setNextSiteName(String nextSiteName) {
        this.nextSiteName = nextSiteName;
    }

    public Integer getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(Integer taskNum) {
        this.taskNum = taskNum;
    }

    public Integer getTotalItemNum() {
        return totalItemNum;
    }

    public void setTotalItemNum(Integer totalItemNum) {
        this.totalItemNum = totalItemNum;
    }
}
