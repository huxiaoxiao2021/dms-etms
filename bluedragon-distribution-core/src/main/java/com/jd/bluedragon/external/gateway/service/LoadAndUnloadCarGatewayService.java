package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.unloadCar.*;

import java.util.List;

/**
 * 装卸车服务
 *  发布到物流网关 由安卓调用
 *
 * @author: hujiping
 * @date: 2020/6/24 17:14
 */
public interface LoadAndUnloadCarGatewayService {

    JdCResponse<UnloadCarScanResult> getUnloadCar(String sealCarCode);

    JdCResponse<UnloadCarScanResult> getUnloadScan(UnloadCarScanRequest unloadCarScanRequest);

    JdCResponse<UnloadCarScanResult> barCodeScan(UnloadCarScanRequest unloadCarScanRequest);

    JdVerifyResponse<UnloadCarScanResult> packageCodeScan(UnloadCarScanRequest unloadCarScanRequest);

    JdCResponse<UnloadCarScanResult> waybillScan(UnloadCarScanRequest unloadCarScanRequest);

    JdCResponse<List<UnloadCarDetailScanResult>> getUnloadCarDetail(String sealCarCode);

    JdCResponse<List<UnloadCarTaskDto>> getUnloadCarTask(UnloadCarTaskReq unloadCarTaskReq);

    JdCResponse<List<UnloadCarTaskDto>> updateUnloadCarTaskStatus(UnloadCarTaskReq unloadCarTaskReq);

    JdCResponse<List<HelperDto>> getUnloadCarTaskHelpers(String taskCode);

    JdCResponse<List<HelperDto>> updateUnloadCarTaskHelpers(TaskHelpersReq taskHelpersReq);

    JdCResponse<List<UnloadCarTaskDto>> getUnloadCarTaskScan(TaskHelpersReq taskHelpersReq);

    JdCResponse<Void> startUnloadTask(UnloadCarTaskReq unloadCarTaskReq);

    JdCResponse<UnloadScanDetailDto>  unloadScan(UnloadCarScanRequest  req);

    JdVerifyResponse<UnloadScanDetailDto> packageCodeScanNew(UnloadCarScanRequest  req);

    JdCResponse<UnloadScanDetailDto> waybillScanNew(UnloadCarScanRequest unloadCarScanRequest);


    JdCResponse<String> createUnloadTask(CreateUnloadTaskReq req);

    JdCResponse<Void> combinationBoardComplete(UnloadCarScanRequest request);

    /**
     * 获取erp下的历史添加协助人信息
     * @param erp
     * @return
     */
    JdCResponse<List<String>> getUnloadCarHistoryHelper(String erp);

    /**
     * 获取卸车任务时效
     * @param unloadCarTaskReq
     * @return
     */
    JdCResponse<UnloadCarTaskDto> getUnloadCarTaskDuration(UnloadCarTaskReq unloadCarTaskReq);

}
