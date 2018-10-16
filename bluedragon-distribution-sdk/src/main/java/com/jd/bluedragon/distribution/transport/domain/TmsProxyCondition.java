package com.jd.bluedragon.distribution.transport.domain;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.util.Date;

/**
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2018年09月25日 14时:16分
 */
public class TmsProxyCondition  extends BasePagerCondition {

    private static final long serialVersionUID = 1L;
    /**
     * 委托书号
     */
    private String transBookCode;
    /**
     * 批次号
     */
    private String billCode;
    /**
     * 始发分拣中心COde
     */
    private String beginNodeCode;
    /**
     * 始发分拣中心名称
     */
    private String beginNodeName;
    /**
     * 目的分拣中心code
     */
    private String endNodeCode;
    /**
     * 目的分拣中心名称
     */
    private String endNodeName;
    /**
     * 车牌号
     */
    private String vehicleNumber;
    /**
     * 预约提货起始时间
     */
    private Date requirePickupTimeBegin;
    /**
     * 预约提货结束时间
     */
    private Date requirePickupTimeEnd;

    public String getTransBookCode() {
        return transBookCode;
    }

    public void setTransBookCode(String transBookCode) {
        this.transBookCode = transBookCode;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public String getBeginNodeCode() {
        return beginNodeCode;
    }

    public void setBeginNodeCode(String beginNodeCode) {
        this.beginNodeCode = beginNodeCode;
    }

    public String getBeginNodeName() {
        return beginNodeName;
    }

    public void setBeginNodeName(String beginNodeName) {
        this.beginNodeName = beginNodeName;
    }

    public String getEndNodeCode() {
        return endNodeCode;
    }

    public void setEndNodeCode(String endNodeCode) {
        this.endNodeCode = endNodeCode;
    }

    public String getEndNodeName() {
        return endNodeName;
    }

    public void setEndNodeName(String endNodeName) {
        this.endNodeName = endNodeName;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Date getRequirePickupTimeBegin() {
        return requirePickupTimeBegin;
    }

    public void setRequirePickupTimeBegin(Date requirePickupTimeBegin) {
        this.requirePickupTimeBegin = requirePickupTimeBegin;
    }

    public Date getRequirePickupTimeEnd() {
        return requirePickupTimeEnd;
    }

    public void setRequirePickupTimeEnd(Date requirePickupTimeEnd) {
        this.requirePickupTimeEnd = requirePickupTimeEnd;
    }
}
