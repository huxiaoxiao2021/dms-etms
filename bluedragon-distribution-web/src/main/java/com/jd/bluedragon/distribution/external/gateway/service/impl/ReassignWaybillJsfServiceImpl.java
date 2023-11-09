package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.distribution.api.request.ReassignWaybillApprovalRecordQuery;
import com.jd.bluedragon.distribution.api.response.BaseStaffResponse;
import com.jd.bluedragon.distribution.api.response.ReassignOrder;
import com.jd.bluedragon.distribution.api.response.ReassignWaybillApprovalRecordResponse;
import com.jd.bluedragon.distribution.api.response.StationMatchResponse;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jsf.domain.ReassignWaybillReq;
import com.jd.bluedragon.distribution.jsf.domain.StationMatchRequest;
import com.jd.bluedragon.distribution.jsf.service.ReassignWaybillJsfService;
import com.jd.bluedragon.distribution.reassignWaybill.service.ReassignWaybillService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/10/30 20:25
 * @Description:
 */
@Service("reassignWaybillJsfService")
public class ReassignWaybillJsfServiceImpl implements ReassignWaybillJsfService {

    @Autowired
    private ReassignWaybillService reassignWaybillService;


    @Override
    public JdResult<ReassignOrder> getReassignOrderInfo(String packageCode, List<String> decryptFields) {
        return reassignWaybillService.getReassignOrderInfo(packageCode,decryptFields);
    }

    @Override
    public JdResult<StationMatchResponse> stationMatchByAddress(StationMatchRequest request) {
        return reassignWaybillService.stationMatchByAddress(request);
    }

    @Override
    public JdResult<List<BaseStaffResponse>> getSiteByCodeOrName(String siteCodeOrName) {
        return reassignWaybillService.getSiteByCodeOrName(siteCodeOrName);
    }

    @Override
    public JdResult<Boolean> executeReassignWaybill(ReassignWaybillReq req) {
        return reassignWaybillService.executeReassignWaybill(req);
    }

    @Override
    public JdResult<PageDto<ReassignWaybillApprovalRecordResponse>> getReassignWaybillRecordListByPage(ReassignWaybillApprovalRecordQuery query) {
        return reassignWaybillService.getReassignWaybillRecordListByPage(query);
    }

    @Override
    public JdResult<Integer> getReassignWaybillRecordCount(ReassignWaybillApprovalRecordQuery query) {
        return reassignWaybillService.getReassignWaybillRecordCount(query);
    }
}
