package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/4 15:32
 * @Description
 */
public class FilterConditionQueryRes implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    /**
     * 订舱类型统计
     */
    private List<BookingTypeDto> bookingTypeDtoList;
    /**
     * 当前场地始发机场
     */
    private List<AirportDataDto> airportDataDtoList;


    public List<BookingTypeDto> getBookingTypeDtoList() {
        return bookingTypeDtoList;
    }

    public void setBookingTypeDtoList(List<BookingTypeDto> bookingTypeDtoList) {
        this.bookingTypeDtoList = bookingTypeDtoList;
    }

    public List<AirportDataDto> getAirportDataDtoList() {
        return airportDataDtoList;
    }

    public void setAirportDataDtoList(List<AirportDataDto> airportDataDtoList) {
        this.airportDataDtoList = airportDataDtoList;
    }
}
