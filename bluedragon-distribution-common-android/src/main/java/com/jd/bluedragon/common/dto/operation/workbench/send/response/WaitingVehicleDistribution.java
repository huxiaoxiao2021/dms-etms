package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.util.Date;
import java.util.List;

public class WaitingVehicleDistribution extends BaseSendVehicle{
    private static final long serialVersionUID = -4874713646811057276L;

    /**
     * 流向明细
     */
    private List<WaitingVehicleDistributionDetail> flowList;
}
