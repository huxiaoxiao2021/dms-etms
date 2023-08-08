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

    private List<TransportDataDto> transportDataDtoList;

    public List<TransportDataDto> getTransportInfoDtoList() {
        return transportDataDtoList;
    }

    public void setTransportInfoDtoList(List<TransportDataDto> transportDataDtoList) {
        this.transportDataDtoList = transportDataDtoList;
    }
}
