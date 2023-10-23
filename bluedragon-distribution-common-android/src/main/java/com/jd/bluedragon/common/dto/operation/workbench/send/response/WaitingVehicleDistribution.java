package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.util.List;

public class WaitingVehicleDistribution extends BaseSendVehicle{
    private static final long serialVersionUID = -4874713646811057276L;

    /**
     * 待派车明细
     */
    private List<WaitingVehicleDistributionDetail> flowList;

    public List<WaitingVehicleDistributionDetail> getFlowList() {
        return flowList;
    }

    public void setFlowList(List<WaitingVehicleDistributionDetail> flowList) {
        this.flowList = flowList;
    }
}
