package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BoardCommonManager;
import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.api.request.TransportServiceRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.external.service.FuncSwitchConfigApiService;
import com.jd.bluedragon.distribution.external.service.TransportCommonService;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.util.StringUtils;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 转运依赖分拣相关服务
 */
@Slf4j
@Service("transportCommonService")
public class TransportCommonServiceImpl implements TransportCommonService {

    @Resource
    private BoardCommonManager boardCommonManager;

    @Resource
    private FuncSwitchConfigApiService funcSwitchConfigApiService;

    @Resource
    private WaybillService waybillService;

    @Override
    @JProfiler(jKey = "DMSWEB.TransportCommonServiceImpl.interceptValidateUnloadCar", jAppName = Constants.UMP_APP_NAME_DMSWEB , mState = {JProEnum.TP})
    public InvokeResult<Boolean> interceptValidateUnloadCar(TransportServiceRequest transportServiceRequest) {
        final String methodDesc = "TransportCommonServiceImpl.interceptValidateUnloadCar--装卸车通用校验begin--";
        InvokeResult<Boolean> result = new InvokeResult<>();
        if(transportServiceRequest == null) {
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("参数不能为空");
            return result;
        }
        if(StringUtils.isBlank(transportServiceRequest.getWaybillCode())) {
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("运单号不能为空");
            return result;
        }
        if(StringUtils.isBlank(transportServiceRequest.getWaybillSign())) {
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("运单waybillSign不能为空");
            return result;
        }

        try {
            log.info(methodDesc + "请求参数=【{}】", JsonHelper.toJson(transportServiceRequest));
            return boardCommonManager.loadUnloadInterceptValidate(transportServiceRequest.getWaybillCode(), transportServiceRequest.getWaybillSign());
        } catch (Exception e) {
            log.info(methodDesc + "系统异常，，请求参数=【{}】", JsonHelper.toJson(transportServiceRequest), e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE, InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.TransportCommonServiceImpl.boardCombinationCheck", jAppName = Constants.UMP_APP_NAME_DMSWEB , mState = {JProEnum.TP})
    public InvokeResult<Void> boardCombinationCheck(BoardCommonRequest request) {
        InvokeResult<Void> result = new InvokeResult<>();
        result.customMessage(InvokeResult.RESULT_SUCCESS_CODE, InvokeResult.RESULT_SUCCESS_MESSAGE);
        // ver组板拦截
        try {
            InvokeResult invokeResult = boardCommonManager.boardCombinationCheck(request);
            if (invokeResult != null) {
                result.setCode(invokeResult.getCode());
                result.setMessage(invokeResult.getMessage());
            }
            return result;
        } catch (Exception e) {
            log.error("ver组板拦截出现异常，请求参数BoardCommonRequest={},error=", JsonHelper.toJson(request), e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE, InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.TransportCommonServiceImpl.hasInspectOrSendFunction", jAppName = Constants.UMP_APP_NAME_DMSWEB , mState = {JProEnum.TP})
    public InvokeResult<Boolean> hasInspectOrSendFunction(TransportServiceRequest transportServiceRequest) {
        final String methodDesc = "TransportCommonServiceImpl.hasInspectOrSendFunction--获取发货验货白名单配置begin--";
        InvokeResult<Boolean> result = new InvokeResult<>();
        if(transportServiceRequest == null) {
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("参数不能为空");
            return result;
        }
        try{
            log.info(methodDesc + "请求参数=【{}】", JsonHelper.toJson(transportServiceRequest));
            if(transportServiceRequest.getBusinessType() == null || transportServiceRequest.getCreateSiteId() == null
                        || StringUtils.isBlank(transportServiceRequest.getUserErp())) {
                result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
                result.setMessage(InvokeResult.PARAM_ERROR);
                return result;
            }
            Integer menuCode = null;
            if(transportServiceRequest.getBusinessType().equals(1)) {
                menuCode = FuncSwitchConfigEnum.FUNCTION_SEND.getCode();
            }else if(transportServiceRequest.getBusinessType().equals(2)) {
                menuCode = FuncSwitchConfigEnum.FUNCTION_INSPECTION.getCode();
            }else {
                result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
                result.setMessage("参数businessType只允许为1（装车）或2（卸车）");
                return result;
            }
            return funcSwitchConfigApiService.hasInspectOrSendFunction(transportServiceRequest.getCreateSiteId(), menuCode,
                    transportServiceRequest.getUserErp());

        }catch (Exception e) {
            log.info(methodDesc + "系统异常，，请求参数=【{}】", JsonHelper.toJson(transportServiceRequest), e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            return result;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.TransportCommonServiceImpl.getRouterNextSiteId", jAppName = Constants.UMP_APP_NAME_DMSWEB , mState = {JProEnum.TP})
    public InvokeResult<Integer> getRouterNextSiteId(TransportServiceRequest transportServiceRequest){
        final String methodDesc = "TransportCommonServiceImpl.hasInspectOrSendFunction--获取发货验货白名单配置begin--";
        InvokeResult<Integer> result = new InvokeResult<>();
        if(transportServiceRequest == null) {
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("参数不能为空");
            return result;
        }
        log.info(methodDesc + "请求参数=【{}】", JsonHelper.toJson(transportServiceRequest));
        if(StringUtils.isBlank(transportServiceRequest.getWaybillCode())) {
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("运单号不能为空");
            return result;
        }
        if(transportServiceRequest.getCreateSiteId() == null) {
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("操作场地编码不能为空");
            return result;
        }
        try{
           Integer nextSiteId = waybillService.getRouterFromMasterDb(transportServiceRequest.getWaybillCode(), transportServiceRequest.getCreateSiteId());
            result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
            result.setData(nextSiteId);
            return result;
        }catch (Exception e) {
            log.info(methodDesc + "系统异常，，请求参数=【{}】", JsonHelper.toJson(transportServiceRequest), e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            return result;
        }
    }
}
