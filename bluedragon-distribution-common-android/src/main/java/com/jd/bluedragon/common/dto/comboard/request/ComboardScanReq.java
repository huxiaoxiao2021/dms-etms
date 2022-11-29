package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class ComboardScanReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -3505275696826500188L;
    /**
     * 扫描单号
     */
    private String barCode;
    /**
     * 扫描类型
     */
    private Integer scanType;
    /**
     * 混扫任务编号（单独从板进去的时候没有这个参数）
     */
    private String templateCode;

    /**
     * 当前页面所处流向
     */
    private Integer endSiteId;
    private String endSiteName;

    /**
     * 是否支持混扫
     */
    private boolean supportMutilSendFlow;

    /**
     * 用户点击切换流向后
     */
    private boolean needSkipSendFlowCheck;

    /**
     * 单独选板进入扫描的时候要传入（这个时候可能会没有混扫任务的限定（流向从混扫任务给移除了））
     */
    private String boardCode;

    /**
     * 以下不是请求入参，作为参数上下文使用
     */
    private String sendCode;
    private String bizId;
    private boolean cancelLastSend;
    /**
     * 包裹、箱号的目的地
     */
    private Integer destinationId;
    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getScanType() {
        return scanType;
    }

    public void setScanType(Integer scanType) {
        this.scanType = scanType;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public Integer getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Integer endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public boolean getCancelLastSend() {
        return cancelLastSend;
    }

    public void setCancelLastSend(boolean cancelLastSend) {
        this.cancelLastSend = cancelLastSend;
    }

    public boolean getSupportMutilSendFlow() {
        return supportMutilSendFlow;
    }

    public void setSupportMutilSendFlow(boolean supportMutilSendFlow) {
        this.supportMutilSendFlow = supportMutilSendFlow;
    }

    public boolean getNeedSkipSendFlowCheck() {
        return needSkipSendFlowCheck;
    }

    public void setNeedSkipSendFlowCheck(boolean needSkipSendFlowCheck) {
        this.needSkipSendFlowCheck = needSkipSendFlowCheck;
    }

    public Integer getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Integer destinationId) {
        this.destinationId = destinationId;
    }
}
