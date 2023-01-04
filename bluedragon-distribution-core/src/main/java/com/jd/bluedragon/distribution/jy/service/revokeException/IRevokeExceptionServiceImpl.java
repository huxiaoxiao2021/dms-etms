package com.jd.bluedragon.distribution.jy.service.revokeException;

import com.jd.bluedragon.common.dto.revokeException.request.QueryExceptionReq;
import com.jd.bluedragon.common.dto.revokeException.request.RevokeExceptionReq;
import com.jd.bluedragon.common.dto.revokeException.response.ExceptionReportResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.manager.RevokeExceptionManager;
import com.jd.tms.dtp.dto.AccountDto;
import com.jd.tms.dtp.dto.PageDto;
import com.jd.tms.dtp.dto.TransAbnormalBillDetailDto;
import com.jd.tms.dtp.dto.TransAbnormalBillQueryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.SERVER_ERROR_CODE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.SERVER_ERROR_MESSAGE;

/**
 * @author liwenji
 * @date 2023-01-04 16:16
 */
@Service
public class IRevokeExceptionServiceImpl implements IRevokeExceptionService {
    
    @Autowired
    private RevokeExceptionManager revokeExceptionManager;
    
    @Override
    public InvokeResult<ExceptionReportResp> queryAbnormalPage(QueryExceptionReq request) {
        InvokeResult<ExceptionReportResp> invokeResult = new InvokeResult<>(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE)
        AccountDto accountDto = getAccountDto(request);
        TransAbnormalBillQueryDto transAbnormalBillQueryDto = getTransAbnormalBillQueryDto(request);
        PageDto<TransAbnormalBillDetailDto> pageDto = getPageDto(request);
        revokeExceptionManager.queryAbnormalPage(accountDto, transAbnormalBillQueryDto, pageDto);
    }

    @Override
    public InvokeResult<String> closeTransAbnormal(RevokeExceptionReq revokeExceptionReq) {
        return null;
    }
}
