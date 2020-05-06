package com.jd.bluedragon.common.dto.send.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

public class DeliveryRequest {

    /**
     * 用户
     */
    private User user;

    /**
     * 当前站点
     */
    private CurrentOperate currentOperate;

    /** 收货单位编号 */
    private Integer receiveSiteCode;

    /** 箱号 */
    private String boxCode;

    /** 批次号 */
    private String sendCode;

    /**
     * 运输计划编码
     */
    private String transPlanCode;

    /** 周转箱号 */
    private String turnoverBoxCode;

    /** 航标发货标示*/
    private Integer transporttype;

    /** 运输类型（默认老发货：0，快运发货：1）*/
    private Integer opType;
    /**
     * 已发货的包裹数量
     */
    private Integer hasSendPackageNum;
    /**
     * 已扫描的包裹数,老发货\快运需要先扫描后一起发货
     */
    private Integer scannedPackageNum;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getTurnoverBoxCode() {
        return turnoverBoxCode;
    }

    public void setTurnoverBoxCode(String turnoverBoxCode) {
        this.turnoverBoxCode = turnoverBoxCode;
    }

    public Integer getTransporttype() {
        return transporttype;
    }

    public void setTransporttype(Integer transporttype) {
        this.transporttype = transporttype;
    }

    public Integer getOpType() {
        return opType;
    }

    public void setOpType(Integer opType) {
        this.opType = opType;
    }

    public Integer getHasSendPackageNum() {
        return hasSendPackageNum;
    }

    public void setHasSendPackageNum(Integer hasSendPackageNum) {
        this.hasSendPackageNum = hasSendPackageNum;
    }

    public Integer getScannedPackageNum() {
        return scannedPackageNum;
    }

    public void setScannedPackageNum(Integer scannedPackageNum) {
        this.scannedPackageNum = scannedPackageNum;
    }

    public String getTransPlanCode() {
        return transPlanCode;
    }

    public void setTransPlanCode(String transPlanCode) {
        this.transPlanCode = transPlanCode;
    }
}
