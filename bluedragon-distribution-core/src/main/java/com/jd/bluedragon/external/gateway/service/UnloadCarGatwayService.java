package com.jd.bluedragon.external.gateway.service;


import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.unloadCar.HelperDto;
import com.jd.bluedragon.common.dto.unloadCar.TaskHelpersReq;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarTaskDto;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarTaskReq;

import java.util.List;

/**
 * @author lijie
 * @date 2020/6/28 13:55
 */
public interface UnloadCarGatwayService {

    JdCResponse<List<UnloadCarTaskDto>> getUnloadCarTask(UnloadCarTaskReq unloadCarTaskReq);

    JdCResponse<Boolean> updateUnloadCarTaskStatus(UnloadCarTaskReq unloadCarTaskReq);

    JdCResponse<List<HelperDto>> getUnloadCarTaskHelpers(String taskCode);

    JdCResponse<List<HelperDto>> updateUnloadCarTaskHelpers(TaskHelpersReq taskHelpersReq);

    JdCResponse<List<UnloadCarTaskDto>> getUnloadCarTaskScan(TaskHelpersReq taskHelpersReq);

}
