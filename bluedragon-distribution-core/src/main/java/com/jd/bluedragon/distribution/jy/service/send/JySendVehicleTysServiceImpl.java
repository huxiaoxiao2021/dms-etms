package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendModeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehiclePhotoEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendDetailRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendPhotoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleInfoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.InvokeWithMsgBoxResult;
import com.jd.bluedragon.distribution.jy.api.JySendVehicleTysService;
import com.jd.bluedragon.distribution.jy.dto.JySelectOption;
import com.jd.bluedragon.distribution.jy.dto.send.*;
import com.jd.bluedragon.distribution.jy.enums.JyLineTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JySendLineTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JySendVehicleStatusEnum;
import com.jd.bluedragon.utils.BeanCopyUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private IJySendVehicleService jySendVehicleService;

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

            InvokeResult<SendVehicleTaskResponse> invokeResult = jySendVehicleService.fetchSendVehicleTask(param);
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

            InvokeResult<Boolean> invokeResult = jySendVehicleService.uploadPhoto(param);
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
                    jySendVehicleService.sendVehicleInfo(param);
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
                    jySendVehicleService.sendDestDetail(param);
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
        return null;
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
        return null;
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
        return null;
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
        return null;
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
        return null;
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
        return null;
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
        return null;
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
        return null;
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
        return null;
    }

    /**
     * 根据运输任务bizId查询车的封签号列表
     *
     * @param SealCodeReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.listSealCodeByBizId", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<SealCodeResp> listSealCodeByBizId(SealCodeReq SealCodeReq) {
        return null;
    }

    /**
     * 查询流向任务封车数据详情
     *
     * @param SealVehicleInfoReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.getSealVehicleInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<SealVehicleInfoResp> getSealVehicleInfo(SealVehicleInfoReq SealVehicleInfoReq) {
        return null;
    }

    /**
     * 根据运力编码查询运输信息
     *
     * @param TransportReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.getTransportResourceByTransCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<TransportInfoDto> getTransportResourceByTransCode(TransportReq TransportReq) {
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
     * @param GetVehicleNumberReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.getTransWorkItemByWorkItemCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<TransportResp> getTransWorkItemByWorkItemCode(GetVehicleNumberReq GetVehicleNumberReq) {
        return null;
    }

    /**
     * 提交封车
     *
     * @param SealVehicleReq
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.sealVehicle", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult sealVehicle(SealVehicleReq SealVehicleReq) {
        return null;
    }

    /**
     * 校验运力编码和发货批次的目的地是否一致
     *
     * @param ValidSendCodeReq
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.validateTranCodeAndSendCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult validateTranCodeAndSendCode(ValidSendCodeReq ValidSendCodeReq) {
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
        return null;
    }

    /**
     * 创建自建类型的运输车辆任务（主任务）
     *
     * @param CreateVehicleTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.createVehicleTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<CreateVehicleTaskResp> createVehicleTask(CreateVehicleTaskReq CreateVehicleTaskReq) {
        return null;
    }

    /**
     * 删除自建类型的运输车辆任务（主任务）
     *
     * @param DeleteVehicleTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.deleteVehicleTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult deleteVehicleTask(DeleteVehicleTaskReq DeleteVehicleTaskReq) {
        return null;
    }

    /**
     * 查询运输车辆任务列表
     *
     * @param VehicleTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.listVehicleTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<VehicleTaskResp> listVehicleTask(VehicleTaskReq VehicleTaskReq) {
        return null;
    }

    /**
     * 查询运输车辆任务列表：提供给任务迁移场景查询使用
     * 迁出时 扫包裹号定位包裹所在任务；迁入时 @1可扫包裹 @2也可录入站点id
     *
     * @param TransferVehicleTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.listVehicleTaskSupportTransfer", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<VehicleTaskResp> listVehicleTaskSupportTransfer(TransferVehicleTaskReq TransferVehicleTaskReq) {
        return null;
    }

    /**
     * 自建任务绑定-运输真实任务
     *
     * @param BindVehicleDetailTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.bindVehicleDetailTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult bindVehicleDetailTask(BindVehicleDetailTaskReq BindVehicleDetailTaskReq) {
        return null;
    }

    /**
     * 迁移发货批次数据
     *
     * @param TransferSendTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.transferSendTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult transferSendTask(TransferSendTaskReq TransferSendTaskReq) {
        return null;
    }

    /**
     * 取消发货
     *
     * @param CancelSendTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.cancelSendTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public InvokeResult<CancelSendTaskResp> cancelSendTask(CancelSendTaskReq CancelSendTaskReq) {
        return null;
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
}
