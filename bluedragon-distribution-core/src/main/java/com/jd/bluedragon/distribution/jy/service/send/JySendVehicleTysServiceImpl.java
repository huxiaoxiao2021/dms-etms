package com.jd.bluedragon.distribution.jy.service.send;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.BoxMsgResult;
import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendModeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehiclePhotoEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendAbnormalResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendScanResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.JdiSelectWSManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.InvokeWithMsgBoxResult;
import com.jd.bluedragon.distribution.jy.api.JySendVehicleTysService;
import com.jd.bluedragon.distribution.jy.dto.JyLabelOption;
import com.jd.bluedragon.distribution.jy.dto.JySelectOption;
import com.jd.bluedragon.distribution.jy.dto.send.*;
import com.jd.bluedragon.distribution.jy.enums.*;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.seal.JySealVehicleService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.utils.*;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.util.SiteSignTool;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.jd.bluedragon.Constants.BUSSINESS_TYPE_POSITIVE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/8/17
 * @Description: 拣运发货封车岗 转运专用服务
 * <p>
 * 与卸车岗思想保持一致，暂不增加租户概念
 */
@Service("jySendVehicleTysService")
@UnifiedExceptionProcess
public class JySendVehicleTysServiceImpl implements JySendVehicleTysService {

    private static final Logger logger = LoggerFactory.getLogger(JySendVehicleTysServiceImpl.class);

    @Resource(name = "jySendVehicleServiceTys")
    private JySendVehicleServiceTysImpl jySendVehicleServiceTys;
    @Autowired
    JySealVehicleService jySealVehicleService;
    @Autowired
    private NewSealVehicleService newsealVehicleService;
    @Autowired
    private JdiSelectWSManager jdiSelectWSManager;
    @Autowired
    DeliveryService deliveryService;
    @Autowired
    BaseMajorManager baseMajorManager;

