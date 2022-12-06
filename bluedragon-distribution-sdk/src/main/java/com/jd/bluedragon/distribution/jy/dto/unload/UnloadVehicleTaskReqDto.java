package com.jd.bluedragon.distribution.jy.dto.unload;


import java.io.Serializable;
import java.util.Date;

public class UnloadVehicleTaskReqDto extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = 8483802798838866603L;

    private Integer pageNo;
    private Integer PageSize;
    private Integer vehicleStatus;
    /**
     * com.jd.bluedragon.distribution.jy.enums.JyLineTypeEnum
     */
    private Integer lineType;
    private String packageCode;

    private Date startTime;
    private Date endTime;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return PageSize;
    }

    public void setPageSize(Integer pageSize) {
        PageSize = pageSize;
    }

    public Integer getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(Integer vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public Integer getLineType() {
        return lineType;
    }

    public void setLineType(Integer lineType) {
        this.lineType = lineType;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
