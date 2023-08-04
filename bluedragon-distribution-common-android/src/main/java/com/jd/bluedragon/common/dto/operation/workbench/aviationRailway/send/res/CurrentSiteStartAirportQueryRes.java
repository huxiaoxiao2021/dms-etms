package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/4 15:32
 * @Description
 */
public class CurrentSiteStartAirportQueryRes implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    private List<AirportDataDto> airportDataDtoList;


    public List<AirportDataDto> getAirportInfoDtoList() {
        return airportDataDtoList;
    }

    public void setAirportInfoDtoList(List<AirportDataDto> airportDataDtoList) {
        this.airportDataDtoList = airportDataDtoList;
    }
}
