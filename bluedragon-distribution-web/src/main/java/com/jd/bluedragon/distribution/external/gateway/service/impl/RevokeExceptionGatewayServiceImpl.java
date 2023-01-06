package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.revokeException.request.QueryExceptionReq;
import com.jd.bluedragon.common.dto.revokeException.request.RevokeExceptionReq;
import com.jd.bluedragon.common.dto.revokeException.response.ExceptionReportResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.service.revokeException.IRevokeExceptionService;
import com.jd.bluedragon.external.gateway.service.RevokeExceptionGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liwenji
 * @date 2023-01-04 16:12
 */
@Service
public class RevokeExceptionGatewayServiceImpl implements RevokeExceptionGatewayService {
    
    @Autowired
    private IRevokeExceptionService revokeExceptionService;
    
    @Override
    public JdCResponse<List<ExceptionReportResp>> queryAbnormalPage(QueryExceptionReq query) {
        return retJdCResponse(revokeExceptionService.queryAbnormalPage(query));
    }   

    @Override
    public JdCResponse<String> closeTransAbnormal(RevokeExceptionReq revokeExceptionReq) {
        return retJdCResponse(revokeExceptionService.closeTransAbnormal(revokeExceptionReq));
    }

    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(),
                invokeResult.getData());
    }
}
