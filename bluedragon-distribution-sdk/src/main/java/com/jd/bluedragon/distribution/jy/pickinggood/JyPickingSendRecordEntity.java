package com.jd.bluedragon.distribution.jy.pickinggood;

import java.util.Date;

public class JyPickingSendRecordEntity {
    private Long id;

    private String bizId;

    private Long pickingSiteId;

    private String pickingNodeCode;

    private String packageCode;

    private String waybillCode;

    private String boxCode;

    private String waitScanCode;

    private Byte waitScanCodeType;

    private Long initNextSiteId;

    private String boxInitFlowKey;

    private Byte waitScanFlag;

    private Byte scanFalg;

    private Byte scanCodeType;

    private Byte moreScanFlag;

    private String pickingUserErp;

    private String pickingUserName;

    private Date pickingTime;

    private Byte sendFlag;

    private Long realNextSiteId;

    private String boxRealFlowKey;

    private Byte moreSendFlag;

    private Byte forceSendFlag;

    private Date sendTime;

    private String sendUserErp;

    private String sendUserName;

    private Date createTime;

    private Date updateTime;

    private Boolean yn;

    private Date ts;

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

    public Byte getWaitScanCodeType() {
        return waitScanCodeType;
    }

    public void setWaitScanCodeType(Byte waitScanCodeType) {
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

    public Byte getWaitScanFlag() {
        return waitScanFlag;
    }

    public void setWaitScanFlag(Byte waitScanFlag) {
        this.waitScanFlag = waitScanFlag;
    }

    public Byte getScanFalg() {
        return scanFalg;
    }

    public void setScanFalg(Byte scanFalg) {
        this.scanFalg = scanFalg;
    }

    public Byte getScanCodeType() {
        return scanCodeType;
    }

    public void setScanCodeType(Byte scanCodeType) {
        this.scanCodeType = scanCodeType;
    }

    public Byte getMoreScanFlag() {
        return moreScanFlag;
    }

    public void setMoreScanFlag(Byte moreScanFlag) {
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

    public Byte getSendFlag() {
        return sendFlag;
    }

    public void setSendFlag(Byte sendFlag) {
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

    public Byte getMoreSendFlag() {
        return moreSendFlag;
    }

    public void setMoreSendFlag(Byte moreSendFlag) {
        this.moreSendFlag = moreSendFlag;
    }

    public Byte getForceSendFlag() {
        return forceSendFlag;
    }

    public void setForceSendFlag(Byte forceSendFlag) {
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

    public Boolean getYn() {
        return yn;
    }

    public void setYn(Boolean yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}