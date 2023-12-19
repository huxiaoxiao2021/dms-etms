package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res;

import java.io.Serializable;
import java.util.List;

public class AirRailTaskAggRes implements Serializable {
    private static final long serialVersionUID = 6553423390626409410L;

    private List<AirRailTaskAggDto> taskAggDtoList;

    public List<AirRailTaskAggDto> getTaskAggDtoList() {
        return taskAggDtoList;
    }

    public void setTaskAggDtoList(List<AirRailTaskAggDto> taskAggDtoList) {
        this.taskAggDtoList = taskAggDtoList;
    }
}