    /**
     * 发货模式
     *
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.sendModeOptions", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.vehicleStatusOptions", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.lineTypeOptions", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.scanTypeOptions", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.fetchSendVehicleTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<SendVehicleTaskResp> fetchSendVehicleTask(SendVehicleTaskReq request) {
        InvokeResult<SendVehicleTaskResp> result = new InvokeResult<SendVehicleTaskResp>();
        try {
            SendVehicleTaskRequest param = new SendVehicleTaskRequest();
            BeanCopyUtil.copy(request, param);
            param.setUser(copyUser(request.getUser()));
            param.setCurrentOperate(copyCurrentOperate(request.getCurrentOperate()));
            InvokeResult<SendVehicleTaskResponse> invokeResult = jySendVehicleServiceTys.fetchSendVehicleTask(param);
            if (invokeResult != null) {
                result.setCode(invokeResult.getCode());
                result.setMessage(invokeResult.getMessage());
                if (invokeResult.getData() != null) {
                    String jsonStr = JSON.toJSONString(invokeResult.getData());
                    SendVehicleTaskResp sendVehicleTaskResp = JSON.parseObject(jsonStr, SendVehicleTaskResp.class);
                    result.setData(sendVehicleTaskResp);
                }
            } else {
                result.error();
                logger.error("JySendVehicleTysService.fetchSendVehicleTask error! invokeResult is null ,req:{}", JsonHelper.toJson(request));
            }
        } catch (Exception e) {
            logger.error("JySendVehicleTysService.fetchSendVehicleTask error! ,req:{}", JsonHelper.toJson(request));
            result.error();
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info("JySendVehicleTysService.fetchSendVehicleTask req:{} , resp:{}", JsonHelper.toJson(request), JsonHelper.toJson(result));
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
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.sendPhotoOptions", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.uploadPhoto", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> uploadPhoto(SendPhotoReq request) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        try {
            SendPhotoRequest param = new SendPhotoRequest();
            BeanCopyUtil.copy(request, param);
            param.setUser(copyUser(request.getUser()));
            param.setCurrentOperate(copyCurrentOperate(request.getCurrentOperate()));
            InvokeResult<Boolean> invokeResult = jySendVehicleServiceTys.uploadPhoto(param);
            if (invokeResult != null) {
                result.setCode(invokeResult.getCode());
                result.setMessage(invokeResult.getMessage());
                result.setData(invokeResult.getData());
            } else {
                result.error();
                logger.error("JySendVehicleTysService.uploadPhoto error! invokeResult is null ,req:{}", JsonHelper.toJson(request));
            }
        } catch (Exception e) {
            logger.error("JySendVehicleTysService.uploadPhoto error! ,req:{}", JsonHelper.toJson(request));
            result.error();
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info("JySendVehicleTysService.uploadPhoto req:{} , resp:{}", JsonHelper.toJson(request), JsonHelper.toJson(result));
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
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.sendVehicleInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<SendVehicleInfo> sendVehicleInfo(SendVehicleInfoReq request) {
        InvokeResult<SendVehicleInfo> result = new InvokeResult<SendVehicleInfo>();
        try {
            SendVehicleInfoRequest param = new SendVehicleInfoRequest();
            BeanCopyUtil.copy(request, param);
            param.setUser(copyUser(request.getUser()));
            param.setCurrentOperate(copyCurrentOperate(request.getCurrentOperate()));
            InvokeResult<com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleInfo> invokeResult = jySendVehicleServiceTys.sendVehicleInfo(param);
            if (invokeResult != null) {
                result.setCode(invokeResult.getCode());
                result.setMessage(invokeResult.getMessage());
                if (invokeResult.getData() != null) {
                    SendVehicleInfo sendVehicleTaskResp = new SendVehicleInfo();
                    BeanCopyUtil.copy(invokeResult.getData(), sendVehicleTaskResp);
                    result.setData(sendVehicleTaskResp);
                }
            } else {
                result.error();
                logger.error("JySendVehicleTysService.sendVehicleInfo error! invokeResult is null ,req:{}", JsonHelper.toJson(request));
            }
        } catch (Exception e) {
            logger.error("JySendVehicleTysService.sendVehicleInfo error! ,req:{}", JsonHelper.toJson(request));
            result.error();
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info("JySendVehicleTysService.sendVehicleInfo req:{} , resp:{}", JsonHelper.toJson(request), JsonHelper.toJson(result));
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
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.sendDestDetail", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<SendDestDetail>> sendDestDetail(SendDetailReq request) {
        InvokeResult<List<SendDestDetail>> result = new InvokeResult<List<SendDestDetail>>();
        try {
            SendDetailRequest param = new SendDetailRequest();
            BeanCopyUtil.copy(request, param);
            param.setUser(copyUser(request.getUser()));
            param.setCurrentOperate(copyCurrentOperate(request.getCurrentOperate()));
            InvokeResult<List<com.jd.bluedragon.common.dto.operation.workbench.send.response.SendDestDetail>> invokeResult = jySendVehicleServiceTys.sendDestDetail(param);
            if (invokeResult != null) {
                result.setCode(invokeResult.getCode());
                result.setMessage(invokeResult.getMessage());
                List<SendDestDetail> sendDestDetails = new ArrayList<>();
                if (!CollectionUtils.isEmpty(invokeResult.getData())) {
                    for (com.jd.bluedragon.common.dto.operation.workbench.send.response.SendDestDetail _sendDestDetail : invokeResult.getData()) {
                        SendDestDetail sendDestDetail = new SendDestDetail();
                        BeanCopyUtil.copy(_sendDestDetail, sendDestDetail);
                        sendDestDetails.add(sendDestDetail);
                    }
                }
                result.setData(sendDestDetails);
            } else {
                result.error();
                logger.error("JySendVehicleTysService.sendDestDetail error! invokeResult is null ,req:{}", JsonHelper.toJson(request));
            }
        } catch (Exception e) {
            logger.error("JySendVehicleTysService.sendDestDetail error! ,req:{}", JsonHelper.toJson(request));
            result.error();
        } finally {
            if (logger.isInfoEnabled()) {
                logger.info("JySendVehicleTysService.sendDestDetail req:{} , resp:{}", JsonHelper.toJson(request), JsonHelper.toJson(result));
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
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.loadProgress", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<SendVehicleProgress> loadProgress(SendVehicleProgressReq request) {
        SendVehicleProgressRequest sendVehicleProgressRequest = BeanUtils.copy(request, SendVehicleProgressRequest.class);
        if (sendVehicleProgressRequest != null) {
            sendVehicleProgressRequest.setUser(copyUser(request.getUser()));
            sendVehicleProgressRequest.setCurrentOperate(copyCurrentOperate(request.getCurrentOperate()));
        }
        InvokeResult<com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleProgress> result = jySendVehicleServiceTys.loadProgress(sendVehicleProgressRequest);
        if (ObjectHelper.isNotNull(result)) {
            return convertResult(result, SendVehicleProgress.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
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
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.checkSendVehicleNormalStatus", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<SendAbnormalResp> checkSendVehicleNormalStatus(SendAbnormalReq request) {
        SendAbnormalRequest sendAbnormalRequest = BeanUtils.copy(request, SendAbnormalRequest.class);
        if (sendAbnormalRequest != null) {
            sendAbnormalRequest.setUser(copyUser(request.getUser()));
            sendAbnormalRequest.setCurrentOperate(copyCurrentOperate(request.getCurrentOperate()));
        }
        InvokeResult<SendAbnormalResponse> result = jySendVehicleServiceTys.checkSendVehicleNormalStatus(sendAbnormalRequest);
        if (ObjectHelper.isNotNull(result)) {
            InvokeResult<SendAbnormalResp> response = new InvokeResult<>();
            response.setCode(result.getCode());
            response.setMessage(result.getMessage());
            if (result.getData() != null) {
                SendAbnormalResp sendAbnormalResp = new SendAbnormalResp();
                if (result.getData().getAbnormalType() != null) {
                    sendAbnormalResp.setAbnormalType(JySendAbnormalEnum.valueOf(result.getData().getAbnormalType().name()));
                }
                sendAbnormalResp.setNormalFlag(result.getData().getNormalFlag());
                response.setData(sendAbnormalResp);
            }
            return response;
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }


    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.listSendWaybillDetail", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<SendWaybillStatisticsResp> listSendWaybillDetail(QuerySendWaybillReq querySendWaybillReq) {
        SendWaybillStatisticsResp sendWaybillStatisticsResp =new SendWaybillStatisticsResp();
        if (ObjectHelper.isNotNull(querySendWaybillReq.getExpFlag()) && querySendWaybillReq.getExpFlag()){
            List<SendExcepScanDto> sendExcepScanDtoList=jySendVehicleServiceTys.listExcepScanType(new ExcepScanQueryDto(querySendWaybillReq.getSendVehicleBizId()));
            sendWaybillStatisticsResp.setExcepScanDtoList(sendExcepScanDtoList);
        }
        else  {
            return new InvokeResult(NOT_SUPPORT_TYPE_QUERY_CODE,NOT_SUPPORT_TYPE_QUERY_MESSAGE);
        }
        QueryExcepWaybillDto queryExcepWaybillDto = assembleQueryExcepWaybillDto(querySendWaybillReq);
        ExcepWaybillDto waybillDto = jySendVehicleServiceTys.queryExcepScanWaybill(queryExcepWaybillDto);
        assembleRespWaybillData(sendWaybillStatisticsResp, waybillDto);
        return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, sendWaybillStatisticsResp);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.listSendPackageDetail", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<SendPackageStatisticsResp> listSendPackageDetail(QuerySendPackageReq querySendPackageReq) {
        SendPackageStatisticsResp sendPackageStatisticsResp =new SendPackageStatisticsResp();
        QueryExcepPackageDto queryExcepPackageDto = assembleQueryExcepPackageDto(querySendPackageReq);
        ExcepPackageDto packageDto = jySendVehicleServiceTys.queryExcepPackageUnderWaybill(queryExcepPackageDto);
        assembleRespPackageData(sendPackageStatisticsResp, packageDto);
        return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, sendPackageStatisticsResp);
    }

    private void assembleRespPackageData(SendPackageStatisticsResp sendPackageStatisticsResp, ExcepPackageDto packageDto) {
        if (ObjectHelper.isNotNull(packageDto) && ObjectHelper.isNotNull(packageDto.getSendPackageDtoList())) {
            List<SendPackage> packList = new ArrayList();
            for (SendPackageDto sendPackageDto : packageDto.getSendPackageDtoList()) {
                SendPackage sendScanPack = new SendPackage();
                sendScanPack.setPackageCode(sendPackageDto.getPackageCode());
                sendScanPack.setTags(resolveBarCodeTag(sendPackageDto.getExcepScanLabelEnum()));
                packList.add(sendScanPack);
            }
            sendPackageStatisticsResp.setPackageList(packList);
        }
    }

    private void assembleRespWaybillData(SendWaybillStatisticsResp sendWaybillStatisticsResp, ExcepWaybillDto waybillDto) {
        if (ObjectHelper.isNotNull(waybillDto) && ObjectHelper.isNotNull(waybillDto.getSendWaybillDtoList())) {
            sendWaybillStatisticsResp.setWaybillDtoList(waybillDto.getSendWaybillDtoList());
        }
    }

    private void checkSendAbnormalPackReq(SendAbnormalPackReq request) {
        if (!ObjectHelper.isNotNull(request.getSendVehicleBizId())) {
            throw new JyBizException("参数有误：sendVehicleBizId为空！");
        }
        if (!ObjectHelper.isNotNull(request.getExcepScanTypeEnum())) {
            throw new JyBizException("参数有误：excepScanTypeEnum为空！");
        }
        if (!ObjectHelper.isNotNull(request.getPageNumber())) {
            throw new JyBizException("参数有误：pageNumber为空！");
        }
        if (!ObjectHelper.isNotNull(request.getPageSize())) {
            throw new JyBizException("参数有误：pageSize为空！");
        }
    }


    private QueryExcepWaybillDto assembleQueryExcepWaybillDto(QuerySendWaybillReq request) {
        QueryExcepWaybillDto queryExcepWaybillDto = new QueryExcepWaybillDto();
        queryExcepWaybillDto.setSendVehicleBizId(request.getSendVehicleBizId());
        queryExcepWaybillDto.setExcepScanTypeEnum(ExcepScanTypeEnum.getExcepScanTypeEnum(request.getExpType()));
        queryExcepWaybillDto.setPageNo(request.getPageNo());
        queryExcepWaybillDto.setPageSize(request.getPageSize());
        return queryExcepWaybillDto;
    }

    private QueryExcepPackageDto assembleQueryExcepPackageDto(QuerySendPackageReq request) {
        QueryExcepPackageDto queryExcepPackageDto = new QueryExcepPackageDto();
        queryExcepPackageDto.setSendVehicleBizId(request.getSendVehicleBizId());
        queryExcepPackageDto.setExcepScanTypeEnum(ExcepScanTypeEnum.getExcepScanTypeEnum(request.getExpType()));
        queryExcepPackageDto.setPageNo(request.getPageNo());
        queryExcepPackageDto.setPageSize(request.getPageSize());
        queryExcepPackageDto.setWaybillCode(request.getWaybillCode());
        return queryExcepPackageDto;
    }

    private List<JyLabelOption> resolveBarCodeTag(ExcepScanLabelEnum excepScanLabelEnum) {
        List<JyLabelOption> tags = new ArrayList<>();
        tags.add(new JyLabelOption(excepScanLabelEnum.getCode(), excepScanLabelEnum.getName()));
        return tags;
    }

    /**
     * 选择封车流向
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.selectSealDest", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<ToSealDestAgg> selectSealDest(SelectSealDestReq request) {
        SelectSealDestRequest selectSealDestRequest = BeanUtils.copy(request, SelectSealDestRequest.class);
        if (selectSealDestRequest != null) {
            selectSealDestRequest.setUser(copyUser(request.getUser()));
            selectSealDestRequest.setCurrentOperate(copyCurrentOperate(request.getCurrentOperate()));
        }
        InvokeResult<com.jd.bluedragon.common.dto.operation.workbench.send.response.ToSealDestAgg> result = jySendVehicleServiceTys.selectSealDest(selectSealDestRequest);
        if (ObjectHelper.isNotNull(result)) {
            InvokeResult<ToSealDestAgg> response = new InvokeResult<>();
            response.setCode(result.getCode());
            response.setMessage(result.getMessage());
            if (result.getData() != null) {
                String jsonStr = JSON.toJSONString(result.getData());
                ToSealDestAgg toSealDestAgg = JSON.parseObject(jsonStr, ToSealDestAgg.class);
                response.setData(toSealDestAgg);
            }
            return response;
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }

    /**
     * 发货扫描
     *
     * @param request
     * @return
     */
    @Override
    @BoxMsgResult
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.sendScan", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeWithMsgBoxResult<SendScanResp> sendScan(SendScanReq request) {
        SendScanRequest sendScanRequest = BeanUtils.copy(request, SendScanRequest.class);
        if (sendScanRequest != null) {
            sendScanRequest.setUser(copyUser(request.getUser()));
            sendScanRequest.setCurrentOperate(copyCurrentOperate(request.getCurrentOperate()));
        }
        JdVerifyResponse<SendScanResponse> response = jySendVehicleServiceTys.sendScan(sendScanRequest);
        if (ObjectHelper.isNotNull(response)) {
            return convertResultWithMsgBox(response, SendScanResp.class);
        }
        return new InvokeWithMsgBoxResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }

    /**
     * 根据运输任务bizId查询车的封签号列表
     *
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.listSealCodeByBizId", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<SealCodeResp> listSealCodeByBizId(SealCodeReq sealCodeReq) {
        com.jd.bluedragon.common.dto.seal.request.SealCodeReq req = BeanUtils.copy(sealCodeReq, com.jd.bluedragon.common.dto.seal.request.SealCodeReq.class);
        if (req != null) {
            req.setUser(copyUser(sealCodeReq.getUser()));
            req.setCurrentOperate(copyCurrentOperate(sealCodeReq.getCurrentOperate()));
        }
        InvokeResult<com.jd.bluedragon.common.dto.seal.response.SealCodeResp> result = jySealVehicleService.listSealCodeByBizId(req);
        if (ObjectHelper.isNotNull(result)) {
            return convertResult(result, SealCodeResp.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }

    /**
     * 查询流向任务封车数据详情
     *
     * @param sealVehicleInfoReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.getSealVehicleInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<SealVehicleInfoResp> getSealVehicleInfo(SealVehicleInfoReq sealVehicleInfoReq) {
        com.jd.bluedragon.common.dto.seal.request.SealVehicleInfoReq req = BeanUtils.copy(sealVehicleInfoReq, com.jd.bluedragon.common.dto.seal.request.SealVehicleInfoReq.class);
        if (req != null) {
            req.setUser(copyUser(sealVehicleInfoReq.getUser()));
            req.setCurrentOperate(copyCurrentOperate(sealVehicleInfoReq.getCurrentOperate()));
        }
        InvokeResult<com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp> result = jySealVehicleService.getSealVehicleInfo(req);
        if (ObjectHelper.isNotNull(result)) {
            return convertResult(result, SealVehicleInfoResp.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }

    /**
     * 根据运力编码查询运输信息
     *
     * @param transportReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.getTransportResourceByTransCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<TransportInfoDto> getTransportResourceByTransCode(TransportReq transportReq) {
        if (StringUtils.isEmpty(transportReq.getTransportCode()) || !NumberHelper.isPositiveNumber(transportReq.getCurrentOperate().getSiteCode())) {
            return new InvokeResult(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
        }
        try {
            com.jd.tms.basic.dto.CommonDto<TransportResourceDto> vtsDto = newsealVehicleService.getTransportResourceByTransCode(transportReq.getTransportCode());
            if (vtsDto == null) {
                return new InvokeResult(GET_TRANSPORT_RESOURCE_CODE, GET_TRANSPORT_RESOURCE_MESSAGE);
            }
            if (Constants.RESULT_SUCCESS == vtsDto.getCode()) {
                TransportResourceDto vtrd = vtsDto.getData();
                if (vtrd != null) {
                    RouteTypeResponse routeTypeResponse = newsealVehicleService.checkTransportCode(vtrd, transportReq.getCurrentOperate().getSiteCode());
                    if (routeTypeResponse.getCode().equals(JdResponse.CODE_OK)) {
                        TransportInfoDto transportInfoDto = BeanUtils.copy(routeTypeResponse, TransportInfoDto.class);
                        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, transportInfoDto);
                    } else {
                        return new InvokeResult(routeTypeResponse.getCode(), routeTypeResponse.getMessage());
                    }
                } else {
                    return new InvokeResult(GET_TRANSPORT_RESOURCE_CODE, GET_TRANSPORT_RESOURCE_MESSAGE);
                }
            } else if (Constants.RESULT_WARN == vtsDto.getCode()) {
                //查询运力信息接口返回警告，给出前台提示
                return new InvokeResult(SERVER_ERROR_CODE, vtsDto.getMessage());
            } else {
                //服务出错或者出异常，打日志
                logger.warn("查询运力信息出错,出错原因:{}", vtsDto.getMessage());
            }
        } catch (Exception e) {
            logger.error("通过运力编码获取基础资料信息异常：{}", JsonHelper.toJson(transportReq), e);
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }

    /**
     * 校验运力编码和任务简码是否匹配
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.checkTransportCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult checkTransportCode(CheckTransportCodeReq request) {
        if (request == null || StringUtils.isEmpty(request.getTransWorkItemCode()) || StringUtils.isEmpty(request.getTransportCode())) {
            if (logger.isWarnEnabled()) {
                logger.warn("JySendVehicleTysService.checkTransportCode --> 传入参数非法:{}", JsonHelper.toJson(request));
            }
            return new InvokeResult(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
        }
        com.jd.tms.jdi.dto.CommonDto<String> commonDto = jdiSelectWSManager.checkTransportCode(request.getTransWorkItemCode(), request.getTransportCode());
        if (commonDto != null) {
            if (Constants.RESULT_SUCCESS == commonDto.getCode()) {
                return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
            } else {
                return new InvokeResult(NewSealVehicleResponse.CODE_EXCUTE_ERROR, "[" + commonDto.getCode() + ":" + commonDto.getMessage() + "]");
            }
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }

    /**
     * 根据任务简码查询运输任务相关信息--原pda调用接口逻辑
     *
     * @param GetVehicleNumberReq
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.getVehicleNumberByWorkItemCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.getTransWorkItemByWorkItemCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<TransportResp> getTransWorkItemByWorkItemCode(GetVehicleNumberReq getVehicleNumberReq) {
        com.jd.bluedragon.common.dto.seal.request.GetVehicleNumberReq req = BeanUtils.copy(getVehicleNumberReq, com.jd.bluedragon.common.dto.seal.request.GetVehicleNumberReq.class);
        if (req != null) {
            req.setUser(copyUser(getVehicleNumberReq.getUser()));
            req.setCurrentOperate(copyCurrentOperate(getVehicleNumberReq.getCurrentOperate()));
        }
        InvokeResult<com.jd.bluedragon.common.dto.seal.response.TransportResp> result = jySealVehicleService.getTransWorkItemByWorkItemCode(req);
        if (ObjectHelper.isNotNull(result)) {
            return convertResult(result, TransportResp.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }

    /**
     * 提交封车
     *
     * @param sealVehicleReq
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.sealVehicle", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult sealVehicle(SealVehicleReq sealVehicleReq) {
        com.jd.bluedragon.common.dto.seal.request.SealVehicleReq req = BeanUtils.copy(sealVehicleReq, com.jd.bluedragon.common.dto.seal.request.SealVehicleReq.class);
        if (req != null) {
            req.setUser(copyUser(sealVehicleReq.getUser()));
            req.setCurrentOperate(copyCurrentOperate(sealVehicleReq.getCurrentOperate()));
        }
        InvokeResult result = jySealVehicleService.sealVehicle(req);
        if (ObjectHelper.isNotNull(result)) {
            return convertResult(result, SealVehicleInfoResp.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }

    /**
     * 校验运力编码和发货批次的目的地是否一致
     *
     * @param validSendCodeReq
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.validateTranCodeAndSendCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult validateTranCodeAndSendCode(ValidSendCodeReq validSendCodeReq) {
        checkValidSendCodeReq(validSendCodeReq);
        InvokeResult invokeResult = new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
        newsealVehicleService.checkBatchCode(invokeResult, validSendCodeReq.getSendCode());
        if (RESULT_SUCCESS_CODE == invokeResult.getCode()) {
            com.jd.tms.basic.dto.CommonDto<TransportResourceDto> commonDto = newsealVehicleService.getTransportResourceByTransCode(validSendCodeReq.getTransportCode());
            if (commonDto == null) {
                invokeResult.setCode(SERVER_ERROR_CODE);
                invokeResult.setMessage("查询运力信息结果为空:" + validSendCodeReq.getSendCode());
                return invokeResult;
            }
            if (commonDto.getData() != null && Constants.RESULT_SUCCESS == commonDto.getCode()) {
                Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(validSendCodeReq.getSendCode());
                Integer endNodeId = commonDto.getData().getEndNodeId();
                if (receiveSiteCode.equals(endNodeId)) {
                    invokeResult.setCode(JdResponse.CODE_OK);
                    invokeResult.setMessage(JdResponse.MESSAGE_OK);
                } else {
                    //不分传摆和运力都去校验目的地类型是中转场的时候 跳过目的地不一致逻辑
                    BaseStaffSiteOrgDto endNodeSite = baseMajorManager.getBaseSiteBySiteId(endNodeId);
                    if (endNodeSite != null && SiteSignTool.supportTemporaryTransfer(endNodeSite.getSiteSign())) {
                        invokeResult.setCode(RESULT_SUCCESS_CODE);
                        invokeResult.setMessage(RESULT_SUCCESS_MESSAGE);
                        return invokeResult;
                    }
                    invokeResult.setCode(SENDCODE_TRANSPOST_NOT_UNIFIED_CODE);
                    invokeResult.setMessage(SENDCODE_TRANSPOST_NOT_UNIFIED_MESSAGE);
                }
            } else if (Constants.RESULT_WARN == commonDto.getCode()) {
                invokeResult.setCode(SERVER_ERROR_CODE);
                invokeResult.setMessage(commonDto.getMessage());
            } else {
                invokeResult.setCode(SERVER_ERROR_CODE);
                invokeResult.setMessage("查询运力信息出错！");
                logger.warn("根据运力编码：【{}】查询运力信息出错,出错原因:{}", validSendCodeReq.getTransportCode(), commonDto.getMessage());
            }
        }
        return invokeResult;
    }

    private void checkValidSendCodeReq(ValidSendCodeReq validSendCodeReq) {
        if (!(Constants.SEAL_TYPE_TRANSPORT.equals(validSendCodeReq.getSealCarType()) || Constants.SEAL_TYPE_TASK.equals(validSendCodeReq.getSealCarType()))) {
            throw new JyBizException("不支持该封车类型！");
        }
        if (!ObjectHelper.isNotNull(validSendCodeReq.getSendCode())) {
            throw new JyBizException("参数错误：批次号为空！");
        }
        if (!ObjectHelper.isNotNull(validSendCodeReq.getTransportCode())) {
            throw new JyBizException("参数错误：运力编码为空！");
        }
    }

    /**
     * 获取车辆类型列表信息
     *
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.listVehicleType", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<VehicleSpecResp>> listVehicleType() {
        InvokeResult<List<com.jd.bluedragon.common.dto.send.response.VehicleSpecResp>> result = jySendVehicleServiceTys.listVehicleType();
        if (ObjectHelper.isNotNull(result)) {
            InvokeResult<List<VehicleSpecResp>> response = new InvokeResult<>();
            response.setCode(result.getCode());
            response.setMessage(result.getMessage());
            if (result.getData() != null) {
                String jsonStr = JSON.toJSONString(result.getData());
                List<VehicleSpecResp> list = JSON.parseObject(jsonStr, new TypeReference<List<VehicleSpecResp>>(){});
                response.setData(list);
            }
            return response;
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }

    /**
     * 创建自建类型的运输车辆任务（主任务）
     *
     * @param createVehicleTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.createVehicleTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<CreateVehicleTaskResp> createVehicleTask(CreateVehicleTaskReq createVehicleTaskReq) {
        com.jd.bluedragon.common.dto.send.request.CreateVehicleTaskReq req = BeanUtils.copy(createVehicleTaskReq, com.jd.bluedragon.common.dto.send.request.CreateVehicleTaskReq.class);
        if (req != null) {
            req.setUser(copyUser(createVehicleTaskReq.getUser()));
            req.setCurrentOperate(copyCurrentOperate(createVehicleTaskReq.getCurrentOperate()));
        }
        InvokeResult<com.jd.bluedragon.common.dto.send.response.CreateVehicleTaskResp> result = jySendVehicleServiceTys.createVehicleTask(req);
        if (ObjectHelper.isNotNull(result)) {
            return convertResult(result, CreateVehicleTaskResp.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }

    /**
     * 删除自建类型的运输车辆任务（主任务）
     *
     * @param deleteVehicleTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.deleteVehicleTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult deleteVehicleTask(DeleteVehicleTaskReq deleteVehicleTaskReq) {
        com.jd.bluedragon.common.dto.send.request.DeleteVehicleTaskReq req = BeanUtils.copy(deleteVehicleTaskReq, com.jd.bluedragon.common.dto.send.request.DeleteVehicleTaskReq.class);
        if (req != null) {
            req.setUser(copyUser(deleteVehicleTaskReq.getUser()));
            req.setCurrentOperate(copyCurrentOperate(deleteVehicleTaskReq.getCurrentOperate()));
        }
        InvokeResult result = jySendVehicleServiceTys.deleteVehicleTask(req);
        if (ObjectHelper.isNotNull(result)) {
            return convertResult(result, null);
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }

    /**
     * 查询运输车辆任务列表
     *
     * @param vehicleTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.listVehicleTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<VehicleTaskResp> listVehicleTask(VehicleTaskReq vehicleTaskReq) {
        com.jd.bluedragon.common.dto.send.request.VehicleTaskReq req = BeanUtils.copy(vehicleTaskReq, com.jd.bluedragon.common.dto.send.request.VehicleTaskReq.class);
        if (req != null) {
            req.setUser(copyUser(vehicleTaskReq.getUser()));
            req.setCurrentOperate(copyCurrentOperate(vehicleTaskReq.getCurrentOperate()));
        }
        InvokeResult<com.jd.bluedragon.common.dto.send.response.VehicleTaskResp> result = jySendVehicleServiceTys.listVehicleTask(req);
        if (ObjectHelper.isNotNull(result)) {
            InvokeResult<VehicleTaskResp> response = new InvokeResult<>();
            response.setCode(result.getCode());
            response.setMessage(result.getMessage());
            if (result.getData() != null) {
                String jsonStr = JSON.toJSONString(result.getData());
                VehicleTaskResp vehicleTaskResp = JSON.parseObject(jsonStr, VehicleTaskResp.class);
                response.setData(vehicleTaskResp);
            }
            return response;
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }

    /**
     * 查询运输车辆任务列表：提供给任务迁移场景查询使用
     * 迁出时 扫包裹号定位包裹所在任务；迁入时 @1可扫包裹 @2也可录入站点id
     *
     * @param transferVehicleTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.listVehicleTaskSupportTransfer", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<VehicleTaskResp> listVehicleTaskSupportTransfer(TransferVehicleTaskReq transferVehicleTaskReq) {
        com.jd.bluedragon.common.dto.send.request.TransferVehicleTaskReq req = BeanUtils.copy(transferVehicleTaskReq, com.jd.bluedragon.common.dto.send.request.TransferVehicleTaskReq.class);
        if (req != null) {
            req.setUser(copyUser(transferVehicleTaskReq.getUser()));
            req.setCurrentOperate(copyCurrentOperate(transferVehicleTaskReq.getCurrentOperate()));
        }
        InvokeResult<com.jd.bluedragon.common.dto.send.response.VehicleTaskResp> result = jySendVehicleServiceTys.listVehicleTaskSupportTransfer(req);
        if (ObjectHelper.isNotNull(result)) {
            InvokeResult<VehicleTaskResp> response = new InvokeResult<>();
            response.setCode(result.getCode());
            response.setMessage(result.getMessage());
            if (result.getData() != null) {
                String jsonStr = JSON.toJSONString(result.getData());
                VehicleTaskResp vehicleTaskResp = JSON.parseObject(jsonStr, VehicleTaskResp.class);
                response.setData(vehicleTaskResp);
            }
            return response;
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }

    /**
     * 自建任务绑定-运输真实任务
     *
     * @param bindVehicleDetailTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.bindVehicleDetailTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult bindVehicleDetailTask(BindVehicleDetailTaskReq bindVehicleDetailTaskReq) {
        com.jd.bluedragon.common.dto.send.request.BindVehicleDetailTaskReq req = BeanUtils.copy(bindVehicleDetailTaskReq, com.jd.bluedragon.common.dto.send.request.BindVehicleDetailTaskReq.class);
        if (req != null) {
            req.setUser(copyUser(bindVehicleDetailTaskReq.getUser()));
            req.setCurrentOperate(copyCurrentOperate(bindVehicleDetailTaskReq.getCurrentOperate()));
        }
        InvokeResult result = jySendVehicleServiceTys.bindVehicleDetailTask(req);
        if (ObjectHelper.isNotNull(result)) {
            return convertResult(result, null);
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }

    /**
     * 迁移发货批次数据
     *
     * @param transferSendTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.transferSendTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult transferSendTask(TransferSendTaskReq transferSendTaskReq) {
        com.jd.bluedragon.common.dto.send.request.TransferSendTaskReq req = BeanUtils.copy(transferSendTaskReq, com.jd.bluedragon.common.dto.send.request.TransferSendTaskReq.class);
        if (req != null) {
            req.setUser(copyUser(transferSendTaskReq.getUser()));
            req.setCurrentOperate(copyCurrentOperate(transferSendTaskReq.getCurrentOperate()));
        }
        InvokeResult result = jySendVehicleServiceTys.transferSendTask(req);
        if (ObjectHelper.isNotNull(result)) {
            return convertResult(result, null);
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }

    /**
     * 取消发货
     *
     * @param cancelSendTaskReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.cancelSendTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<CancelSendTaskResp> cancelSendTask(CancelSendTaskReq cancelSendTaskReq) {
        com.jd.bluedragon.common.dto.send.request.CancelSendTaskReq req = BeanUtils.copy(cancelSendTaskReq, com.jd.bluedragon.common.dto.send.request.CancelSendTaskReq.class);
        if (req != null) {
            req.setUser(copyUser(cancelSendTaskReq.getUser()));
            req.setCurrentOperate(copyCurrentOperate(cancelSendTaskReq.getCurrentOperate()));
        }
        InvokeResult<com.jd.bluedragon.common.dto.send.response.CancelSendTaskResp> result = jySendVehicleServiceTys.cancelSendTask(req);
        if (ObjectHelper.isNotNull(result)) {
            return convertResult(result, CancelSendTaskResp.class);
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }

    /**
     * 扫描包裹号、箱号 获取流向信息
     *
     * @param GetSendRouterInfoReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.getSendRouterInfoByScanCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<GetSendRouterInfoResp> getSendRouterInfoByScanCode(GetSendRouterInfoReq GetSendRouterInfoReq) {
        return null;
    }

    /**
     * 不齐处理提交
     *
     * @param incompleteSendReq
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.JySendVehicleTysService.incompleteSendSubmit", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<IncompleteSendResp> incompleteSendSubmit(IncompleteSendReq incompleteSendReq) {
        checkIncompleteSendReq(incompleteSendReq);
        List<SendM> sendMList = new ArrayList();
        if (incompleteSendReq.getCheckAllFlag() != null && incompleteSendReq.getCheckAllFlag()) {
            //查询任务下所以的不齐-扫过的包裹
            QueryExcepWaybillDto queryExcepWaybillDto = new QueryExcepWaybillDto();
            queryExcepWaybillDto.setSendVehicleBizId(incompleteSendReq.getSendVehicleBizId());
            queryExcepWaybillDto.setExcepScanTypeEnum(ExcepScanTypeEnum.INCOMPELETE_DEAL);
            queryExcepWaybillDto.setPageNo(Constants.DEFAULT_PAGE_NO);
            queryExcepWaybillDto.setPageSize(Constants.DEFAULT_PAGE_SIZE_LIMIT);
            ExcepWaybillDto waybillDto = jySendVehicleServiceTys.queryExcepScanWaybill(queryExcepWaybillDto);
            if (ObjectHelper.isNotNull(waybillDto) && ObjectHelper.isNotNull(waybillDto.getSendWaybillDtoList())) {
                for (SendWaybillDto sendWaybillDto : waybillDto.getSendWaybillDtoList()) {
                    SendM sendM = generateSendM(sendWaybillDto.getWaybillCode(), incompleteSendReq);
                    sendMList.add(sendM);
                }
            }
        } else if (ObjectHelper.isNotNull(incompleteSendReq.getPackList())) {
            //按照包裹进行取消发货
            for (String packageCode : incompleteSendReq.getPackList()) {
                SendM sendM = generateSendM(packageCode, incompleteSendReq);
                sendMList.add(sendM);
            }
        }
        if (ObjectHelper.isNotNull(sendMList) && sendMList.size() > 0) {
            for (SendM sendM : sendMList) {
                ThreeDeliveryResponse tDResponse = deliveryService.dellCancelDeliveryMessageWithServerTime(sendM, true);
                if (!(ObjectHelper.isNotNull(tDResponse) && JdCResponse.CODE_SUCCESS.equals(tDResponse.getCode()))) {
                    return new InvokeResult(tDResponse.getCode(), tDResponse.getMessage());
                }
            }
            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
        }
        return new InvokeResult(NO_FOUND_INCOMPELETE_DATA_CODE, NO_FOUND_INCOMPELETE_DATA_MESSAGE);
    }

    private SendM generateSendM(String boxCode, IncompleteSendReq incompleteSendReq) {
        SendM sendM = new SendM();
        sendM.setBoxCode(boxCode);
        sendM.setCreateSiteCode(incompleteSendReq.getCurrentOperate().getSiteCode());
        sendM.setUpdaterUser(incompleteSendReq.getUser().getUserName());
        sendM.setUpdateUserCode(incompleteSendReq.getUser().getUserCode());
        sendM.setSendType(BUSSINESS_TYPE_POSITIVE);
        Date now = new Date();
        sendM.setOperateTime(now);
        sendM.setUpdateTime(now);
        sendM.setYn(0);
        return sendM;
    }

    private void checkIncompleteSendReq(IncompleteSendReq incompleteSendReq) {
        if (!ObjectHelper.isNotNull(incompleteSendReq.getSendVehicleBizId())) {
            throw new JyBizException("参数错误：sendVehicleBizId不能为空！");
        }
    }

    private <T> InvokeResult<T> convertResult(InvokeResult input, Class<T> tClass) {
        InvokeResult<T> output = new InvokeResult<>();
        output.setCode(input.getCode());
        output.setMessage(input.getMessage());
        if (ObjectHelper.isNotNull(input.getData())) {
            T t = BeanUtils.copy(input.getData(), tClass);
            output.setData(t);
        }
        return output;
    }

    private <T> InvokeResult<List<T>> convertListResult(InvokeResult input, Class<T> tClass) {
        InvokeResult<List<T>> output = new InvokeResult();
        output.setCode(input.getCode());
        output.setMessage(input.getMessage());
        if (ObjectHelper.isNotNull(input.getData())) {
            List<T> t = BeanUtils.copy((List) input.getData(), tClass);
            output.setData(t);
        }
        return output;
    }

    private <T> InvokeWithMsgBoxResult<T> convertResultWithMsgBox(JdVerifyResponse input, Class<T> tClass) {
        InvokeWithMsgBoxResult<T> output = new InvokeWithMsgBoxResult<>();
        output.setCode(input.getCode());
        output.setMessage(input.getMessage());
        output.setMsgBoxes(input.getMsgBoxes());
        if (ObjectHelper.isNotNull(input.getData())) {
            T t = BeanUtils.copy(input.getData(), tClass);
            output.setData(t);
        }
        return output;
    }

    private com.jd.bluedragon.common.dto.base.request.User copyUser(com.jd.bluedragon.distribution.jy.dto.User userParam) {
        com.jd.bluedragon.common.dto.base.request.User user = new com.jd.bluedragon.common.dto.base.request.User();
        user.setUserCode(userParam.getUserCode());
        user.setUserName(userParam.getUserName());
        user.setUserErp(userParam.getUserErp());
        return user;
    }

    private com.jd.bluedragon.common.dto.base.request.CurrentOperate copyCurrentOperate(com.jd.bluedragon.distribution.jy.dto.CurrentOperate currentOperateParam) {
        com.jd.bluedragon.common.dto.base.request.CurrentOperate currentOperate = new com.jd.bluedragon.common.dto.base.request.CurrentOperate();
        currentOperate.setSiteCode(currentOperateParam.getSiteCode());
        currentOperate.setDmsCode(currentOperateParam.getDmsCode());
        currentOperate.setSiteName(currentOperateParam.getSiteName());
        currentOperate.setOperateTime(currentOperateParam.getOperateTime());
        currentOperate.setOrgId(currentOperateParam.getOrgId());
        currentOperate.setOrgName(currentOperateParam.getOrgName());
        return currentOperate;
    }

}
