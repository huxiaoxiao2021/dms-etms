package com.jd.bluedragon.distribution.departure.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by dudong on 2014/11/20.
 */
public class DepartureLog implements Serializable{

    private Long departureLogID;        // 主键ID
    private Integer distributeCode;     // 收货分拣中心code
    private String distributeName;      // 收货分拣中心名称
    private Integer operatorCode;       // 收货操作人code
    private String operatorName;        // 收货操作人名称
    private Date departureTime;            // 发车时间
    private Date receiveTime;           // 收货时间
    private Long departureCarID;        // 发车批次号
    private String capacityCode;        // 运力编码
    private String fingerPrint;         // 指纹

    public Long getDepartureLogID() {
        return departureLogID;
    }

    public void setDepartureLogID(Long departureLogID) {
        this.departureLogID = departureLogID;
    }

    public Integer getDistributeCode() {
        return distributeCode;
    }

    public void setDistributeCode(Integer distributeCode) {
        this.distributeCode = distributeCode;
    }

    public String getDistributeName() {
        return distributeName;
    }

    public void setDistributeName(String distributeName) {
        this.distributeName = distributeName;
    }

    public Integer getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(Integer operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }


    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Long getDepartureCarID() {
        return departureCarID;
    }

    public void setDepartureCarID(Long departureCarID) {
        this.departureCarID = departureCarID;
    }

    public String getCapacityCode() {
        return capacityCode;
    }

    public void setCapacityCode(String capacityCode) {
        this.capacityCode = capacityCode;
    }

    public String getFingerPrint() {
        return fingerPrint;
    }

    public void setFingerPrint(String fingerPrint) {
        this.fingerPrint = fingerPrint;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }
}
