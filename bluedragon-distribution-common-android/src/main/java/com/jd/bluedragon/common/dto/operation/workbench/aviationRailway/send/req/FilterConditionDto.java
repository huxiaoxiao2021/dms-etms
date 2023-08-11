package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/8 15:15
 * @Description
 */
public class FilterConditionDto implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    /**
     * 订舱类型
     */
    private Integer bookingType;

    private String airportCode;

    public Integer getBookingType() {
        return bookingType;
    }

    public void setBookingType(Integer bookingType) {
        this.bookingType = bookingType;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }
}
