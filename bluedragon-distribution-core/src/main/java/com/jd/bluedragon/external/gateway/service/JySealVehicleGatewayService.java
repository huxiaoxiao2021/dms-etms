package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadCompleteRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadProductTypeRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadScanRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadVehicleRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealCodeRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealTaskInfoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealCodeResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealTaskInfo;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealVehicleTaskResponse;
import com.jd.bluedragon.common.dto.select.SelectOption;

import java.util.List;
import java.util.Map;

/**
 * @ClassName JySealVehicleGatewayService
 * @Description
 * @Author wyh
 * @Date 2022/3/9 17:19
 **/
public interface JySealVehicleGatewayService {

    /**
     * 封车任务列表
     * @param request
     * @return
     */
    JdCResponse<SealVehicleTaskResponse> fetchSealTask(SealVehicleTaskRequest request);

    /**
     * 封车任务明细
     * @param request
     * @return
     */
    JdCResponse<SealTaskInfo> taskInfo(SealTaskInfoRequest request);

    /**
     * 车辆状态枚举
     * @return
     */
    JdCResponse<List<SelectOption>> vehicleStatusOptions();

    /**
     * 封签号列表
     * @param request
     * @return
     */
    JdCResponse<SealCodeResponse> sealCodeList(SealCodeRequest request);

}
