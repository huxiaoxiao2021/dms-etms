package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealCodeRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealTaskInfoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealCodeResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealTaskInfo;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealVehicleTaskResponse;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

import java.util.List;

/**
 * @ClassName JySealVehicleGatewayService
 * @Description 拣运到车岗网关服务
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
     * 拉取待解封车任务
     * @param request
     * @return
     */
    JdCResponse<SealVehicleTaskResponse> fetchUnSealTask(SealVehicleTaskRequest request);

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
     * 待解封车状态枚举
     * @return
     */
    JdCResponse<List<SelectOption>> unSealVehicleStatusOptions();

    /**
     * 封签号列表
     * @param request
     * @return
     */
    JdCResponse<SealCodeResponse> sealCodeList(SealCodeRequest request);

}
