package com.jd.bluedragon.distribution.print.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.WaybillCommonService;
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
import com.jd.bluedragon.dms.utils.BusinessUtil;
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

    @Autowired
    private ReversePrintService reversePrintService;
    @Autowired
    private WaybillQueryManager waybillQueryManager;
    @Autowired
    private WaybillCommonService waybillCommonService;
    @Autowired
    private WaybillReverseManager waybillReverseManager;
    @Autowired
    private ColdChainReverseManager coldChainReverseManager;

    @Override
    @JProfiler(jKey = "DMSWEB.WaybillInterceptReverseServiceImpl.exchangeNewWaybill",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<String> exchangeNewWaybill(ExchangeWaybillRequest request) {
        InvokeResult<String> invokeResult = new InvokeResult<>();
        // 原单号
        String oldWaybillCode = request.getWaybillCode();
        // 操作场地
        int operateSiteId = request.getCurrentOperate().getSiteCode();
        Waybill waybill = waybillQueryManager.queryWaybillByWaybillCode(oldWaybillCode);
        if (waybill == null) {
            invokeResult.parameterError("根据原单号查询运单详情返回空");
            return invokeResult;
        }
        // 换单前校验
        InvokeResult<Boolean> beforeCheckResult = reversePrintService.checkWayBillForExchange(oldWaybillCode, operateSiteId);
        if (!beforeCheckResult.getData()) {
            LOGGER.warn("exchangeNewWaybill|换单前校验返回失败:request={},msg={}", JsonHelper.toJson(request), beforeCheckResult.getMessage());
            invokeResult.parameterError(beforeCheckResult.getMessage());
            return invokeResult;
        }
        String waybillSign = waybill.getWaybillSign();
        // 如果是自营
        if (BusinessUtil.isSelf(waybillSign)) {
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
            // 如果是外单
            ExchangeWaybillDto exchangeWaybillDto = createExchangeWaybillDto(request);
            // 组装外单原单查询参数
            DmsWaybillReverseDTO waybillReverseDTO = waybillReverseManager.makeWaybillReverseDTOCanTwiceExchange(exchangeWaybillDto);
            StringBuilder errorMessage = new StringBuilder();
            // 查询外单原单信息
            DmsWaybillReverseResponseDTO waybillReverseResponseDTO = waybillReverseManager.queryReverseWaybill(waybillReverseDTO, errorMessage);
            if (waybillReverseResponseDTO == null) {
                LOGGER.warn("exchangeNewWaybill|外单查询原单号信息返回失败:request={},waybillReverseResponseDTO为空", JsonHelper.toJson(request));
                invokeResult.parameterError(errorMessage.toString());
                return invokeResult;
            }
            exchangeWaybillDto.setPackageCount(waybillReverseResponseDTO.getPackageCount());
            DmsWaybillReverseResult waybillReverseResult;
            if (coldChainReverseManager.checkColdReverseProductType(oldWaybillCode)) {
                LOGGER.info("exchangeNewWaybill|外单走冷链换单流程:request={}", JsonHelper.toJson(request));
                ColdChainReverseRequest coldChainReverseRequest = coldChainReverseManager.makeColdChainReverseRequest(exchangeWaybillDto);
                waybillReverseResult = coldChainReverseManager.createReverseWbOrder(coldChainReverseRequest, errorMessage);
            } else {
                LOGGER.info("exchangeNewWaybill|外单走原有流程:request={}", JsonHelper.toJson(request));
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
        // todo exchangeWaybillDto.setisTotalout();
        return exchangeWaybillDto;
    }


}
