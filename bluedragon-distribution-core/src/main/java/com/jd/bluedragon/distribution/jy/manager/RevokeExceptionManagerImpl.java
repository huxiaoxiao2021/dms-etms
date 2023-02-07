package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.tms.dtp.api.DtpTransAbnormalSelectApi;
import com.jd.tms.dtp.api.DtpTransAbnormalApi;
import com.jd.tms.dtp.dto.*;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.jd.bluedragon.distribution.api.Response.CODE_SUCCESS;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;

/**
 * @author liwenji
 * @date 2023-01-04 16:39
 */
@Service
@Slf4j
public class RevokeExceptionManagerImpl implements RevokeExceptionManager {

    @Autowired
    private DtpTransAbnormalSelectApi transAbnormalSelectApi;

    @Autowired
    private DtpTransAbnormalApi dtpTransAbnormalApi;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.RevokeExceptionManagerImpl.queryAbnormalPage", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<PageDto<TransAbnormalBillDetailDto>> queryAbnormalPage(AccountDto accountDto, TransAbnormalBillQueryDto transAbnormalBillQueryDto, PageDto<TransAbnormalBillDetailDto> pageDto) {

        InvokeResult<PageDto<TransAbnormalBillDetailDto>> invokeResult = new InvokeResult<>();

        if (log.isInfoEnabled()) {
            log.info("开始查询封签异常提报：{} {} {}", JsonHelper.toJson(accountDto), JsonHelper.toJson(transAbnormalBillQueryDto), JsonHelper.toJson(pageDto));
        }

        try {
            CommonDto<PageDto<TransAbnormalBillDetailDto>> commonDto = transAbnormalSelectApi.querySealCodeAbnormalPage(accountDto, transAbnormalBillQueryDto, pageDto);

            if (commonDto == null) {
                if (log.isErrorEnabled()) {
                    log.error("封签异常提报查询无返回数据：{} {} {}", JsonHelper.toJson(accountDto), JsonHelper.toJson(transAbnormalBillQueryDto), JsonHelper.toJson(pageDto));
                }
                invokeResult.setCode(QUERY_EXCEPTION_REPORT_CODE);
                invokeResult.setMessage(QUERY_EXCEPTION_REPORT_MESSAGE);
            }

            if (commonDto != null && CommonDto.CODE_SUCCESS == commonDto.getCode()) {
                invokeResult.setCode(RESULT_SUCCESS_CODE);
                invokeResult.setMessage(RESULT_SUCCESS_MESSAGE);
                invokeResult.setData(commonDto.getData());
                if (log.isInfoEnabled()) {
                    log.info("查询封签异常提报成功,结果为：{}", JsonHelper.toJson(commonDto));
                }
            } else {
                invokeResult.setCode(QUERY_EXCEPTION_REPORT_CODE);
                invokeResult.setMessage(commonDto.getMessage());
                log.error("查询封签异常提报失败,请求:{} {} {} 结果为：{}", JsonHelper.toJson(accountDto), JsonHelper.toJson(transAbnormalBillQueryDto), JsonHelper.toJson(pageDto), JsonHelper.toJson(commonDto));
            }

        } catch (Exception e) {
            log.error("封签异常提报查询异常：{} {} {}", JsonHelper.toJson(accountDto), JsonHelper.toJson(transAbnormalBillQueryDto), JsonHelper.toJson(pageDto), e);
            invokeResult.setCode(QUERY_EXCEPTION_REPORT_CODE);
            invokeResult.setMessage(QUERY_EXCEPTION_REPORT_MESSAGE);
        }
        return invokeResult;
    }


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.RevokeExceptionManagerImpl.closeTransAbnormal", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<String> closeTransAbnormal(AccountDto accountDto, TransAbnormalExtendDto transAbnormalExtendDto) {
        InvokeResult<String> invokeResult = new InvokeResult<>();

        if (log.isInfoEnabled()) {
            log.info("开始查询封签异常提报：{} {}", JsonHelper.toJson(accountDto), JsonHelper.toJson(transAbnormalExtendDto));
        }

        try {
            CommonDto<String> commonDto = dtpTransAbnormalApi.closeTransAbnormal(accountDto, transAbnormalExtendDto);

            if (commonDto == null) {
                log.error("撤销封签异常提报无返回数据：{} {}", JsonHelper.toJson(accountDto), JsonHelper.toJson(transAbnormalExtendDto));
                invokeResult.setCode(REVOKE_EXCEPTION_REPORT_CODE);
                invokeResult.setMessage(REVOKE_EXCEPTION_REPORT_MESSAGE);
            }

            if (commonDto != null && commonDto.getCode() == CommonDto.CODE_SUCCESS) {
                if (log.isInfoEnabled()) {
                    log.info("撤销封签异常提报成功：{} {} {}", JsonHelper.toJson(accountDto), JsonHelper.toJson(transAbnormalExtendDto), JsonHelper.toJson(commonDto));
                }
                invokeResult.setCode(RESULT_SUCCESS_CODE);
                invokeResult.setMessage(RESULT_SUCCESS_MESSAGE);
                invokeResult.setData(commonDto.getData());
            } else {
                invokeResult.setCode(REVOKE_EXCEPTION_REPORT_CODE);
                invokeResult.setMessage(commonDto.getMessage());
                log.error("撤销封签异常提报失败,请求：{} {} 结果为：{}", JsonHelper.toJson(accountDto), JsonHelper.toJson(transAbnormalExtendDto), JsonHelper.toJson(commonDto));
            }
        } catch (Exception e) {
            log.error("撤销封签异常提报异常：{} {}", JsonHelper.toJson(accountDto), JsonHelper.toJson(transAbnormalExtendDto), e);
            invokeResult.setCode(REVOKE_EXCEPTION_REPORT_CODE);
            invokeResult.setMessage(REVOKE_EXCEPTION_REPORT_MESSAGE);
        }

        return invokeResult;
    }

}
