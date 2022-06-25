package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealCodeRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealTaskInfoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealCodeResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealTaskInfo;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealVehicleTaskResponse;
import com.jd.bluedragon.common.dto.seal.request.*;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.common.dto.seal.response.TransportResp;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.enums.JyUnSealStatusEnum;
import com.jd.bluedragon.distribution.jy.service.unseal.IJyUnSealVehicleService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.external.gateway.service.JySealVehicleGatewayService;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.jy.realtime.enums.seal.VehicleStatusEnum;
import com.jdl.jy.realtime.model.query.seal.SealVehicleTaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName JySealVehicleGatewayServiceImpl
 * @Description
 * @Author wyh
 * @Date 2022/3/9 17:54
 **/
public class JySealVehicleGatewayServiceImpl implements JySealVehicleGatewayService {

    private static final Logger log = LoggerFactory.getLogger(JySealVehicleGatewayServiceImpl.class);

    @Autowired
    private IJyUnSealVehicleService jyUnSealVehicleService;

    @Autowired
    private UccPropertyConfiguration uccConfig;

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySealVehicleGatewayService.fetchSealTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<SealVehicleTaskResponse> fetchSealTask(SealVehicleTaskRequest request) {

        JdCResponse<SealVehicleTaskResponse> response = new JdCResponse<>();
        response.toSucceed();

        if (!checkQueryParam(response, request)) {
            return response;
        }

        InvokeResult<SealVehicleTaskResponse> invokeResult = jyUnSealVehicleService.fetchSealTask(request);
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySealVehicleGatewayService.fetchUnSealTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<SealVehicleTaskResponse> fetchUnSealTask(SealVehicleTaskRequest request) {
        JdCResponse<SealVehicleTaskResponse> response = new JdCResponse<>();
        response.toSucceed();

        if (!checkQueryParam(response, request)) {
            return response;
        }
        if (Objects.equals(Constants.CONSTANT_NUMBER_ONE, uccConfig.getJyUnSealTaskSwitchToEs())) {
            return retJdCResponse(jyUnSealVehicleService.fetchSealTask(request));
        }
        else {
            return retJdCResponse(jyUnSealVehicleService.fetchUnSealTask(request));
        }
    }

    private boolean checkQueryParam(JdCResponse<SealVehicleTaskResponse> response, SealVehicleTaskRequest request) {
        String fetchType = request.getFetchType();

        if (!NumberHelper.isPositiveNumber(request.getEndSiteCode())) {
            response.toError("解封车目的地不能为空");
            return false;
        }

        switch (fetchType) {
            case SealVehicleTaskQuery.FETCH_TYPE_REFRESH:
                if (null == request.getPageNumber() || null == request.getPageSize()) {
                    response.toError("请传入分页参数");
                    return false;
                }
                if (null == request.getVehicleStatus()) {
                    response.toError("车辆状态不能为空");
                    return false;
                }
                break;
            case SealVehicleTaskQuery.FETCH_TYPE_SEARCH:
                if (StringUtils.isBlank(request.getBarCode())) {
                    response.toError("请输入搜索条件");
                    return false;
                }
                // 非封签号则是车牌号后四位，转大写
                if (!BusinessUtil.isSealBoxNo(request.getBarCode())) {
                    request.setBarCode(StringUtils.upperCase(request.getBarCode()));
                }
                break;
        }

        return true;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySealVehicleGatewayService.taskInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<SealTaskInfo> taskInfo(SealTaskInfoRequest request) {
        JdCResponse<SealTaskInfo> response = new JdCResponse<>();
        response.toSucceed();

        InvokeResult<SealTaskInfo> invokeResult = jyUnSealVehicleService.taskInfo(request);
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySealVehicleGatewayService.vehicleStatusOptions",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<List<SelectOption>> vehicleStatusOptions() {
        List<SelectOption> optionList = new ArrayList<>();
        for (VehicleStatusEnum _enum : VehicleStatusEnum.values()) {
            SelectOption option = new SelectOption(_enum.getCode(), _enum.getName(), _enum.getOrder());
            optionList.add(option);
        }

        Collections.sort(optionList, new SelectOption.OrderComparator());

        JdCResponse<List<SelectOption>> response = new JdCResponse<>();
        response.toSucceed();
        response.setData(optionList);
        return response;
    }

    @Override
    public JdCResponse<List<SelectOption>> unSealVehicleStatusOptions() {
        List<SelectOption> optionList = new ArrayList<>();
        for (JyUnSealStatusEnum _enum : JyUnSealStatusEnum.values()) {
            SelectOption option = new SelectOption(_enum.getCode(), _enum.getName(), _enum.getOrder());
            optionList.add(option);
        }

        Collections.sort(optionList, new SelectOption.OrderComparator());

        JdCResponse<List<SelectOption>> response = new JdCResponse<>();
        response.toSucceed();
        response.setData(optionList);
        return response;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySealVehicleGatewayService.sealCodeList",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<SealCodeResponse> sealCodeList(SealCodeRequest request) {
        JdCResponse<SealCodeResponse> response = new JdCResponse<>();
        response.toSucceed();
        if (null == request || StringUtils.isBlank(request.getSealCarCode())) {
            response.toFail("缺少封车编码");
            return response;
        }
        InvokeResult<SealCodeResponse> invokeResult = jyUnSealVehicleService.sealCodeList(request);
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }

    @Override
    public JdCResponse<SealCodeResponse> listSealCodeBy(SealCodeReq sealCodeReq) {
        return null;
    }

    @Override
    public JdCResponse<SealVehicleInfoResp> getSealVehicleInfo(SealVehicleInfoReq sealVehicleInfoReq) {
        return null;
    }

    @Override
    public JdCResponse<TransportResp> getTransportResourceByTransCode(TransportReq transportReq) {
        return null;
    }

    @Override
    public JdCResponse checkTransportCode(CheckTransportCodeReq checkTransportCodeReq) {
        return null;
    }

    @Override
    public JdCResponse<TransportResp> getVehicleNumberByWorkItemCode(GetVehicleNumberReq getVehicleNumberReq) {
        return null;
    }

    @Override
    public JdCResponse sealVehicle(SealVehicleReq sealVehicleReq) {
        return null;
    }

    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }
}
