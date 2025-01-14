package com.jd.bluedragon.distribution.print.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.ColdChainReverseManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jsf.waybill.WaybillReverseManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.message.OwnReverseTransferDomain;
import com.jd.bluedragon.distribution.print.domain.ExchangeWaybillRequest;
import com.jd.bluedragon.distribution.print.service.WaybillInterceptReverseService;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillReverseDTO;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillReverseResponseDTO;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillReverseResult;
import com.jd.bluedragon.distribution.reverse.domain.ExchangeWaybillDto;
import com.jd.bluedragon.distribution.reverse.service.ReversePrintService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.coldchain.fulfillment.ot.api.dto.waybill.ColdChainReverseRequest;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("waybillInterceptReverseService")
public class WaybillInterceptReverseServiceImpl implements WaybillInterceptReverseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaybillInterceptReverseServiceImpl.class);

    private static final String ERROR_MESSAGE = "换单前获取外单信息接口失败 该运单已换单完成";

    private static final String BAI_CHUAN_ERROR_MESSAGE = "该运单已换单完成!";

    @Autowired
    private ReversePrintService reversePrintService;
    @Autowired
    private WaybillQueryManager waybillQueryManager;
    @Autowired
    private WaybillReverseManager waybillReverseManager;
    @Autowired
    private ColdChainReverseManager coldChainReverseManager;
    @Autowired
    private WaybillService waybillService;

    @Override
    @JProfiler(jKey = "DMSWEB.WaybillInterceptReverseServiceImpl.exchangeNewWaybill",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<String> exchangeNewWaybill(ExchangeWaybillRequest request) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("exchangeNewWaybill|德邦拦截换单请求参数:request={}", JsonHelper.toJson(request));
        }
        InvokeResult<String> invokeResult = new InvokeResult<>();
        // 原单号
        String oldWaybillCode = request.getWaybillCode();
        // 操作场地
        int operateSiteId = request.getCurrentOperate().getSiteCode();
        try {
            Waybill waybill = waybillQueryManager.queryWaybillByWaybillCode(oldWaybillCode);
            if (waybill == null) {
                invokeResult.parameterError("根据原单号查询运单详情返回空");
                return invokeResult;
            }
            // 换单前校验
            InvokeResult<Boolean> beforeCheckResult = reversePrintService.checkWayBillForDpkExchange(oldWaybillCode, operateSiteId);
            if (!beforeCheckResult.getData()) {
                LOGGER.warn("exchangeNewWaybill|换单前校验返回失败:request={},msg={}", JsonHelper.toJson(request), beforeCheckResult.getMessage());
                invokeResult.parameterError(beforeCheckResult.getMessage());
                return invokeResult;
            }
            // 如果是自营
            final String waybillSign = waybill.getWaybillSign();
            if (WaybillUtil.isJDWaybillCode(oldWaybillCode) || BusinessUtil.isSelfReverse(waybillSign)) {
                LOGGER.info("exchangeNewWaybill|自营换单:waybillCode={}", oldWaybillCode);
                // 执行自营逆向换单提交
                OwnReverseTransferDomain ownReverseParam = createOwnReverseTransferDomain(request);
                InvokeResult<String> ownExchangeResult = reversePrintService.exchangeOwnWaybillSync(ownReverseParam);
                if (!ownExchangeResult.codeSuccess()) {
                    LOGGER.warn("exchangeNewWaybill|自营换单提交操作返回失败:request={},ownExchangeResult={}", JsonHelper.toJson(request), JsonHelper.toJson(ownExchangeResult));
                    invokeResult.parameterError(ownExchangeResult.getMessage());
                    return invokeResult;
                }
                invokeResult.setData(ownExchangeResult.getData());
            } else {
                LOGGER.info("exchangeNewWaybill|外单换单:waybillCode={}", oldWaybillCode);
                // 如果是外单
                ExchangeWaybillDto exchangeWaybillDto = createExchangeWaybillDto(request);
                exchangeWaybillDto.setPackageCount(waybill.getGoodNumber());
                // 设置逆向原因编码
                Integer reverseReasonCode = waybillService.queryReverseReasonCode(oldWaybillCode);
                exchangeWaybillDto.setReverseReasonCode(reverseReasonCode);
                // 组装外单原单查询参数
                DmsWaybillReverseDTO waybillReverseDTO = waybillReverseManager.makeWaybillReverseDTOCanTwiceExchange(exchangeWaybillDto);
                StringBuilder errorMessage = new StringBuilder();
                // 查询外单原单信息
                DmsWaybillReverseResponseDTO waybillReverseResponseDTO = waybillReverseManager.queryReverseWaybill(waybillReverseDTO, errorMessage);
                if (waybillReverseResponseDTO == null) {
                    LOGGER.warn("exchangeNewWaybill|外单查询原单号信息返回失败:request={},waybillReverseResponseDTO为空,errorMessage={}", JsonHelper.toJson(request), errorMessage);
                    // 如果是重复换单，返回之前的新单号
                    if (ERROR_MESSAGE.contentEquals(errorMessage) || BAI_CHUAN_ERROR_MESSAGE.contentEquals(errorMessage)) {
                        invokeResult.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                        invokeResult.setData(waybill.getReturnWaybillCode());
                        return invokeResult;
                    }
                    invokeResult.parameterError(errorMessage.toString());
                    return invokeResult;
                }
                DmsWaybillReverseResult waybillReverseResult;
                if (coldChainReverseManager.checkColdReverseProductType(oldWaybillCode)) {
                    LOGGER.info("exchangeNewWaybill|外单走冷链换单流程:waybillCode={}", oldWaybillCode);
                    ColdChainReverseRequest coldChainReverseRequest = coldChainReverseManager.makeColdChainReverseRequest(exchangeWaybillDto);
                    waybillReverseResult = coldChainReverseManager.createReverseWbOrder(coldChainReverseRequest, errorMessage);
                } else {
                    LOGGER.info("exchangeNewWaybill|外单走原有流程:waybillCode={}", oldWaybillCode);
                    waybillReverseResult = waybillReverseManager.waybillReverse(waybillReverseDTO, errorMessage);
                }
                if (waybillReverseResult == null) {
                    LOGGER.warn("exchangeNewWaybill|外单根据原单号查询新单号信息返回失败:request={},waybillReverseResult为空", JsonHelper.toJson(request));
                    invokeResult.parameterError(errorMessage.toString());
                    return invokeResult;
                }
                // 外单换单成功后处理
                reversePrintService.exchangeSuccessAfter(exchangeWaybillDto);
                invokeResult.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                invokeResult.setData(waybillReverseResult.getWaybillCode());
                return invokeResult;
            }
        } catch (Exception e) {
            LOGGER.error("exchangeNewWaybill|德邦调用换单接口出现异常:request={}", JsonHelper.toJson(request), e);
            invokeResult.customMessage(InvokeResult.SERVER_ERROR_CODE, InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return invokeResult;
    }

    private OwnReverseTransferDomain createOwnReverseTransferDomain(ExchangeWaybillRequest request) {
        OwnReverseTransferDomain transferDomain = new OwnReverseTransferDomain();
        transferDomain.setUserId(request.getUser().getUserCode());
        transferDomain.setUserRealName(request.getUser().getUserName());
        transferDomain.setSiteId(request.getCurrentOperate().getSiteCode());
        transferDomain.setSiteName(request.getCurrentOperate().getSiteName());
        transferDomain.setWaybillCode(request.getWaybillCode());
        return transferDomain;
    }

    private ExchangeWaybillDto createExchangeWaybillDto(ExchangeWaybillRequest request) {
        ExchangeWaybillDto exchangeWaybillDto = new ExchangeWaybillDto();
        exchangeWaybillDto.setWaybillCode(request.getWaybillCode());
        exchangeWaybillDto.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
        exchangeWaybillDto.setOperatorId(request.getUser().getUserCode());
        exchangeWaybillDto.setOperatorName(request.getUser().getUserName());
        exchangeWaybillDto.setOperateTime(DateHelper.formatDateTime(request.getCurrentOperate().getOperateTime()));
        exchangeWaybillDto.setisTotalout(true);
        return exchangeWaybillDto;
    }


}
