package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/4 16:15
 * @Description
 */
public class TransportInfoQueryRes implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    private String careTransportCode;

    private String careDepartureTime;

    private List<TransportDataDto> transportDataDtoList;

    public String getCareTransportCode() {
        return careTransportCode;
    }

    public void setCareTransportCode(String careTransportCode) {
        this.careTransportCode = careTransportCode;
    }

    public String getCareDepartureTime() {
        return careDepartureTime;
    }

    public void setCareDepartureTime(String careDepartureTime) {
        this.careDepartureTime = careDepartureTime;
    }

    public List<TransportDataDto> getTransportInfoDtoList() {
        return transportDataDtoList;
    }

    public void setTransportInfoDtoList(List<TransportDataDto> transportDataDtoList) {
        this.transportDataDtoList = transportDataDtoList;
    }
}
