package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.send.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

import java.util.List;

public interface JyWarehouseSendVehicleService {
    InvokeResult<MixScanTaskDetailRes> getMixScanTaskDetailList(MixScanTaskQueryReq request);

    /**
     * 查询待发和发货中的任务数据
     * @param request
     * @return
     */
    InvokeResult<AppendSendVehicleTaskQueryRes> fetchToSendAndSendingTaskPage(AppendSendVehicleTaskQueryReq request);

    /**
     * 获取场地混扫流向限制
     * @param siteCode
     * @return
     */
    Integer getFlowMaxBySiteCode(Integer siteCode);

    JdVerifyResponse<SendScanRes> scan(SendScanReq request, JdVerifyResponse<SendScanRes> response);

    /**
     * 查询下一流向
     * 按箱查询时，返回是箱号下一流向
     * 按包裹或运单查询时，返回get(0)是预分拣站点，其他为流向
     * @param request
     * @return
     */
    InvokeResult<List<Integer>> fetchNextSiteId(SendScanReq request);

    /**
     * 查询不齐运单
     * @param request
     * @return
     */
    InvokeResult<BuQiWaybillRes> findByQiWaybillPage(BuQiWaybillReq request);
}
