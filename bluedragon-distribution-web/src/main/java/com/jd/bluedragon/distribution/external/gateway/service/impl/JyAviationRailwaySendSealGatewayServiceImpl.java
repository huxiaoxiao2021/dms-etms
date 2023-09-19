package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.blockcar.enumeration.SealCarTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.JyAviationRailwaySendVehicleStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.ShuttleQuerySourceEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.*;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleScanTypeEnum;
import com.jd.bluedragon.common.dto.seal.request.ShuttleTaskSealCarReq;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.constants.TaskBindTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.seal.JySealVehicleService;
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

import static com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.SendTaskQueryEnum.TASK_RECOMMEND;

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
    private JySealVehicleService jySealVehicleService;
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.fetchAviationToSendAndSendingList",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.pageFetchAviationTaskByNextSite",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
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
            if(!TASK_RECOMMEND.getCode().equals(request.getSource())
                    && !JyAviationRailwaySendVehicleStatusEnum.TO_SEND.getCode().equals(request.getStatusCode())
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.pageFetchAviationToSealAndSealedList",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.pageFetchAviationSealedList",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<AviationSealedListRes> pageFetchAviationSealedList(AviationSealedListReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.AviationSealedListRes:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            baseParamValidateService.checkPdaPage(request.getPageNo(), request.getPageSize());
            if(StringUtils.isBlank(request.getSendVehicleBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "摆渡任务bizId为空", null);
            }
            if(StringUtils.isBlank(request.getSendVehicleDetailBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "摆渡任务detailBizId为空", null);
            }
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            return retJdCResponse(jyAviationRailwaySendSealService.pageFetchAviationSealedList(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "查询已封航空任务服务异常", null);//500+非自定义异常
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.pageFetchFilterCondition",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<FilterConditionQueryRes> pageFetchFilterCondition(FilterConditionQueryReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.pageFetchCurrentSiteStartAirport:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());

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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.pageFetchShuttleSendTaskList",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.fetchTransportCodeList",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<TransportInfoQueryRes> fetchTransportCodeList(TransportCodeQueryReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.fetchTransportCodeList:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            if(StringUtils.isBlank(request.getBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "航空任务bizId为空", null);
            }
            if(StringUtils.isBlank(request.getDetailBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "航空任务detailBizId为空", null);
            }
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.scanAndCheckTransportInfo",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
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
            if(Objects.isNull(request.getNextSiteId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "流向场地为空", null);
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.sendTaskBinding",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
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
            if(StringUtils.isBlank(request.getDetailBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "摆渡任务detailBizId为空", null);
            }
            if(StringUtils.isBlank(request.getBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "摆渡任务bizId为空", null);
            }
            if(CollectionUtils.isEmpty(request.getSendTaskBindDtoList())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "航空任务信息为空", null);
            }
            if(request.getSendTaskBindDtoList().size() > JyAviationRailwaySendSealServiceImpl.BIND_TASK_NUM) {
                String msg = String.format("最多绑定%s个任务", JyAviationRailwaySendSealServiceImpl.BIND_TASK_NUM);
                return new JdCResponse<>(JdCResponse.CODE_FAIL, msg, null);
            }
            //服务调用
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            request.setType(TaskBindTypeEnum.BIND_TYPE_AVIATION.getCode());
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.sendTaskUnbinding",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
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
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "摆渡任务bizId为空", null);
            }
            if(StringUtils.isBlank(request.getDetailBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "摆渡任务detailBizId为空", null);
            }
            if(StringUtils.isBlank(request.getUnbindBizId()) || StringUtils.isBlank(request.getUnbindDetailBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "航空任务信息为空", null);
            }
            //服务调用
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            request.setType(TaskBindTypeEnum.BIND_TYPE_AVIATION.getCode());
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.fetchSendTaskBindingData",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<SendTaskBindQueryRes> fetchSendTaskBindingData(SendTaskBindQueryReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.fetchSendTaskBindingData:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            //业务参数校验
            if(StringUtils.isBlank(request.getBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "摆渡任务bizId为空", null);
            }
            if(StringUtils.isBlank(request.getDetailBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "摆渡任务detailBizId为空", null);
            }
            request.setType(TaskBindTypeEnum.BIND_TYPE_AVIATION.getCode());
            //服务调用
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            return retJdCResponse(jyAviationRailwaySendSealService.queryBindTaskList(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "查询绑定航空任务服务异常", null);//500+非自定义异常
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.fetchShuttleTaskSealCarInfo",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
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
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "摆渡任务bizId为空", null);
            }
            if(StringUtils.isBlank(request.getDetailBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "摆渡任务detailBizId为空", null);
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.shuttleTaskSealCar",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Void> shuttleTaskSealCar(ShuttleTaskSealCarReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.shuttleTaskSealCar:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            //业务参数校验
            if(StringUtils.isBlank(request.getBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "摆渡任务bizId为空", null);
            }
            if(StringUtils.isBlank(request.getDetailBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "摆渡任务detailBizId为空", null);
            }
            if(StringUtils.isBlank(request.getTransportCode())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "运力编码不能为空", null);
            }
            if(!NumberHelper.gt0(request.getVolume())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "体积不合法", null);
            }
            if(!NumberHelper.gt0(request.getPalletCount())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "托盘数不合法", null);
            }
            //服务调用
            request.setSealCarType(SealCarTypeEnum.SEAL_BY_TRANSPORT_CAPABILITY.getType());
            return retJdCResponse(jySealVehicleService.shuttleTaskSealCar(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "摆渡任务封车服务异常", null);//500+非自定义异常
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.aviationTaskSealCar",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Void> aviationTaskSealCar(AviationTaskSealCarReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.aviationTaskSealCar:航空任务封车：";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            if(StringUtils.isBlank(request.getBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "任务bizId为空", null);
            }
            if(StringUtils.isBlank(request.getBookingCode())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "订舱号为空", null);
            }
            if(!NumberHelper.gt0(request.getWeight()) || !NumberHelper.gt0(request.getVolume())){
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "重量/体积不能小于0", null);
            }
            if(StringUtils.isBlank(request.getTransportCode())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "运力编码不能为空", null);
            }
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

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.scan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdVerifyResponse<AviationSendScanResp> scan(AviationSendScanReq request) {
        if(Objects.isNull(request)){
            return new JdVerifyResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        try{
            baseParamValidateService.checkUserAndSiteAndGroup(request.getUser(),request.getCurrentOperate(),request.getGroupCode());
            scanRequestCheck(request);
            //服务调用
            if(log.isInfoEnabled()) {
                log.info("空铁发货岗，发货扫描请求信息={}", JsonHelper.toJson(request));
            }
            return jyAviationRailwaySendSealService.scan(request);
        }catch (JyBizException ex) {
            log.error("空铁发货岗，发货扫描请求信息自定义异常捕获，请求信息={},errMsg={}", JsonHelper.toJson(request), ex.getMessage());
            return new JdVerifyResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("空铁发货岗，发货扫描执行异常={},errMsg={}", JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdVerifyResponse<>(JdCResponse.CODE_ERROR, "包裹发货异常，请联系分拣小秘！", null);//500+非自定义异常
        }
    }

    private void scanRequestCheck(AviationSendScanReq request) {
        if (StringUtils.isEmpty(request.getSendVehicleBizId())) {
            throw new JyBizException("派车任务主键为空！");
        }
        if (StringUtils.isEmpty(request.getSendVehicleDetailBizId())) {
            throw new JyBizException("派车任务明细主键为空！");
        }
        if (StringUtils.isEmpty(request.getBarCode())) {
            throw new JyBizException("扫描条码为空！");
        }
        if (request.getBarCodeType() == null) {
            throw new JyBizException("扫描条码类型为空！");
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.getAviationSendVehicleProgress",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<AviationSendVehicleProgressResp> getAviationSendVehicleProgress(AviationSendVehicleProgressReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        try{
            if(log.isInfoEnabled()) {
                log.info("空铁发货岗，发货统计数据请求信息={}", JsonHelper.toJson(request));
            }
            return retJdCResponse(jyAviationRailwaySendSealService.getAviationSendVehicleProgress(request));
        }catch (Exception ex) {
            log.error("空铁发货岗，统计数据查询异常={},errMsg={}", JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "包裹发货异常，请联系分拣小秘！", null);//500+非自定义异常
        }   
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.abnormalBarCodeDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<AviationSendAbnormalPackResp> abnormalBarCodeDetail(AviationSendAbnormalPackReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        try{
            if(log.isInfoEnabled()) {
                log.info("空铁发货岗，异常发货明细请求信息={}", JsonHelper.toJson(request));
            }
            return retJdCResponse(jyAviationRailwaySendSealService.abnormalBarCodeDetail(request));
        }catch (Exception ex) {
            log.error("空铁发货岗，异常发货明细请求信息={},errMsg={}", JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "查询异常发货信息异常！", null);//500+非自定义异常
        }
    }
    
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.sendBarCodeDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<AviationBarCodeDetailResp> sendBarCodeDetail(AviationBarCodeDetailReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        try{
            
            if(log.isInfoEnabled()) {
                log.info("空铁发货岗，发货明细请求信息={}", JsonHelper.toJson(request));
            }
            return retJdCResponse(jyAviationRailwaySendSealService.sendBarCodeDetail(request));
        }catch (Exception ex) {
            log.error("空铁发货岗，发货明细请求信息={},errMsg={}", JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "查询发货信息异常！", null);//500+非自定义异常
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.sendBoxDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<AviationBarCodeDetailResp> sendBoxDetail(AviationBoxDetailReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        try{
            if(log.isInfoEnabled()) {
                log.info("空铁发货岗，发货明细请求信息={}", JsonHelper.toJson(request));
            }
            return retJdCResponse(jyAviationRailwaySendSealService.sendBoxDetail(request));
        }catch (Exception ex) {
            log.error("空铁发货岗，发货明细请求信息={},errMsg={}", JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "查询发货信息异常！", null);//500+非自定义异常
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.aviationSendComplete",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Void> aviationSendComplete(AviationSendCompleteReq req) {
        if(Objects.isNull(req)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        
        if (StringUtils.isEmpty(req.getSendVehicleBizId())) {
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "任务id为空！");
        }
        
        try{
            if(log.isInfoEnabled()) {
                log.info("空铁发货岗，发货任务完成={}", JsonHelper.toJson(req));
            }
            return retJdCResponse(jyAviationRailwaySendSealService.aviationSendComplete(req));
        }catch (Exception ex) {
            log.error("空铁发货岗，发货任务完成={},errMsg={}", JsonHelper.toJson(req), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, JsonHelper.toJson(ex), null);//500+非自定义异常
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.validateTranCodeAndSendCode",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<ScanSendCodeValidRes> validateTranCodeAndSendCode(ScanSendCodeValidReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.validateTranCodeAndSendCode:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            if(StringUtils.isBlank(request.getTransportCode())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "运力编码为空", null);
            }
            if(StringUtils.isBlank(request.getSendCode())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "批次号为空", null);
            }
            //服务调用
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            return retJdCResponse(jyAviationRailwaySendSealService.validateTranCodeAndSendCode(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "扫描批次号服务异常", null);//500+非自定义异常
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwaySendSealGatewayServiceImpl.fetchToSealShuttleTaskDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<ShuttleTaskSealCarQueryRes> fetchToSealShuttleTaskDetail(ShuttleTaskSealCarQueryReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwaySendSealGatewayServiceImpl.fetchToSealShuttleTaskDetail:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            //业务参数校验
            if(StringUtils.isBlank(request.getBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "摆渡任务bizId为空", null);
            }
            if(StringUtils.isBlank(request.getDetailBizId())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "摆渡任务detailBizId为空", null);
            }
            //服务调用
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            return retJdCResponse(jyAviationRailwaySendSealService.fetchToSealShuttleTaskDetail(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "摆渡任务待封车明细查询服务异常", null);//500+非自定义异常
        }
    }
}
