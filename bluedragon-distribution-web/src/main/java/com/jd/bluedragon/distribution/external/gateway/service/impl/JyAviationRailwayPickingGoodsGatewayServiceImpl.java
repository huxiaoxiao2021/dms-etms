package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.picking.JyAviationRailwayPickingGoodsService;
import com.jd.bluedragon.external.gateway.service.JyAviationRailwayPickingGoodsGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Date 2023/12/4 10:57
 * @Description
 */
@Service
public class JyAviationRailwayPickingGoodsGatewayServiceImpl implements JyAviationRailwayPickingGoodsGatewayService {
    private static final Logger log = LoggerFactory.getLogger(JyAviationRailwayPickingGoodsGatewayServiceImpl.class);


    @Autowired
    private BaseParamValidateService baseParamValidateService;

    @Autowired
    private JyAviationRailwayPickingGoodsService jyAviationRailwayPickingGoodsService;

    private <T> JdCResponse<T> retJdcResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }


    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsGatewayService.pickingGoodsScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<PickingGoodsRes> pickingGoodsScan(PickingGoodsReq request) {
        if(Objects.isNull(request)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwayPickingGoodsGatewayService.pickingGoodsScan:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());

            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            return retJdcResponse(jyAviationRailwayPickingGoodsService.pickingGoodsScan(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "航空提货服务异常", null);//500+非自定义异常
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsGatewayService.finishPickGoods",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Void> finishPickGoods(FinishPickGoodsReq req) {
        if(Objects.isNull(req)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwayPickingGoodsGatewayService.finishPickGoods:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    req.getUser(), req.getCurrentOperate(), req.getGroupCode(), req.getPost());

            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(req));
            }
            return retJdcResponse(jyAviationRailwayPickingGoodsService.finishPickGoods(req));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "空铁提货完成异常", null);//500+非自定义异常
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsGatewayService.submitException",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Void> submitException(ExceptionSubmitReq req) {
        if(Objects.isNull(req)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwayPickingGoodsGatewayService.submitException:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    req.getUser(), req.getCurrentOperate(), req.getGroupCode(), req.getPost());
            if(Objects.isNull(req.getCurrentOperate().getOperatorData())
                    || StringUtils.isBlank(req.getCurrentOperate().getOperatorData().getPositionCode())) {
                return new JdCResponse<>(JdCResponse.CODE_FAIL, "岗位码为空", null);
            }
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(req));
            }
            return retJdcResponse(jyAviationRailwayPickingGoodsService.submitException(req));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "空铁异常上报异常", null);//500+非自定义异常
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsGatewayService.listSendFlowInfo",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<SendFlowRes> listSendFlowInfo(SendFlowReq req) {
        if(Objects.isNull(req)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwayPickingGoodsGatewayService.listSendFlowInfo:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    req.getUser(), req.getCurrentOperate(), req.getGroupCode(), req.getPost());

            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(req));
            }
            return retJdcResponse(jyAviationRailwayPickingGoodsService.listSendFlowInfo(req));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "空铁流向信息查询异常", null);//500+非自定义异常
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsGatewayService.addSendFlow",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Void> addSendFlow(SendFlowAddReq req) {
        if(Objects.isNull(req)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwayPickingGoodsGatewayService.addSendFlow:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    req.getUser(), req.getCurrentOperate(), req.getGroupCode(), req.getPost());

            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(req));
            }
            return retJdcResponse(jyAviationRailwayPickingGoodsService.addSendFlow(req));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "空铁流向添加异常", null);//500+非自定义异常
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsGatewayService.deleteSendFlow",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Void> deleteSendFlow(SendFlowDeleteReq req) {
        if(Objects.isNull(req)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwayPickingGoodsGatewayService.deleteSendFlow:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    req.getUser(), req.getCurrentOperate(), req.getGroupCode(), req.getPost());

            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(req));
            }
            return retJdcResponse(jyAviationRailwayPickingGoodsService.deleteSendFlow(req));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "空铁提货流向删除异常！", null);//500+非自定义异常
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsGatewayService.finishSendTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Void> finishSendTask(FinishSendTaskReq req) {
        if(Objects.isNull(req)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwayPickingGoodsGatewayService.finishSendTask:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    req.getUser(), req.getCurrentOperate(), req.getGroupCode(), req.getPost());

            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(req));
            }
            InvokeResult<FinishSendTaskRes> invokeResult = jyAviationRailwayPickingGoodsService.completePickingSendTask(req);
            JdCResponse<Void> res = new JdCResponse<Void>();
            res.init(invokeResult.getCode(), invokeResult.getMessage());
            return res;
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "空铁发货完成接口异常", null);//500+非自定义异常
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsGatewayService.completePickingSendTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<FinishSendTaskRes> completePickingSendTask(FinishSendTaskReq req) {
        if(Objects.isNull(req)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwayPickingGoodsGatewayService.completePickingSendTask:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    req.getUser(), req.getCurrentOperate(), req.getGroupCode(), req.getPost());

            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(req));
            }
            return retJdcResponse(jyAviationRailwayPickingGoodsService.completePickingSendTask(req));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "提货发货完成接口异常", null);//500+非自定义异常
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsGatewayService.listAirRailTaskSummary",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<AirRailTaskRes> listAirRailTaskSummary(AirRailTaskSummaryReq req) {
        if(Objects.isNull(req)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwayPickingGoodsGatewayService.listAirRailTaskSummary:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    req.getUser(), req.getCurrentOperate(), req.getGroupCode(), req.getPost());

            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(req));
            }
            return retJdcResponse(jyAviationRailwayPickingGoodsService.listAirRailTaskSummary(req));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "空铁提货机场/车站列表查询异常", null);//500+非自定义异常
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsGatewayService.listAirRailTaskAgg",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<AirRailTaskAggRes> listAirRailTaskAgg(AirRailTaskAggReq req) {
        if(Objects.isNull(req)){
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwayPickingGoodsGatewayService.listAirRailTaskAgg:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    req.getUser(), req.getCurrentOperate(), req.getGroupCode(), req.getPost());

            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(req));
            }
            return retJdcResponse(jyAviationRailwayPickingGoodsService.listAirRailTaskAgg(req));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "空铁提货机场/车站列表明细查询异常", null);//500+非自定义异常
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsGatewayService.pageFetchSendBatchCodeDetailList",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<PickingSendBatchCodeDetailRes> pageFetchSendBatchCodeDetailList(PickingSendBatchCodeDetailReq req) {
        if (Objects.isNull(req)) {
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwayPickingGoodsGatewayService.pageFetchSendBatchCodeDetailList:";
        try {
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    req.getUser(), req.getCurrentOperate(), req.getGroupCode(), req.getPost());
            baseParamValidateService.checkPdaPage(req.getPageNum(), req.getPageSize());
            if (log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(req));
            }
            return retJdcResponse(jyAviationRailwayPickingGoodsService.pageFetchSendBatchCodeDetailList(req));
        } catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        } catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "查询批次数据服务异常", null);//500+非自定义异常
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyAviationRailwayPickingGoodsGatewayService.delBatchCodes",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<DelBatchCodesRes> delBatchCodes(DelBatchCodesReq req) {
        if (Objects.isNull(req)) {
            return new JdCResponse<>(JdCResponse.CODE_FAIL, "参数为空", null);
        }
        final String methodDesc = "JyAviationRailwayPickingGoodsGatewayService.delBatchCodes:";
        try {
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    req.getUser(), req.getCurrentOperate(), req.getGroupCode(), req.getPost());
            if (log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(req));
            }
            return retJdcResponse(jyAviationRailwayPickingGoodsService.delBatchCodes(req));
        } catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        } catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(req), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "删除批次服务异常", null);//500+非自定义异常
        }    }
}
