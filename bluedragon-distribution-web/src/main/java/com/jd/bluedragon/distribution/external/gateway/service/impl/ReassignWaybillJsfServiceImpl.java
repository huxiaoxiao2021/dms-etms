package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.distribution.api.response.ReassignWaybillApprovalRecordResponse;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jsf.domain.ReassignWaybillReq;
import com.jd.bluedragon.distribution.jsf.service.ReassignWaybillJsfService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/10/30 20:25
 * @Description:
 */
public class ReassignWaybillJsfServiceImpl implements ReassignWaybillJsfService {


    @Override
    public JdResult<Boolean> executeReassignWaybill(ReassignWaybillReq req) {
        return null;
    }

    @Override
    public JdResult<PageDto<ReassignWaybillApprovalRecordResponse>> getReassignWaybillRecordListByPage() {
        return null;
    }



}
