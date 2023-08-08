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
    private String BookingType;

    private Integer airportCode;

    public String getBookingType() {
        return BookingType;
    }

    public void setBookingType(String bookingType) {
        BookingType = bookingType;
    }

    public Integer getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(Integer airportCode) {
        this.airportCode = airportCode;
    }
}
