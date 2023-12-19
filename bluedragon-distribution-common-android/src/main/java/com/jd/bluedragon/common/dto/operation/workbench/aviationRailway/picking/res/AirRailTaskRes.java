package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res;

import java.io.Serializable;
import java.util.List;

public class AirRailTaskRes implements Serializable {

    private static final long serialVersionUID = 3456943500574756656L;
    /**
     * 各状态下的机场/车站任务数量
     */
    private List<AirRailTaskCountDto> countDtoList;
    /**
     * 当前所选状态下的机场/车站任务列表
     */
    private List<AirRailDto> airRailDtoList;

    public List<AirRailTaskCountDto> getCountDtoList() {
        return countDtoList;
    }

    public void setCountDtoList(List<AirRailTaskCountDto> countDtoList) {
        this.countDtoList = countDtoList;
    }

    public List<AirRailDto> getAirRailDtoList() {
        return airRailDtoList;
    }

    public void setAirRailDtoList(List<AirRailDto> airRailDtoList) {
        this.airRailDtoList = airRailDtoList;
    }
}
