package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.blockcar.enumeration.SealCarSourceEnum;
import com.jd.bluedragon.common.dto.blockcar.enumeration.SealCarTypeEnum;
import com.jd.bluedragon.common.dto.blockcar.request.SealCarPreRequest;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendModeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehiclePhotoEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendAbnormalResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendScanResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.NewSealVehicleRequest;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.api.response.TransWorkItemResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.InvokeWithMsgBoxResult;
import com.jd.bluedragon.distribution.jy.api.JySendVehicleTysService;
import com.jd.bluedragon.distribution.jy.dto.JySelectOption;
import com.jd.bluedragon.distribution.jy.dto.send.*;
import com.jd.bluedragon.distribution.jy.enums.JyLineTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JySendLineTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JySendVehicleStatusEnum;
import com.jd.bluedragon.distribution.jy.service.seal.JySealVehicleService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.utils.BeanCopyUtil;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.SERVER_ERROR_CODE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.SERVER_ERROR_MESSAGE;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/8/17
 * @Description: 拣运发货封车岗 转运专用服务
 *
 *  与卸车岗思想保持一致，暂不增加租户概念
 */
@Service("jySendVehicleTysService")
public class JySendVehicleTysServiceImpl implements JySendVehicleTysService {

    private static final Logger logger = LoggerFactory.getLogger(JySendVehicleTysServiceImpl.class);

    @Autowired
    private IJySendVehicleService jySendVehicleServiceTys;
    @Autowired
    JySealVehicleService jySealVehicleService;
    @Autowired
    private NewSealVehicleService newsealVehicleService;

