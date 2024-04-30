package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.operation.workbench.send.request.DriverViolationReportingAddRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.DriverViolationReportingRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.DriverViolationReportingDto;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.external.domain.DriverViolationReportingResponse;
import com.jd.bluedragon.distribution.external.domain.QueryDriverViolationReportingReq;

/**
 * @author pengchong28
 * @description 司机违规举报服务接口
 * @date 2024/4/12
 */
public interface IJyDriverViolationReportingService {
    /***
     * 根据bizId查询已封车任务是否可以进行举报
     * @param request
     * @return
     */
    InvokeResult<DriverViolationReportingDto> checkViolationReporting(DriverViolationReportingRequest request);
    /***
     * 提交司机违规举报数据
     * @param request
     * @return
     */
    InvokeResult<Void> submitViolationReporting(DriverViolationReportingAddRequest request);
    /***
     * 根据bizId查询司机违规举报详情
     * @param request
     * @return
     */
    InvokeResult<DriverViolationReportingResponse> queryViolationReporting(QueryDriverViolationReportingReq request);
}
