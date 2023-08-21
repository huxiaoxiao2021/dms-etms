package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author zhengchengfa
 * @Date 2023/8/21 18:43
 * @Description
 */
public class JyBizSendTaskAssociationDto implements Serializable {

    private static final long serialVersionUID = 4959005149526416723L;

    private String transWorkCode;
    private String vehicleNumber;
    private Date lastPlanDepartTime;
    private String sendVehicleBizId;
    private String bizId;
    private String transWorkItemCode;
    private String vehicleStatus;
    private Integer startSiteId;
    private String startSiteName;
    private Integer endSiteId;
    private String endSiteName;


    public String getTransWorkCode() {
        return transWorkCode;
    }

    public void setTransWorkCode(String transWorkCode) {
        this.transWorkCode = transWorkCode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Date getLastPlanDepartTime() {
        return lastPlanDepartTime;
    }

    public void setLastPlanDepartTime(Date lastPlanDepartTime) {
        this.lastPlanDepartTime = lastPlanDepartTime;
    }

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
    }

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public Integer getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Integer startSiteId) {
        this.startSiteId = startSiteId;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
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
}
