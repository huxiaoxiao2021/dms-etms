package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
/**
 * @Author zhengchengfa
 * @Date 2023/12/4 13:29
 * @Description XXX
 */
public class ExceptionSubmitReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -8246822903202645446L;

    /**
     * 提货任务主键
     */
    private String bizId;

    /**
     * 班次号：航班号/车次号
     */
    private String serviceNumber;

    /**
     * 提货任务类型
     * @see com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodTaskTypeEnum
     */
    private Integer taskType;

    /**
     * 当前场地总数待提数量
     */
    private Integer currentSiteWaitScan = 0;
    /**
     * 当前场地已提总数
     */
    private Integer currentSiteHaveScanned = 0;
    /**
     * 当前场地多提总数
     */
    private Integer currentSiteMultipleScan = 0;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getServiceNumber() {
        return serviceNumber;
    }

    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getCurrentSiteWaitScan() {
        return currentSiteWaitScan;
    }

    public void setCurrentSiteWaitScan(Integer currentSiteWaitScan) {
        this.currentSiteWaitScan = currentSiteWaitScan;
    }

    public Integer getCurrentSiteHaveScanned() {
        return currentSiteHaveScanned;
    }

    public void setCurrentSiteHaveScanned(Integer currentSiteHaveScanned) {
        this.currentSiteHaveScanned = currentSiteHaveScanned;
    }

    public Integer getCurrentSiteMultipleScan() {
        return currentSiteMultipleScan;
    }

    public void setCurrentSiteMultipleScan(Integer currentSiteMultipleScan) {
        this.currentSiteMultipleScan = currentSiteMultipleScan;
    }
}
