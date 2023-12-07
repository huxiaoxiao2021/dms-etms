package com.jd.bluedragon.distribution.jy.pickinggood;

import java.util.Date;

/**
 * 空提待提、提货、发货记录表
 */
public class JyPickingSendRecordEntity {
    private Long id;

    private String bizId;
    /**
     * 提货场地id
     */
    private Long pickingSiteId;
    /**
     * 提货机场编码/提货车站编码
     */
    private String pickingNodeCode;
    /**
     *
     */
    private String packageCode;
    /**
     *
     */
    private String waybillCode;
    /**
     *
     */
    private String boxCode;
    /**
     * 待提单据
     */
    private String waitScanCode;
    /**
     * 类型[1-包裹号，2-箱号]
     */
    private Integer waitScanCodeType;
    /**
     * 待提初始化时单据的发货流向
     */
    private Long initNextSiteId;
    /**
     * 箱号初始化流向依据的包裹号
     */
    private String boxInitFlowKey;
    /**
     * 待提标识：1是
     */
    private Integer waitScanFlag;
    /**
     * 已提标识：1是
     */
    private Integer scanFlag;
    /**
     * 实际提货单据类型[1-包裹号，2-箱号]
     */
    private Integer scanCodeType;
    /**
     * 多提标识：1是
     */
    private Integer moreScanFlag;
    /**
     *
     */
    private String pickingUserErp;
    /**
     *
     */
    private String pickingUserName;
    /**
     * 提货时间
     */
    private Date pickingTime;
    /**
     * 发货标识：1是
     */
    private Integer sendFlag;
    /**
     * 实际发货下一场地id
     */
    private Long realNextSiteId;
    /**
     * 箱发货流向依据的包裹号
     */
    private String boxRealFlowKey;
    /**
     * 是否多发：1是[多发=多提+已发]
     */
    private Integer moreSendFlag;
    /**
     * 是否强发：1是
     */
    private Integer forceSendFlag;
    /**
     * 发货操作时间
     */
    private Date sendTime;
    /**
     *
     */
    private String sendUserErp;
    /**
     *
     */
    private String sendUserName;

    private Date createTime;

    private Date updateTime;

    private Integer yn;

    private Date ts;


    public JyPickingSendRecordEntity() {
    }

    public JyPickingSendRecordEntity(Long pickingSiteId) {
        this.pickingSiteId = pickingSiteId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Long getPickingSiteId() {
        return pickingSiteId;
    }

    public void setPickingSiteId(Long pickingSiteId) {
        this.pickingSiteId = pickingSiteId;
    }

    public String getPickingNodeCode() {
        return pickingNodeCode;
    }

    public void setPickingNodeCode(String pickingNodeCode) {
        this.pickingNodeCode = pickingNodeCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getWaitScanCode() {
        return waitScanCode;
    }

    public void setWaitScanCode(String waitScanCode) {
        this.waitScanCode = waitScanCode;
    }

    public Integer getWaitScanCodeType() {
        return waitScanCodeType;
    }

    public void setWaitScanCodeType(Integer waitScanCodeType) {
        this.waitScanCodeType = waitScanCodeType;
    }

    public Long getInitNextSiteId() {
        return initNextSiteId;
    }

    public void setInitNextSiteId(Long initNextSiteId) {
        this.initNextSiteId = initNextSiteId;
    }

    public String getBoxInitFlowKey() {
        return boxInitFlowKey;
    }

    public void setBoxInitFlowKey(String boxInitFlowKey) {
        this.boxInitFlowKey = boxInitFlowKey;
    }

    public Integer getWaitScanFlag() {
        return waitScanFlag;
    }

    public void setWaitScanFlag(Integer waitScanFlag) {
        this.waitScanFlag = waitScanFlag;
    }

    public Integer getScanFlag() {
        return scanFlag;
    }

    public void setScanFlag(Integer scanFlag) {
        this.scanFlag = scanFlag;
    }

    public Integer getScanCodeType() {
        return scanCodeType;
    }

    public void setScanCodeType(Integer scanCodeType) {
        this.scanCodeType = scanCodeType;
    }

    public Integer getMoreScanFlag() {
        return moreScanFlag;
    }

    public void setMoreScanFlag(Integer moreScanFlag) {
        this.moreScanFlag = moreScanFlag;
    }

    public String getPickingUserErp() {
        return pickingUserErp;
    }

    public void setPickingUserErp(String pickingUserErp) {
        this.pickingUserErp = pickingUserErp;
    }

    public String getPickingUserName() {
        return pickingUserName;
    }

    public void setPickingUserName(String pickingUserName) {
        this.pickingUserName = pickingUserName;
    }

    public Date getPickingTime() {
        return pickingTime;
    }

    public void setPickingTime(Date pickingTime) {
        this.pickingTime = pickingTime;
    }

    public Integer getSendFlag() {
        return sendFlag;
    }

    public void setSendFlag(Integer sendFlag) {
        this.sendFlag = sendFlag;
    }

    public Long getRealNextSiteId() {
        return realNextSiteId;
    }

    public void setRealNextSiteId(Long realNextSiteId) {
        this.realNextSiteId = realNextSiteId;
    }

    public String getBoxRealFlowKey() {
        return boxRealFlowKey;
    }

    public void setBoxRealFlowKey(String boxRealFlowKey) {
        this.boxRealFlowKey = boxRealFlowKey;
    }

    public Integer getMoreSendFlag() {
        return moreSendFlag;
    }

    public void setMoreSendFlag(Integer moreSendFlag) {
        this.moreSendFlag = moreSendFlag;
    }

    public Integer getForceSendFlag() {
        return forceSendFlag;
    }

    public void setForceSendFlag(Integer forceSendFlag) {
        this.forceSendFlag = forceSendFlag;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getSendUserErp() {
        return sendUserErp;
    }

    public void setSendUserErp(String sendUserErp) {
        this.sendUserErp = sendUserErp;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public void setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}