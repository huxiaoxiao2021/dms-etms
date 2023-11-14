package com.jd.bluedragon.common.dto.send.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class CreateAviationTaskReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -5531009617431175788L;

    /**
     * 航空类型
     * com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.AirTypeEnum
     */
    private Integer airType;
    /**
     * 航班号
     */
    private String flightNumber;
    /**
     * 目的地ID
     */
    private Integer nextSiteId;
    private String nextSiteName;
    /**
     * 预计订舱量
     */
    private Double bookingWeight;
    /**
     * 确定创建自建任务
     */
    private Boolean confirmCreate;
    /**
     * 预计起飞时间
     */
    private Long takeOffTimeStamp;
    /**
     * 预计降落时间
     */
    private Long touchDownTimeStamp;


    public Integer getAirType() {
        return airType;
    }

    public void setAirType(Integer airType) {
        this.airType = airType;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Integer getNextSiteId() {
        return nextSiteId;
    }

    public void setNextSiteId(Integer nextSiteId) {
        this.nextSiteId = nextSiteId;
    }

    public String getNextSiteName() {
        return nextSiteName;
    }

    public void setNextSiteName(String nextSiteName) {
        this.nextSiteName = nextSiteName;
    }

    public Double getBookingWeight() {
        return bookingWeight;
    }

    public void setBookingWeight(Double bookingWeight) {
        this.bookingWeight = bookingWeight;
    }

    public Boolean getConfirmCreate() {
        return confirmCreate;
    }

    public void setConfirmCreate(Boolean confirmCreate) {
        this.confirmCreate = confirmCreate;
    }

    public Long getTakeOffTimeStamp() {
        return takeOffTimeStamp;
    }

    public void setTakeOffTimeStamp(Long takeOffTimeStamp) {
        this.takeOffTimeStamp = takeOffTimeStamp;
    }

    public Long getTouchDownTimeStamp() {
        return touchDownTimeStamp;
    }

    public void setTouchDownTimeStamp(Long touchDownTimeStamp) {
        this.touchDownTimeStamp = touchDownTimeStamp;
    }
}
