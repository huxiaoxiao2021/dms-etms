package com.jd.bluedragon.core.base;

import com.jd.ipc.csa.model.AllotRequestDetail;
import com.jd.ipc.csa.model.AllotResponseDetail;
import com.jd.ipc.csa.model.AllotScenarioEnum;

import java.util.List;

public interface GeneralStockAllotOutInterfaceManager {

    List<AllotResponseDetail> batchAllotStock(List<AllotRequestDetail> allotRequestDetails, AllotScenarioEnum scenario, String bizUniqueKey, Boolean isIdempotent, String sysName);
}
