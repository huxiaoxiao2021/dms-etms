package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res;

import java.io.Serializable;
import java.util.List;

public class AirRailTaskAggRes implements Serializable {
    private static final long serialVersionUID = 6553423390626409410L;

    private List<AirRailTaskAggDto> taskAggDtoList;

    /**
     * 当前场地总数待提数量
     */
    private Integer currentSiteWaitScan = 0;
    /**
     * 当前场地已提总数
     */
    private Integer currentSiteHaveScanned = 0;
    /**
     * 当前场地多提总数
     */
    private Integer currentSiteMultipleScan = 0;

    public List<AirRailTaskAggDto> getTaskAggDtoList() {
        return taskAggDtoList;
    }

    public void setTaskAggDtoList(List<AirRailTaskAggDto> taskAggDtoList) {
        this.taskAggDtoList = taskAggDtoList;
    }

    public Integer getCurrentSiteWaitScan() {
        return currentSiteWaitScan;
    }

    public void setCurrentSiteWaitScan(Integer currentSiteWaitScan) {
        this.currentSiteWaitScan = currentSiteWaitScan;
    }

    public Integer getCurrentSiteHaveScanned() {
        return currentSiteHaveScanned;
    }

    public void setCurrentSiteHaveScanned(Integer currentSiteHaveScanned) {
        this.currentSiteHaveScanned = currentSiteHaveScanned;
    }

    public Integer getCurrentSiteMultipleScan() {
        return currentSiteMultipleScan;
    }

    public void setCurrentSiteMultipleScan(Integer currentSiteMultipleScan) {
        this.currentSiteMultipleScan = currentSiteMultipleScan;
    }
}
