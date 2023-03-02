package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName SendVehicleInfo
 * @Description
 * @Author wyh
 * @Date 2022/5/19 15:25
 **/
public class SendVehicleInfo implements Serializable {

    private static final long serialVersionUID = -2632482789820479232L;

    private String sendDetailBizId;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 线路类型简称
     */
    private String lineTypeShortName;

    /**
     * 车长
     */
    private String carLengthStr;

    /**
     * 发货目的地个数
     */
    private Integer destCount = 0;

    /**
     * 预计发货时间
     */
    private Date planDepartTime;

    /**
     * 发货目的地
     */
    private Integer endSiteId;

    /**
     * 发货目的地
     */
    private String endSiteName;

    /**
     * 司机姓名
     */
    private String driverName;

    /**
     * 司机电话
     */
    private String driverPhone;

    /**
     * 自建任务
     */
    private Boolean manualCreated;

    /**
     * 发货前是否已拍照
     */
    private Boolean photo;

    /**
     * 封车前是否已拍照
     */
    private Boolean sealPhoto;

    /**
     * 无任务是否绑定了任务 true：绑定
     */
    private Boolean noTaskBindVehicle;

    public String getSendDetailBizId() {
        return sendDetailBizId;
    }

    public void setSendDetailBizId(String sendDetailBizId) {
        this.sendDetailBizId = sendDetailBizId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getLineTypeShortName() {
        return lineTypeShortName;
    }

    public void setLineTypeShortName(String lineTypeShortName) {
        this.lineTypeShortName = lineTypeShortName;
    }

    public String getCarLengthStr() {
        return carLengthStr;
    }

    public void setCarLengthStr(String carLengthStr) {
        this.carLengthStr = carLengthStr;
    }

    public Integer getDestCount() {
        return destCount;
    }

    public void setDestCount(Integer destCount) {
        this.destCount = destCount;
    }

    public Date getPlanDepartTime() {
        return planDepartTime;
    }

    public void setPlanDepartTime(Date planDepartTime) {
        this.planDepartTime = planDepartTime;
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

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public Boolean getManualCreated() {
        return manualCreated;
    }

    public void setManualCreated(Boolean manualCreated) {
        this.manualCreated = manualCreated;
    }

    public Boolean getPhoto() {
        return photo;
    }

    public void setPhoto(Boolean photo) {
        this.photo = photo;
    }

    public Boolean getNoTaskBindVehicle() {
        return noTaskBindVehicle;
    }

    public void setNoTaskBindVehicle(Boolean noTaskBindVehicle) {
        this.noTaskBindVehicle = noTaskBindVehicle;
    }

    public Boolean getSealPhoto() {
        return sealPhoto;
    }

    public void setSealPhoto(Boolean sealPhoto) {
        this.sealPhoto = sealPhoto;
    }
}
