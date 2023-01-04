package com.jd.bluedragon.distribution.jy.service.revokeException;

import com.jd.bluedragon.common.dto.revokeException.request.QueryExceptionReq;
import com.jd.bluedragon.common.dto.revokeException.request.RevokeExceptionReq;
import com.jd.bluedragon.common.dto.revokeException.response.ExceptionReportResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

/**
 * @author liwenji
 * @date 2023-01-04 16:15
 */
public interface IRevokeExceptionService {

    /**
     * 查询异常提报
     * @param query
     * @return
     */
    InvokeResult<ExceptionReportResp> queryAbnormalPage(QueryExceptionReq query);

    /**
     * 撤销异常提报
     * @param revokeExceptionReq
     * @return
     */
    InvokeResult<String> closeTransAbnormal(RevokeExceptionReq revokeExceptionReq);
}
