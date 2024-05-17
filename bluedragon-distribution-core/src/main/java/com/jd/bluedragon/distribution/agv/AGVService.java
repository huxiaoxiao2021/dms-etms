package com.jd.bluedragon.distribution.agv;



import com.jd.bluedragon.common.dto.agv.AGVPDARequest;
import com.jd.bluedragon.distribution.sdk.common.domain.InvokeResult;
import com.jd.bluedragon.distribution.sdk.modules.agv.model.AGVRequest;

import java.util.List;

public interface AGVService {

    public InvokeResult<Boolean> sortByAGV(AGVPDARequest pdaRequest);
}
