package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.base.request.OperatorData;

import java.io.Serializable;
import java.util.Date;

public class ComboardScanReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -3505275696826500188L;
    //  对外暴露的枚举 BusinessCodeFromSourceEnum
    private String bizSource = "JY_APP";
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
     * 用户所处当前页面流向
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

    /**
     *单号类型，参见BarCodeType
     */
    private Integer barCodeType;

    /**
     * 扫描件数（包裹或者箱子，扫描运单要返回包裹数量）
     */
    private Integer scanDetailCount;

    /**
     * 集包袋号
     */
    private String materialCode;

    /**
     * 强发标识
     */
    private boolean forceSendFlag;

    /**
     * 是否跳过弱拦截
     */
    private boolean needSkipWeakIntercept;

    private boolean needIntercept;

    private Date operateTime;

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    /**
     * com_board_box表业务主键
     */
    private String operateKey;

    /**
     * 操作流水表主键
     */
    private Long operateFlowId;

    /**
     * 笼车箱号
     */
    private String cageCarCode;

    /**
     * 笼车需绑定物资码
     */
    private String cageCarMaterialCode;


    /**
     * 下传方式
     */
    private Integer deliveryType;

    /**
     * 集包任务bizId
     */
    private String collectPackageTaskBizId;

    /**
     * 自动装笼版本标识
     */
    private boolean autoCollectVersion;

    public boolean getAutoCollectVersion() {
        return autoCollectVersion;
    }

    public void setAutoCollectVersion(boolean autoCollectVersion) {
        this.autoCollectVersion = autoCollectVersion;
    }

    public String getCollectPackageTaskBizId() {
        return collectPackageTaskBizId;
    }

    public void setCollectPackageTaskBizId(String collectPackageTaskBizId) {
        this.collectPackageTaskBizId = collectPackageTaskBizId;
    }

    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getCageCarMaterialCode() {
        return cageCarMaterialCode;
    }

    public void setCageCarMaterialCode(String cageCarMaterialCode) {
        this.cageCarMaterialCode = cageCarMaterialCode;
    }

    public String getCageCarCode() {
        return cageCarCode;
    }

    public void setCageCarCode(String cageCarCode) {
        this.cageCarCode = cageCarCode;
    }

    public boolean getNeedSkipWeakIntercept() {
        return needSkipWeakIntercept;
    }

    public void setNeedSkipWeakIntercept(boolean needSkipWeakIntercept) {
        this.needSkipWeakIntercept = needSkipWeakIntercept;
    }

    public boolean getForceSendFlag() {
        return forceSendFlag;
    }

    public void setForceSendFlag(boolean forceSendFlag) {
        this.forceSendFlag = forceSendFlag;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public String getBizSource() {
        return bizSource;
    }

    public void setBizSource(String bizSource) {
        this.bizSource = bizSource;
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

    public Integer getBarCodeType() {
        return barCodeType;
    }

    public void setBarCodeType(Integer barCodeType) {
        this.barCodeType = barCodeType;
    }

    public Integer getScanDetailCount() {
        return scanDetailCount;
    }

    public void setScanDetailCount(Integer scanDetailCount) {
        this.scanDetailCount = scanDetailCount;
    }

    public boolean getNeedIntercept() {
        return needIntercept;
    }

    public void setNeedIntercept(boolean needIntercept) {
        this.needIntercept = needIntercept;
    }

    public String getOperateKey() {
        return operateKey;
    }

    public void setOperateKey(String operateKey) {
        this.operateKey = operateKey;
    }

    public Long getOperateFlowId() {
        return operateFlowId;
    }

    public void setOperateFlowId(Long operateFlowId) {
        this.operateFlowId = operateFlowId;
    }
}
