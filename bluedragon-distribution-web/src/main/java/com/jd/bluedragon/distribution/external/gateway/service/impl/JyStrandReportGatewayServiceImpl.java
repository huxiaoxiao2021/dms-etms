package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizStrandScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizStrandTaskStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.strand.*;
import com.jd.bluedragon.common.dto.strandreport.request.ConfigStrandReasonData;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.service.strand.JyBizTaskStrandReportDealService;
import com.jd.bluedragon.external.gateway.service.JyStrandReportGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 拣运app-滞留上报网关接口实现
 *
 * @author hujiping
 * @date 2023/3/28 3:18 PM
 */
@Service("jyStrandReportGatewayService")
public class JyStrandReportGatewayServiceImpl implements JyStrandReportGatewayService {
    
    @Autowired
    private JyBizTaskStrandReportDealService jyBizTaskStrandReportDealService;

    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyStrandReportGatewayService.artificialCreateStrandReportTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public JdCResponse<JyStrandReportTaskVO> artificialCreateStrandReportTask(JyStrandReportTaskCreateReq request) {
        return retJdCResponse(jyBizTaskStrandReportDealService.artificialCreateStrandReportTask(request));
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyStrandReportGatewayService.cancelStrandReportTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public JdCResponse<Void> cancelStrandReportTask(JyStrandReportTaskCreateReq request) {
        return retJdCResponse(jyBizTaskStrandReportDealService.cancelStrandReportTask(request));
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyStrandReportGatewayService.queryStrandReason",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public JdCResponse<List<ConfigStrandReasonData>> queryStrandReason() {
        return retJdCResponse(jyBizTaskStrandReportDealService.queryStrandReason());
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyStrandReportGatewayService.queryStrandScanType",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public JdCResponse<List<JyBizStrandScanTypeEnum>> queryStrandScanType() {
        return retJdCResponse(jyBizTaskStrandReportDealService.queryStrandScanType());
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyStrandReportGatewayService.strandScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public JdCResponse<JyStrandReportScanResp> strandScan(JyStrandReportScanReq scanRequest) {
        return retJdCResponse(jyBizTaskStrandReportDealService.strandScan(scanRequest));
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyStrandReportGatewayService.cancelStrandScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public JdCResponse<JyStrandReportScanResp> cancelStrandScan(JyStrandReportScanReq request) {
        return retJdCResponse(jyBizTaskStrandReportDealService.cancelStrandScan(request));
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyStrandReportGatewayService.strandReportSubmit",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public JdCResponse<Void> strandReportSubmit(JyStrandReportScanReq scanRequest) {
        scanRequest.setTaskStatus(JyBizStrandTaskStatusEnum.COMPLETE.getCode());
        return retJdCResponse(jyBizTaskStrandReportDealService.strandReportSubmit(scanRequest));
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyStrandReportGatewayService.queryStrandReportTaskPageList",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public JdCResponse<JyStrandReportTaskPageResp> queryStrandReportTaskPageList(JyStrandReportTaskPageReq pageReq) {
        return retJdCResponse(jyBizTaskStrandReportDealService.queryStrandReportTaskPageList(pageReq));
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyStrandReportGatewayService.queryPageStrandReportTaskDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public JdCResponse<List<JyStrandReportScanVO>> queryPageStrandReportTaskDetail(JyStrandReportScanPageReq detailPageReq) {
        return retJdCResponse(jyBizTaskStrandReportDealService.queryPageStrandReportTaskDetail(detailPageReq));
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyStrandReportGatewayService.queryStrandReportTaskDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public JdCResponse<JyStrandReportTaskDetailVO> queryStrandReportTaskDetail(String bizId) {
        return retJdCResponse(jyBizTaskStrandReportDealService.queryStrandReportTaskDetail(bizId));
    }
}
