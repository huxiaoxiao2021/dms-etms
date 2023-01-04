package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.revokeException.request.QueryExceptionReq;
import com.jd.bluedragon.common.dto.revokeException.request.RevokeExceptionReq;
import com.jd.bluedragon.common.dto.revokeException.response.ExceptionReportResp;

/**
 * @author liwenji
 * 撤销异常封签 发布物流网关 由安卓调用
 * @date 2023-01-04 10:51
 */
public interface RevokeExceptionGatewayService {

    /**
     * 查询当前场地封签异常信息
     */
    JdCResponse<ExceptionReportResp> closeTransAbnormal(QueryExceptionReq query);
    
    /**
     * 撤销封签异常接口
     */
    JdCResponse<String> closeTransAbnormal(RevokeExceptionReq revokeExceptionReq);

}
