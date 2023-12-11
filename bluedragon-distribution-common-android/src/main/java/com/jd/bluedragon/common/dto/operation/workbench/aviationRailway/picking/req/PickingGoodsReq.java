package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/12/4 11:28
 * @Description
 */
public class PickingGoodsReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    private String barCode;
    /**
     * 提货任务类型
     * @see com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodTaskTypeEnum
     */
    private Integer taskType;
    /**
     * 上一次提货的任务BizId
     */
    private String lastScanTaskBizId;

    /**
     * 发货标识：true 提货并发货
     */
    private Boolean sendGoodFlag;
    /**
     * 强发标识
     */
    private Boolean forceSendFlag;
    /**
     * 发货流向场地（强发时必填）
     */
    private Integer nextSiteId;
    private String nextSiteName;
    /**
     * 箱号确认流向场地的key[包裹号]
     */
    private String boxConfirmNextSiteKey;
    /**
     * 切换流向开关
     */
    private Boolean sendNextSiteSwitch;
    /**
     * 切换流向之前的发货流向信息
     */
    private Integer beforeSwitchNextSiteId;
    private String beforeSwitchNextSiteName;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Boolean getSendGoodFlag() {
        return sendGoodFlag;
    }

    public void setSendGoodFlag(Boolean sendGoodFlag) {
        this.sendGoodFlag = sendGoodFlag;
    }

    public Boolean getForceSendFlag() {
        return forceSendFlag;
    }

    public void setForceSendFlag(Boolean forceSendFlag) {
        this.forceSendFlag = forceSendFlag;
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

    public String getBoxConfirmNextSiteKey() {
        return boxConfirmNextSiteKey;
    }

    public void setBoxConfirmNextSiteKey(String boxConfirmNextSiteKey) {
        this.boxConfirmNextSiteKey = boxConfirmNextSiteKey;
    }

    public Boolean getSendNextSiteSwitch() {
        return sendNextSiteSwitch;
    }

    public void setSendNextSiteSwitch(Boolean sendNextSiteSwitch) {
        this.sendNextSiteSwitch = sendNextSiteSwitch;
    }

    public Integer getBeforeSwitchNextSiteId() {
        return beforeSwitchNextSiteId;
    }

    public void setBeforeSwitchNextSiteId(Integer beforeSwitchNextSiteId) {
        this.beforeSwitchNextSiteId = beforeSwitchNextSiteId;
    }

    public String getBeforeSwitchNextSiteName() {
        return beforeSwitchNextSiteName;
    }

    public void setBeforeSwitchNextSiteName(String beforeSwitchNextSiteName) {
        this.beforeSwitchNextSiteName = beforeSwitchNextSiteName;
    }

    public String getLastScanTaskBizId() {
        return lastScanTaskBizId;
    }

    public void setLastScanTaskBizId(String lastScanTaskBizId) {
        this.lastScanTaskBizId = lastScanTaskBizId;
    }
}
