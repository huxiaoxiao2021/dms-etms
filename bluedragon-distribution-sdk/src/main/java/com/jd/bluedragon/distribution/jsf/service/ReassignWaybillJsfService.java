package com.jd.bluedragon.distribution.jsf.service;

import com.jd.bluedragon.distribution.api.response.ReassignWaybillApprovalRecordResponse;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jsf.domain.ReassignWaybillReq;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/10/30 18:13
 * @Description: 返调度 JSF
 */
public interface ReassignWaybillJsfService {


    /**
     * 执行返调度
     */
    JdResult<Boolean> executeReassignWaybill(ReassignWaybillReq req);

    /**
     * 获取返调度审批记录
     * @return
     */
    JdResult<PageDto<ReassignWaybillApprovalRecordResponse>> getReassignWaybillRecordListByPage();
}
