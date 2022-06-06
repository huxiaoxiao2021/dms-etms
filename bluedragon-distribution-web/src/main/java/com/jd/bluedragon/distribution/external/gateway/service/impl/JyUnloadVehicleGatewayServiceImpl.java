package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.inspection.request.InspectionRequest;
import com.jd.bluedragon.common.dto.inspection.response.InspectionCheckResultDto;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.*;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.common.dto.select.StringSelectOption;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyUnloadVehicleStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.UnloadProductTypeEnum;
import com.jd.bluedragon.distribution.jy.service.unload.IJyUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadDto;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.InspectionGatewayService;
import com.jd.bluedragon.external.gateway.service.JyUnloadVehicleGatewayService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @ClassName JyUnloadVehicleGatewayServiceImpl
 * @Description
 * @Author wyh
 * @Date 2022/4/1 16:40
 **/
public class JyUnloadVehicleGatewayServiceImpl implements JyUnloadVehicleGatewayService {

    private final Logger logger = LoggerFactory.getLogger(JyUnloadVehicleGatewayServiceImpl.class);

    @Autowired
    private IJyUnloadVehicleService unloadVehicleService;

    @Autowired
    private InspectionGatewayService inspectionGatewayService;

    @Override
    public JdCResponse<UnloadNoTaskResponse> createNoTaskUnloadTask(UnloadNoTaskRequest request) {
        CallerInfo info = Profiler.registerInfo("JyUnloadVehicleGatewayService.createNoTaskUnloadTask",
                Constants.UMP_APP_NAME_DMSWORKER,false, true);
        JdCResponse<UnloadNoTaskResponse> jdCResponse = new JdCResponse<UnloadNoTaskResponse>();
        try {
            // 无任务模式
            JyBizTaskUnloadDto dto = new JyBizTaskUnloadDto();
            dto.setManualCreatedFlag(Constants.CONSTANT_NUMBER_ONE);
            dto.setVehicleNumber(request.getVehicleNumber());
            dto.setOperateSiteId(request.getOperateSiteId());
            dto.setOperateSiteName(request.getOperateSiteName());
            JyBizTaskUnloadDto noTaskUnloadDto = unloadVehicleService.createUnloadTask(dto);
            UnloadNoTaskResponse unloadNoTaskResponse = new UnloadNoTaskResponse();
            unloadNoTaskResponse.setOperateSiteId(noTaskUnloadDto.getOperateSiteId());
            unloadNoTaskResponse.setOperateSiteName(noTaskUnloadDto.getOperateSiteName());
            unloadNoTaskResponse.setBizId(noTaskUnloadDto.getBizId());
            unloadNoTaskResponse.setTaskId(noTaskUnloadDto.getTaskId());
            unloadNoTaskResponse.setSealCarCode(noTaskUnloadDto.getSealCarCode());
            unloadNoTaskResponse.setVehicleNumber(noTaskUnloadDto.getVehicleNumber());
            jdCResponse.toSucceed();
            jdCResponse.setData(unloadNoTaskResponse);
        }catch (Exception e){
            logger.error("创建无任务卸车任务异常，request:{}!", JsonHelper.toJson(request), e);
            jdCResponse.toFail("创建无任务卸车任务异常，请联系分拣小秘!");
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.fetchUnloadTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<UnloadVehicleTaskResponse> fetchUnloadTask(UnloadVehicleTaskRequest request) {
        return retJdCResponse(unloadVehicleService.fetchUnloadTask(request));
    }

    @Override
    public JdCResponse<List<SelectOption>> vehicleStatusOptions() {
        List<SelectOption> optionList = new ArrayList<>();
        for (JyUnloadVehicleStatusEnum statusEnum : JyUnloadVehicleStatusEnum.values()) {
            SelectOption option = new SelectOption(statusEnum.getCode(), statusEnum.getName(), statusEnum.getCode());
            optionList.add(option);
        }
        JdCResponse<List<SelectOption>> response = new JdCResponse<>();
        response.toSucceed();
        response.setData(optionList);
        return response;
    }

    @Override
    public JdCResponse<List<StringSelectOption>> productTypeOptions() {
        List<StringSelectOption> optionList = new ArrayList<>();
        for (UnloadProductTypeEnum _enum : UnloadProductTypeEnum.values()) {
            StringSelectOption keyValue = new StringSelectOption(_enum.getCode(), _enum.getName(), _enum.getDisplayOrder());
            optionList.add(keyValue);
        }

        Collections.sort(optionList, new StringSelectOption.OrderComparator());

        JdCResponse<List<StringSelectOption>> response = new JdCResponse<>();
        response.toSucceed();
        response.setData(optionList);
        return response;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.unloadScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdVerifyResponse<Integer> unloadScan(UnloadScanRequest request) {
        JdVerifyResponse<Integer> response = new JdVerifyResponse<>();
        response.toSuccess();

        if (!checkBeforeScan(response, request)) {
            return response;
        }

        // 扫描前校验拦截结果
        if (!checkBarInterceptResult(response, request)) {
            // 失败直接返回
            return response;
        }

        InvokeResult<Integer> invokeResult = unloadVehicleService.unloadScan(request);
        if (invokeResult.getCode() == InvokeResult.RESULT_SUCCESS_CODE) {
            response.setData(invokeResult.getData());
            response.toSuccess();
            return response;
        }
        else if (invokeResult.getCode() == InvokeResult.CODE_HINT) {
            response.setCode(InvokeResult.CODE_HINT);
            response.addPromptBox(0, invokeResult.getMessage());
            return response;
        }
        else {
            response.toFail(invokeResult.getMessage());
            return response;
        }
    }

    /**
     * 扫描前校验
     * @param response
     * @param request
     * @return
     */
    private boolean checkBeforeScan(JdVerifyResponse<Integer> response, UnloadScanRequest request) {
        String barCode = request.getBarCode();
        if (StringUtils.isBlank(barCode)) {
            response.toFail("请扫描单号！");
            return false;
        }
        if (!BusinessHelper.isBoxcode(barCode)
                && !WaybillUtil.isWaybillCode(barCode)
                && !WaybillUtil.isPackageCode(barCode)) {
            response.toFail("扫描单号非法！");
            return false;
        }

        if (StringUtils.isBlank(request.getBizId())
                || StringUtils.isBlank(request.getTaskId())) {
            response.toFail("请选择卸车任务！");
            return false;
        }

        int siteCode = request.getCurrentOperate().getSiteCode();
        if (!NumberHelper.gt0(siteCode)) {
            response.toFail("缺少操作场地！");
            return false;
        }

        if (StringUtils.isBlank(request.getGroupCode())) {
            response.toFail("扫描前请绑定小组！");
            return false;
        }

        return true;
    }

    /**
     * 调用验货拦截链
     * @param response
     * @param request
     * @return
     */
    private boolean checkBarInterceptResult(JdVerifyResponse<Integer> response, UnloadScanRequest request) {
        // 非强制提交，校验拦截
        if (!request.getForceSubmit()) {
            InspectionRequest inspectionRequest = new InspectionRequest();
            inspectionRequest.setBarCode(request.getBarCode());
            inspectionRequest.setBusinessType(10);
            inspectionRequest.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
            inspectionRequest.setCreateSiteName(request.getCurrentOperate().getSiteName());
            inspectionRequest.setOperateTime(DateHelper.formatDateTime(new Date()));
            inspectionRequest.setOperateType(2);
            inspectionRequest.setOperateUserCode(request.getUser().getUserCode());
            inspectionRequest.setOperateUserName(request.getUser().getUserName());
            JdVerifyResponse<InspectionCheckResultDto> verifyResponse = inspectionGatewayService.checkBeforeInspection(inspectionRequest);
            if (verifyResponse.getCode() != JdVerifyResponse.CODE_SUCCESS) {
                response.setCode(verifyResponse.getCode());
                response.setMessage(verifyResponse.getMessage());
                return false;
            }
            else {
                if (CollectionUtils.isNotEmpty(verifyResponse.getMsgBoxes())) {
                    response.setCode(verifyResponse.getCode());
                    response.setMessage(verifyResponse.getMessage());
                    response.setMsgBoxes(verifyResponse.getMsgBoxes());
                    return true;
                }
            }
        }

        return true;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.unloadDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<UnloadScanDetail> unloadDetail(UnloadCommonRequest request) {
        return retJdCResponse(unloadVehicleService.unloadDetail(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.unloadGoodsDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<List<UnloadScanAggByProductType>> unloadGoodsDetail(UnloadGoodsRequest request) {
        return retJdCResponse(unloadVehicleService.unloadGoodsDetail(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.toScanAggByProduct",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<List<ProductTypeAgg>> toScanAggByProduct(UnloadCommonRequest request) {
        return retJdCResponse(unloadVehicleService.toScanAggByProduct(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.toScanBarCodeDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<ToScanDetailByProductType> toScanBarCodeDetail(UnloadProductTypeRequest request) {
        return retJdCResponse(unloadVehicleService.toScanBarCodeDetail(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.interceptBarCodeDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<InterceptScanBarCode> interceptBarCodeDetail(UnloadCommonRequest request) {
        return retJdCResponse(unloadVehicleService.interceptBarCodeDetail(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.moreScanBarCodeDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<MoreScanBarCode> moreScanBarCodeDetail(UnloadCommonRequest request) {
        return retJdCResponse(unloadVehicleService.moreScanBarCodeDetail(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.unloadPreviewDashboard",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<UnloadPreviewData> unloadPreviewDashboard(UnloadCommonRequest request) {
        return retJdCResponse(unloadVehicleService.unloadPreviewDashboard(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.submitUnloadCompletion",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Boolean> submitUnloadCompletion(UnloadCompleteRequest request) {
        return retJdCResponse(unloadVehicleService.submitUnloadCompletion(request));
    }

    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.countByVehicleNumberAndStatus",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Long> countByVehicleNumberAndStatus(UnsealVehicleTaskRequest request) {
        return retJdCResponse(unloadVehicleService.countByVehicleNumberAndStatus(request));
    }
}
