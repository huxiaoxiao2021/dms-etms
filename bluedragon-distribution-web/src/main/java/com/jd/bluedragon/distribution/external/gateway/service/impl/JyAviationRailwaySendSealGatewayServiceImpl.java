package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.JyAviationRailwaySendVehicleStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.ShuttleQuerySourceEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.*;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleScanTypeEnum;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.constants.TaskBindTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.send.JyAviationRailwaySendSealServiceImpl;
import com.jd.bluedragon.external.gateway.service.JyAviationRailwaySendSealGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 14:14
 * @Description
 */

@Service
public class JyAviationRailwaySendSealGatewayServiceImpl implements JyAviationRailwaySendSealGatewayService {

    private static final Logger log = LoggerFactory.getLogger(JyAviationRailwaySendSealGatewayServiceImpl.class);




    @Autowired
    private JyAviationRailwaySendSealServiceImpl jyAviationRailwaySendSealService;

    @Autowired
    private BaseParamValidateService baseParamValidateService;

    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }


    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.scanTypeOptions",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<List<SelectOption>> scanTypeOptions() {
        List<SelectOption> optionList = new ArrayList<>();
        for (SendVehicleScanTypeEnum _enum : SendVehicleScanTypeEnum.values()) {
            SelectOption option = new SelectOption(_enum.getCode(), _enum.getName(), _enum.getDesc(), _enum.getCode());
            optionList.add(option);
        }

        Collections.sort(optionList, new SelectOption.OrderComparator());

        JdCResponse<List<SelectOption>> response = new JdCResponse<>();
        response.toSucceed();
        response.setData(optionList);
        return response;
    }

    @Override
    public JdCResponse<AviationToSendAndSendingListRes> fetchAviationToSendAndSendingList(AviationSendTaskListReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.fetchAviationToSendAndSendingList:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            if(!JyAviationRailwaySendVehicleStatusEnum.TO_SEND.getCode().equals(request.getStatusCode())
                && !JyAviationRailwaySendVehicleStatusEnum.SENDING.getCode().equals(request.getStatusCode())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "查询状态不合法", null);
            }

            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            return retJdCResponse(jyAviationRailwaySendSealService.fetchAviationToSendAndSendingList(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "查询发货流向列表服务异常", null);//500+非自定义异常
        }
    }

    @Override
    public JdCResponse<AviationSendTaskQueryRes> pageFetchAviationTaskByNextSite(AviationSendTaskQueryReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.pageFetchAviationTaskByNextSite:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            baseParamValidateService.checkPdaPage(request.getPageNo(), request.getPageSize());
            if(!NumberHelper.gt0(request.getNextSiteId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "流向场地不合法", null);
            }
            if(!JyAviationRailwaySendVehicleStatusEnum.TO_SEND.getCode().equals(request.getStatusCode())
                    && !JyAviationRailwaySendVehicleStatusEnum.SENDING.getCode().equals(request.getStatusCode())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "查询状态不合法", null);
            }
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            return retJdCResponse(jyAviationRailwaySendSealService.pageFetchAviationTaskByNextSite(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "按流向查询航空任务服务异常", null);//500+非自定义异常
        }
    }

    @Override
    public JdCResponse<AviationToSealAndSealedListRes> pageFetchAviationToSealAndSealedList(AviationSendTaskSealListReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.fetchAviationToSealAndSealedList:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            baseParamValidateService.checkPdaPage(request.getPageNo(), request.getPageSize());
            if(!JyAviationRailwaySendVehicleStatusEnum.TRUNK_LINE_SEAL_N.getCode().equals(request.getStatusCode())
                    && !JyAviationRailwaySendVehicleStatusEnum.TRUNK_LINE_SEAL_Y.getCode().equals(request.getStatusCode())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "查询状态不合法", null);
            }
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            return retJdCResponse(jyAviationRailwaySendSealService.pageFetchAviationToSealAndSealedList(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "查询航空任务封车数据服务异常", null);//500+非自定义异常
        }
    }

    @Override
    public JdCResponse<FilterConditionQueryRes> pageFetchFilterCondition(FilterConditionQueryReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.pageFetchCurrentSiteStartAirport:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            baseParamValidateService.checkPdaPage(request.getPageNo(), request.getPageSize());

            //服务调用
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            return retJdCResponse(jyAviationRailwaySendSealService.pageFetchCurrentSiteStartAirport(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "查询过滤条件服务异常", null);//500+非自定义异常
        }
    }

    @Override
    public JdCResponse<ShuttleSendTaskRes> pageFetchShuttleSendTaskList(ShuttleSendTaskReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.pageFetchShuttleSendTaskList:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            baseParamValidateService.checkPdaPage(request.getPageNo(), request.getPageSize());
            if(Objects.isNull(ShuttleQuerySourceEnum.getSourceByCode(request.getShuttleQuerySource()))) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "查询source不合法", null);
            }
            if(StringUtils.isNotBlank(request.getKeyword()) && request.getKeyword().length() != 4) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "关键词搜素仅支持后4位车牌号", null);
            }

            //服务调用
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            return retJdCResponse(jyAviationRailwaySendSealService.pageFetchShuttleSendTaskList(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "查询摆渡发车任务数据服务异常", null);//500+非自定义异常
        }
    }


    @Override
    public JdCResponse<TransportInfoQueryRes> fetchTransportCodeList(TransportCodeQueryReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.fetchTransportCodeList:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());

            //服务调用
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            return retJdCResponse(jyAviationRailwaySendSealService.fetchTransportCodeList(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "获取运力编码服务异常", null);//500+非自定义异常
        }
    }

    @Override
    public JdCResponse<TransportDataDto> scanAndCheckTransportInfo(ScanAndCheckTransportInfoReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.scanAndCheckTransportInfo:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            if(StringUtils.isBlank(request.getTransportCode())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "运力编码为空", null);
            }
            //服务调用
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            return retJdCResponse(jyAviationRailwaySendSealService.scanAndCheckTransportInfo(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "扫描校验运力服务异常", null);//500+非自定义异常
        }
    }


    @Override
    public JdCResponse<Void> sendTaskBinding(SendTaskBindReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.sendTaskBinding:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            //业务参数校验
            if(StringUtils.isBlank(request.getBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "被绑任务bizId为空", null);
            }
            if(CollectionUtils.isEmpty(request.getSendTaskBindDtoList())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "需绑任务信息为空", null);
            }
            if(request.getSendTaskBindDtoList().size() > 100) {
                //todo 风险规避，PDA不限制数量
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "最多绑定100个任务", null);
            }
            //服务调用
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            request.setType(TaskBindTypeEnum.BIND_TYPE_AVIATION_RAILWAY.getCode());
            return retJdCResponse(jyAviationRailwaySendSealService.sendTaskBinding(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "摆渡任务绑定空铁任务服务异常", null);//500+非自定义异常
        }
    }

    @Override
    public JdCResponse<Void> sendTaskUnbinding(SendTaskUnbindReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.sendTaskUnbinding:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            //业务参数校验
            if(StringUtils.isBlank(request.getBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "被解绑任务bizId为空", null);
            }
            if(StringUtils.isBlank(request.getUnbindBizId()) || StringUtils.isBlank(request.getUnbindDetailBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "需解绑任务信息为空", null);
            }
            //服务调用
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            request.setType(TaskBindTypeEnum.BIND_TYPE_AVIATION_RAILWAY.getCode());
            return retJdCResponse(jyAviationRailwaySendSealService.sendTaskUnbinding(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "摆渡任务解绑空铁任务服务异常", null);//500+非自定义异常
        }
    }

    @Override
    public JdCResponse<SendTaskBindQueryRes> fetchSendTaskBindingData(SendTaskBindQueryReq request) {
        return null;
    }

    @Override
    public JdCResponse<ShuttleTaskSealCarQueryRes> fetchShuttleTaskSealCarInfo(ShuttleTaskSealCarQueryReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.fetchShuttleTaskSealCarInfo:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            //业务参数校验
            if(StringUtils.isBlank(request.getBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "被解绑任务bizId为空", null);
            }
            //服务调用
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            return retJdCResponse(jyAviationRailwaySendSealService.fetchShuttleTaskSealCarInfo(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "摆渡任务封车信息查询服务异常", null);//500+非自定义异常
        }
    }

    @Override
    public JdCResponse<Void> shuttleTaskSealCar(ShuttleTaskSealCarReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.shuttleTaskSealCar:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            //服务调用
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            return retJdCResponse(jyAviationRailwaySendSealService.shuttleTaskSealCar(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "摆渡任务封车服务异常", null);//500+非自定义异常
        }
    }

    @Override
    public JdCResponse<Void> aviationTaskSealCar(AviationTaskSealCarReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.aviationTaskSealCar:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            //服务调用
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            return retJdCResponse(jyAviationRailwaySendSealService.aviationTaskSealCar(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "航空任务封车服务异常", null);//500+非自定义异常
        }
    }
}
