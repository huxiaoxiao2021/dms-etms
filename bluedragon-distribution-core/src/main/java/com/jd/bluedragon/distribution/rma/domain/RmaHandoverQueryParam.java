package com.jd.bluedragon.distribution.rma.domain;

import java.util.Date;

/**
 * RMA交接清单打印查询实体
 * <p>
 * Created by lixin39 on 2018/9/20.
 */
public class RmaHandoverQueryParam {

    /**
     * 发货开始时间
     */
    private Date sendDateStart;

    /**
     * 发货结束时间
     */
    private Date sendDateEnd;

    /**
     * 是否已打印
     */
    private Boolean hasPrint;

    /**
     * 操作分拣中心编号
     */
    private Integer createSiteCode;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 收货地址
     */
    private String receiverAddress;

    public Date getSendDateStart() {
        return sendDateStart;
    }

    public void setSendDateStart(Date sendDateStart) {
        this.sendDateStart = sendDateStart;
    }

    public Date getSendDateEnd() {
        return sendDateEnd;
    }

    public void setSendDateEnd(Date sendDateEnd) {
        this.sendDateEnd = sendDateEnd;
    }

    public Boolean getHasPrint() {
        return hasPrint;
    }

    public void setHasPrint(Boolean hasPrint) {
        this.hasPrint = hasPrint;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

}