    /**
     * 发货模式
     *
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.sendModeOptions", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<List<JySelectOption>> sendModeOptions() {
        InvokeResult<List<JySelectOption>> result = new InvokeResult<>();
        List<JySelectOption> options = new ArrayList<>();
        for (SendModeEnum _enum : SendModeEnum.values()) {
            options.add(new JySelectOption(_enum.getCode(), _enum.getName(), _enum.getDesc()));
        }
        result.setData(options);
        return result;
    }

    /**
     * 车辆状态枚举
     *
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.vehicleStatusOptions", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<List<JySelectOption>> vehicleStatusOptions() {
        InvokeResult<List<JySelectOption>> result = new InvokeResult<>();
        List<JySelectOption> optionList = new ArrayList<>();
        for (JySendVehicleStatusEnum _enum : JySendVehicleStatusEnum.values()) {
            optionList.add(new JySelectOption(_enum.getCode(), _enum.getName(), _enum.getOrder()));
        }

        Collections.sort(optionList, new JySelectOption.OrderComparator());
        result.setData(optionList);
        return result;
    }

    /**
     * 线路类型枚举
     *
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.lineTypeOptions", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<List<JySelectOption>> lineTypeOptions() {
        InvokeResult<List<JySelectOption>> result = new InvokeResult<>();
        List<JySelectOption> optionList = new ArrayList<>();
        for (JyLineTypeEnum _enum : JyLineTypeEnum.values()) {
            optionList.add(new JySelectOption(_enum.getCode(), _enum.getName(), _enum.getCode()));
        }
        Collections.sort(optionList, new JySelectOption.OrderComparator());
        result.setData(optionList);
        return result;
    }

    /**
     * 发货扫描方式枚举
     *
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.scanTypeOptions", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<List<JySelectOption>> scanTypeOptions() {
        InvokeResult<List<JySelectOption>> result = new InvokeResult<>();
        List<JySelectOption> optionList = new ArrayList<>();
        for (SendVehicleScanTypeEnum _enum : SendVehicleScanTypeEnum.values()) {
            optionList.add(new JySelectOption(_enum.getCode(), _enum.getName(), _enum.getCode()));
        }
        Collections.sort(optionList, new JySelectOption.OrderComparator());
        result.setData(optionList);
        return result;
    }

    /**
     * 拉取发货任务列表
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.fetchSendVehicleTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<SendVehicleTaskResp> fetchSendVehicleTask(SendVehicleTaskReq request) {
        InvokeResult<SendVehicleTaskResp> result = new InvokeResult<SendVehicleTaskResp>();
        SendVehicleTaskResp sendVehicleTaskResp = new SendVehicleTaskResp();
        try{
            SendVehicleTaskRequest param = new SendVehicleTaskRequest();
            BeanCopyUtil.copy(request,param);

            InvokeResult<SendVehicleTaskResponse> invokeResult = jySendVehicleServiceTys.fetchSendVehicleTask(param);
            if(invokeResult != null){
                result.setCode(invokeResult.getCode());
                result.setMessage(invokeResult.getMessage());
                BeanCopyUtil.copy(invokeResult.getData(),sendVehicleTaskResp);
                result.setData(sendVehicleTaskResp);
            }else{
                result.error();
                logger.error("JySendVehicleTysService.fetchSendVehicleTask error! invokeResult is null ,req:{}", JsonHelper.toJson(request));
            }
        }catch (Exception e){
            logger.error("JySendVehicleTysService.fetchSendVehicleTask error! ,req:{}", JsonHelper.toJson(request));
            result.error();
        }finally {
            if(logger.isInfoEnabled()){
                logger.info("JySendVehicleTysService.fetchSendVehicleTask req:{} , resp:{}",JsonHelper.toJson(request),JsonHelper.toJson(result));
            }
        }
        return result;
    }

    /**
     * 车辆未到、已到候选
     *
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.sendPhotoOptions", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<List<JySelectOption>> sendPhotoOptions() {
        InvokeResult<List<JySelectOption>> result = new InvokeResult<>();
        List<JySelectOption> optionList = new ArrayList<>();
        for (SendVehiclePhotoEnum _enum : SendVehiclePhotoEnum.values()) {
            optionList.add(new JySelectOption(_enum.getCode(), _enum.getName(), _enum.getDesc()));
        }
        Collections.sort(optionList, new JySelectOption.CodeComparator());
        result.setData(optionList);
        return result;
    }

    /**
     * 进入发货任务前拍照
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.uploadPhoto", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<Boolean> uploadPhoto(SendPhotoReq request) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        try{
            SendPhotoRequest param = new SendPhotoRequest();
            BeanCopyUtil.copy(request,param);

            InvokeResult<Boolean> invokeResult = jySendVehicleServiceTys.uploadPhoto(param);
            if(invokeResult != null){
                result.setCode(invokeResult.getCode());
                result.setMessage(invokeResult.getMessage());
                result.setData(invokeResult.getData());
            }else{
                result.error();
                logger.error("JySendVehicleTysService.uploadPhoto error! invokeResult is null ,req:{}", JsonHelper.toJson(request));
            }
        }catch (Exception e){
            logger.error("JySendVehicleTysService.uploadPhoto error! ,req:{}", JsonHelper.toJson(request));
            result.error();
        }finally {
            if(logger.isInfoEnabled()){
                logger.info("JySendVehicleTysService.uploadPhoto req:{} , resp:{}",JsonHelper.toJson(request),JsonHelper.toJson(result));
            }
        }
        return result;
    }

    /**
     * 发货任务详情
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.sendVehicleInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<SendVehicleInfo> sendVehicleInfo(SendVehicleInfoReq request) {
        InvokeResult<SendVehicleInfo> result = new InvokeResult<SendVehicleInfo>();
        try{
            SendVehicleInfoRequest param = new SendVehicleInfoRequest();
            BeanCopyUtil.copy(request,param);
            InvokeResult<com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleInfo> invokeResult =
                    jySendVehicleServiceTys.sendVehicleInfo(param);
            if(invokeResult != null){
                result.setCode(invokeResult.getCode());
                result.setMessage(invokeResult.getMessage());
                SendVehicleInfo sendVehicleTaskResp = new SendVehicleInfo();
                BeanCopyUtil.copy(invokeResult.getData(),sendVehicleTaskResp);
                result.setData(sendVehicleTaskResp);
            }else{
                result.error();
                logger.error("JySendVehicleTysService.sendVehicleInfo error! invokeResult is null ,req:{}", JsonHelper.toJson(request));
            }
        }catch (Exception e){
            logger.error("JySendVehicleTysService.sendVehicleInfo error! ,req:{}", JsonHelper.toJson(request));
            result.error();
        }finally {
            if(logger.isInfoEnabled()){
                logger.info("JySendVehicleTysService.sendVehicleInfo req:{} , resp:{}",JsonHelper.toJson(request),JsonHelper.toJson(result));
            }
        }
        return result;
    }

    /**
     * 发货任务流向明细列表
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.sendDestDetail", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<List<SendDestDetail>> sendDestDetail(SendDetailReq request) {
        InvokeResult<List<SendDestDetail>> result = new InvokeResult<List<SendDestDetail>>();
        try{
            SendDetailRequest param = new SendDetailRequest();
            BeanCopyUtil.copy(request,param);
            InvokeResult<List<com.jd.bluedragon.common.dto.operation.workbench.send.response.SendDestDetail>> invokeResult =
                    jySendVehicleServiceTys.sendDestDetail(param);
            if(invokeResult != null){
                result.setCode(invokeResult.getCode());
                result.setMessage(invokeResult.getMessage());
                List<SendDestDetail> sendDestDetails = new ArrayList<>();
                if(!CollectionUtils.isEmpty(invokeResult.getData())){
                    for(com.jd.bluedragon.common.dto.operation.workbench.send.response.SendDestDetail _sendDestDetail : invokeResult.getData()){
                        SendDestDetail sendDestDetail = new SendDestDetail();
                        BeanCopyUtil.copy(_sendDestDetail,sendDestDetail);
                        sendDestDetails.add(sendDestDetail);
                    }
                }
                result.setData(sendDestDetails);
            }else{
                result.error();
                logger.error("JySendVehicleTysService.sendDestDetail error! invokeResult is null ,req:{}", JsonHelper.toJson(request));
            }
        }catch (Exception e){
            logger.error("JySendVehicleTysService.sendDestDetail error! ,req:{}", JsonHelper.toJson(request));
            result.error();
        }finally {
            if(logger.isInfoEnabled()){
                logger.info("JySendVehicleTysService.sendDestDetail req:{} , resp:{}",JsonHelper.toJson(request),JsonHelper.toJson(result));
            }
        }
        return result;
    }

    /**
     * 发货进度
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.loadProgress", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<SendVehicleProgress> loadProgress(SendVehicleProgressReq request) {
        SendVehicleProgressRequest sendVehicleProgressRequest =BeanUtils.copy(request,SendVehicleProgressRequest.class);
        InvokeResult<com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleProgress> result =jySendVehicleServiceTys.loadProgress(sendVehicleProgressRequest);
        if (ObjectHelper.isNotNull(result)){
            return convertResult(result,SendVehicleProgress.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 校验发货任务是否异常
     * <ul>
     *     <li>单流向任务直接校验，多流向只在最后一个流向封车时校验</li>
     *     <li>拦截&强扫或装载率不足，两者都满足时异常优先</li>
     * </ul>
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.checkSendVehicleNormalStatus", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<SendAbnormalResp> checkSendVehicleNormalStatus(SendAbnormalReq request) {
        SendAbnormalRequest sendAbnormalRequest =BeanUtils.copy(request,SendAbnormalRequest.class);
        InvokeResult<SendAbnormalResponse> result =jySendVehicleServiceTys.checkSendVehicleNormalStatus(sendAbnormalRequest);
        if (ObjectHelper.isNotNull(result)){
            return convertResult(result,SendAbnormalResp.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 发货拦截包裹明细 （以运单维度展示）
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.interceptedBarCodeDetail", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<SendAbnormalBarCode> interceptedBarCodeDetail(SendAbnormalPackReq request) {
        SendAbnormalPackRequest sendAbnormalPackRequest =BeanUtils.copy(request,SendAbnormalPackRequest.class);
        InvokeResult<com.jd.bluedragon.common.dto.operation.workbench.send.response.SendAbnormalBarCode> result =jySendVehicleServiceTys.interceptedBarCodeDetail(sendAbnormalPackRequest);
        if (ObjectHelper.isNotNull(result)){
            return convertResult(result,SendAbnormalBarCode.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 强制发货包裹明细  （以运单维度展示）
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.forceSendBarCodeDetail", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<SendAbnormalBarCode> forceSendBarCodeDetail(SendAbnormalPackReq request) {
        SendAbnormalPackRequest sendAbnormalPackRequest =BeanUtils.copy(request,SendAbnormalPackRequest.class);
        InvokeResult<com.jd.bluedragon.common.dto.operation.workbench.send.response.SendAbnormalBarCode> result= jySendVehicleServiceTys.forceSendBarCodeDetail(sendAbnormalPackRequest);
        if (ObjectHelper.isNotNull(result)){
            return convertResult(result,SendAbnormalBarCode.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 发货异常包裹明细  （以运单维度展示）
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.abnormalSendBarCodeDetail", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<SendAbnormalBarCode> abnormalSendBarCodeDetail(SendAbnormalPackReq request) {
        SendAbnormalPackRequest sendAbnormalPackRequest =BeanUtils.copy(request,SendAbnormalPackRequest.class);
        InvokeResult<com.jd.bluedragon.common.dto.operation.workbench.send.response.SendAbnormalBarCode> result= jySendVehicleServiceTys.abnormalSendBarCodeDetail(sendAbnormalPackRequest);
        if (ObjectHelper.isNotNull(result)){
            return convertResult(result,SendAbnormalBarCode.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 不齐运单发货包裹明细  （以运单维度展示）
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.incompleteSendBarCodeDetail", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<SendAbnormalBarCode> incompleteSendBarCodeDetail(SendAbnormalPackReq request) {
        SendAbnormalPackRequest sendAbnormalPackRequest =BeanUtils.copy(request,SendAbnormalPackRequest.class);
        InvokeResult<com.jd.bluedragon.common.dto.operation.workbench.send.response.SendAbnormalBarCode> result= null;
        if (ObjectHelper.isNotNull(result)){
            return convertResult(result,SendAbnormalBarCode.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 已到未扫发货包裹明细  （以运单维度展示）
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.toScanSendBarCodeDetail", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<SendAbnormalBarCode> toScanSendBarCodeDetail(SendAbnormalPackReq request) {
        SendAbnormalPackRequest sendAbnormalPackRequest =BeanUtils.copy(request,SendAbnormalPackRequest.class);
        InvokeResult<com.jd.bluedragon.common.dto.operation.workbench.send.response.SendAbnormalBarCode> result=null;
        if (ObjectHelper.isNotNull(result)){
            return convertResult(result,SendAbnormalBarCode.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 选择封车流向
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.selectSealDest", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<ToSealDestAgg> selectSealDest(SelectSealDestReq request) {
        SelectSealDestRequest selectSealDestRequest =BeanUtils.copy(request,SelectSealDestRequest.class);
        InvokeResult<com.jd.bluedragon.common.dto.operation.workbench.send.response.ToSealDestAgg> result =jySendVehicleServiceTys.selectSealDest(selectSealDestRequest);
        if (ObjectHelper.isNotNull(result)){
            return convertResult(result,ToSealDestAgg.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 发货扫描
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.sendScan", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeWithMsgBoxResult<SendScanResp> sendScan(SendScanReq request) {
        SendScanRequest sendScanRequest =BeanUtils.copy(request,SendScanRequest.class);
        JdVerifyResponse<SendScanResponse> response = jySendVehicleServiceTys.sendScan(sendScanRequest);
        if (ObjectHelper.isNotNull(response)){
            return convertResultWithMsgBox(response,SendScanResp.class);
        }
        return new InvokeWithMsgBoxResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 根据运输任务bizId查询车的封签号列表
     *
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.listSealCodeByBizId", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<SealCodeResp> listSealCodeByBizId(SealCodeReq sealCodeReq) {
        com.jd.bluedragon.common.dto.seal.request.SealCodeReq req =BeanUtils.copy(sealCodeReq, com.jd.bluedragon.common.dto.seal.request.SealCodeReq.class);
        InvokeResult<com.jd.bluedragon.common.dto.seal.response.SealCodeResp> result= jySealVehicleService.listSealCodeByBizId(req);
        if (ObjectHelper.isNotNull(result)){
            return convertResult(result,SealCodeResp.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 查询流向任务封车数据详情
     *
     * @param sealVehicleInfoReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.getSealVehicleInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<SealVehicleInfoResp> getSealVehicleInfo(SealVehicleInfoReq sealVehicleInfoReq) {
        com.jd.bluedragon.common.dto.seal.request.SealVehicleInfoReq req =BeanUtils.copy(sealVehicleInfoReq, com.jd.bluedragon.common.dto.seal.request.SealVehicleInfoReq.class);
        InvokeResult<com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp> result= jySealVehicleService.getSealVehicleInfo(req);
        if (ObjectHelper.isNotNull(result)){
            return convertResult(result,SealVehicleInfoResp.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 根据运力编码查询运输信息
     *
     * @param transportReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.getTransportResourceByTransCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<TransportInfoDto> getTransportResourceByTransCode(TransportReq transportReq) {
       /* InvokeResult invokeResult =new InvokeResult();
        NewSealVehicleRequest param = new NewSealVehicleRequest();
        param.setSiteCode(transportReq.getCurrentOperate().getSiteCode());
        param.setSiteName(transportReq.getCurrentOperate().getSiteName());
        param.setUserCode(transportReq.getUser().getUserCode());
        param.setUserName(transportReq.getUser().getUserName());
        param.setTransportCode(transportReq.getTransportCode());
        RouteTypeResponse routeTypeResponse = newSealVehicleResource.getTransportCode(param);
        if (routeTypeResponse.getCode().equals(JdResponse.CODE_OK)) {
            invokeResult.confirmMessage(routeTypeResponse.getMessage());
        } else if (routeTypeResponse.getCode().equals(NewSealVehicleResponse.CODE_UNSEAL_CAR_OUT_CHECK) || routeTypeResponse.getCode().equals(NewSealVehicleResponse.CODE_TRANSPORT_RANGE_CHECK) || routeTypeResponse.getCode().equals(NewSealVehicleResponse.CODE_TRANSPORT_RANGE_ERROR)) {
            invokeResult.confirmMessage(routeTypeResponse.getMessage());
        } else {
            invokeResult.error(routeTypeResponse.getMessage());
        }
        com.jd.bluedragon.common.dto.blockcar.response.TransportInfoDto transportInfoDto = BeanUtils.copy(routeTypeResponse, com.jd.bluedragon.common.dto.blockcar.response.TransportInfoDto.class);
        invokeResult.setData(transportInfoDto);
        return invokeResult;*/
        return null;
    }

    /**
     * 校验运力编码和任务简码是否匹配
     *
     * @param CheckTransportCodeReq
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.checkTransportCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult checkTransportCode(CheckTransportCodeReq CheckTransportCodeReq) {
       /* NewSealVehicleRequest param = new NewSealVehicleRequest();
        param.setTransportCode(checkTransportCodeReq.getTransportCode());
        param.setTransWorkItemCode(checkTransportCodeReq.getTransWorkItemCode());
        TransWorkItemResponse workItemResponse = newSealVehicleResource.checkTransportCode(param);
        return new JdCResponse(workItemResponse.getCode(), workItemResponse.getMessage());*/
        return null;
    }

    /**
     * 根据任务简码查询运输任务相关信息--原pda调用接口逻辑
     *
     * @param GetVehicleNumberReq
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.getVehicleNumberByWorkItemCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<TransportResp> getVehicleNumberByWorkItemCode(GetVehicleNumberReq GetVehicleNumberReq) {
        return null;
    }

    /**
     * 根据任务简码查询任务详情
     *
     * @param getVehicleNumberReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.getTransWorkItemByWorkItemCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<TransportResp> getTransWorkItemByWorkItemCode(GetVehicleNumberReq getVehicleNumberReq) {
        com.jd.bluedragon.common.dto.seal.request.GetVehicleNumberReq req =BeanUtils.copy(getVehicleNumberReq, com.jd.bluedragon.common.dto.seal.request.GetVehicleNumberReq.class);
        InvokeResult<com.jd.bluedragon.common.dto.seal.response.TransportResp> result=jySealVehicleService.getTransWorkItemByWorkItemCode(req);
        if (ObjectHelper.isNotNull(result)){
            return convertResult(result,TransportResp.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 提交封车
     *
     * @param sealVehicleReq
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.sealVehicle", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult sealVehicle(SealVehicleReq sealVehicleReq) {
        com.jd.bluedragon.common.dto.seal.request.SealVehicleReq req =BeanUtils.copy(sealVehicleReq, com.jd.bluedragon.common.dto.seal.request.SealVehicleReq.class);
        InvokeResult result =jySealVehicleService.sealVehicle(req);
        if (ObjectHelper.isNotNull(result)){
            return convertResult(result,SealVehicleInfoResp.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 校验运力编码和发货批次的目的地是否一致
     *
     * @param ValidSendCodeReq
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.validateTranCodeAndSendCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult validateTranCodeAndSendCode(ValidSendCodeReq ValidSendCodeReq) {
       /* SealCarPreRequest sealCarPreRequest = BeanUtils.copy(validSendCodeReq,SealCarPreRequest.class);
        if (ObjectHelper.isEmpty(sealCarPreRequest.getSealCarType())){
            sealCarPreRequest.setSealCarType(SealCarTypeEnum.SEAL_BY_TASK.getType());
        }
        if (ObjectHelper.isEmpty(sealCarPreRequest.getSealCarSource())){
            sealCarPreRequest.setSealCarSource(SealCarSourceEnum.COMMON_SEAL_CAR.getCode());
        }
        NewSealVehicleResponse newSealVehicleResponse = newSealVehicleResource.newCheckTranCodeAndBatchCode(sealCarPreRequest);
        return new JdCResponse(newSealVehicleResponse.getCode(),newSealVehicleResponse.getMessage());*/
        return null;
    }

    /**
     * 获取车辆类型列表信息
     *
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.listVehicleType", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<List<VehicleSpecResp>> listVehicleType() {
        InvokeResult<List<com.jd.bluedragon.common.dto.send.response.VehicleSpecResp>> result= jySendVehicleServiceTys.listVehicleType();
        if (ObjectHelper.isNotNull(result)){
            return convertListResult(result,VehicleSpecResp.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 创建自建类型的运输车辆任务（主任务）
     *
     * @param createVehicleTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.createVehicleTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<CreateVehicleTaskResp> createVehicleTask(CreateVehicleTaskReq createVehicleTaskReq) {
        com.jd.bluedragon.common.dto.send.request.CreateVehicleTaskReq req =BeanUtils.copy(createVehicleTaskReq, com.jd.bluedragon.common.dto.send.request.CreateVehicleTaskReq.class);
        InvokeResult<com.jd.bluedragon.common.dto.send.response.CreateVehicleTaskResp> result=jySendVehicleServiceTys.createVehicleTask(req);
        if (ObjectHelper.isNotNull(result)){
            return convertResult(result,CreateVehicleTaskResp.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 删除自建类型的运输车辆任务（主任务）
     *
     * @param deleteVehicleTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.deleteVehicleTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult deleteVehicleTask(DeleteVehicleTaskReq deleteVehicleTaskReq) {
        com.jd.bluedragon.common.dto.send.request.DeleteVehicleTaskReq req =BeanUtils.copy(deleteVehicleTaskReq, com.jd.bluedragon.common.dto.send.request.DeleteVehicleTaskReq.class);
        InvokeResult result= jySendVehicleServiceTys.deleteVehicleTask(req);
        if (ObjectHelper.isNotNull(result)){
            return convertResult(result,null);
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 查询运输车辆任务列表
     *
     * @param vehicleTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.listVehicleTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<VehicleTaskResp> listVehicleTask(VehicleTaskReq vehicleTaskReq) {
        com.jd.bluedragon.common.dto.send.request.VehicleTaskReq req =BeanUtils.copy(vehicleTaskReq, com.jd.bluedragon.common.dto.send.request.VehicleTaskReq.class);
        InvokeResult<com.jd.bluedragon.common.dto.send.response.VehicleTaskResp> result= jySendVehicleServiceTys.listVehicleTask(req);
        if (ObjectHelper.isNotNull(result)){
            return convertResult(result,VehicleTaskResp.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 查询运输车辆任务列表：提供给任务迁移场景查询使用
     * 迁出时 扫包裹号定位包裹所在任务；迁入时 @1可扫包裹 @2也可录入站点id
     *
     * @param transferVehicleTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.listVehicleTaskSupportTransfer", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<VehicleTaskResp> listVehicleTaskSupportTransfer(TransferVehicleTaskReq transferVehicleTaskReq) {
        com.jd.bluedragon.common.dto.send.request.TransferVehicleTaskReq req =BeanUtils.copy(transferVehicleTaskReq, com.jd.bluedragon.common.dto.send.request.TransferVehicleTaskReq.class);
        InvokeResult<com.jd.bluedragon.common.dto.send.response.VehicleTaskResp> result= jySendVehicleServiceTys.listVehicleTaskSupportTransfer(req);
        if (ObjectHelper.isNotNull(result)){
            return convertResult(result,VehicleTaskResp.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 自建任务绑定-运输真实任务
     *
     * @param bindVehicleDetailTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.bindVehicleDetailTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult bindVehicleDetailTask(BindVehicleDetailTaskReq bindVehicleDetailTaskReq) {
        com.jd.bluedragon.common.dto.send.request.BindVehicleDetailTaskReq req =BeanUtils.copy(bindVehicleDetailTaskReq, com.jd.bluedragon.common.dto.send.request.BindVehicleDetailTaskReq.class);
        InvokeResult result=jySendVehicleServiceTys.bindVehicleDetailTask(req);
        if (ObjectHelper.isNotNull(result)){
            return convertResult(result,null);
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 迁移发货批次数据
     *
     * @param transferSendTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.transferSendTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult transferSendTask(TransferSendTaskReq transferSendTaskReq) {
        com.jd.bluedragon.common.dto.send.request.TransferSendTaskReq req =BeanUtils.copy(transferSendTaskReq, com.jd.bluedragon.common.dto.send.request.TransferSendTaskReq.class);
        InvokeResult result=jySendVehicleServiceTys.transferSendTask(req);
        if (ObjectHelper.isNotNull(result)){
            return convertResult(result,null);
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 取消发货
     *
     * @param cancelSendTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.cancelSendTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<CancelSendTaskResp> cancelSendTask(CancelSendTaskReq cancelSendTaskReq) {
        com.jd.bluedragon.common.dto.send.request.CancelSendTaskReq req  =BeanUtils.copy(cancelSendTaskReq, com.jd.bluedragon.common.dto.send.request.CancelSendTaskReq.class);
        InvokeResult<com.jd.bluedragon.common.dto.send.response.CancelSendTaskResp> result =jySendVehicleServiceTys.cancelSendTask(req);
        if (ObjectHelper.isNotNull(result)){
            return convertResult(result,CancelSendTaskResp.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    /**
     * 扫描包裹号、箱号 获取流向信息
     *
     * @param GetSendRouterInfoReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.getSendRouterInfoByScanCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<GetSendRouterInfoResp> getSendRouterInfoByScanCode(GetSendRouterInfoReq GetSendRouterInfoReq) {
        return null;
    }

    /**
     * 不齐处理提交
     *
     * @param IncompleteSendReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.incompleteSendSubmit", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<IncompleteSendResp> incompleteSendSubmit(IncompleteSendReq IncompleteSendReq) {
        return null;
    }

    private  <T> InvokeResult<T> convertResult(InvokeResult input,Class<T> tClass) {
       InvokeResult<T> output =new InvokeResult<>();
       output.setCode(input.getCode());
       output.setMessage(input.getMessage());
       if (ObjectHelper.isNotNull(input.getData())){
           T t =BeanUtils.copy(input.getData(),tClass);
           output.setData(t);
       }
       return output;
    }

    private  <T> InvokeResult<List<T>> convertListResult(InvokeResult input,Class<T> tClass) {
        InvokeResult<List<T>> output =new InvokeResult();
        output.setCode(input.getCode());
        output.setMessage(input.getMessage());
        if (ObjectHelper.isNotNull(input.getData())){
            List<T> t =BeanUtils.copy((List)input.getData(),tClass);
            output.setData(t);
        }
        return output;
    }

    private  <T> InvokeWithMsgBoxResult<T> convertResultWithMsgBox(JdVerifyResponse input,Class<T> tClass) {
        InvokeWithMsgBoxResult<T> output =new InvokeWithMsgBoxResult<>();
        output.setCode(input.getCode());
        output.setMessage(input.getMessage());
        output.setMsgBoxes(input.getMsgBoxes());
        if (ObjectHelper.isNotNull(input.getData())){
            T t =BeanUtils.copy(input.getData(),tClass);
            output.setData(t);
        }
        return output;
    }

}
