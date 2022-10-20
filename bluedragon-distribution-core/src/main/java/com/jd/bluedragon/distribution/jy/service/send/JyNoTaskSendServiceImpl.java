package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.MSCodeMapping;
import com.jd.bluedragon.common.dto.send.request.*;
import com.jd.bluedragon.common.dto.send.response.*;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.delivery.IDeliveryOperationService;
import com.jd.bluedragon.distribution.jy.dto.send.JySendCodeDto;
import com.jd.bluedragon.distribution.jy.dto.send.VehicleSendRelationDto;
import com.jd.bluedragon.distribution.jy.enums.CancelSendTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.SendTaskExcepLabelEnum;
import com.jd.bluedragon.distribution.jy.enums.TransferLogTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.manager.JyTransportManager;
import com.jd.bluedragon.distribution.jy.send.JySendCodeEntity;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.service.transfer.JySendTransferService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.enums.SendStatusEnum;
import com.jd.bluedragon.utils.*;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jd.tms.basic.dto.BasicVehicleTypeDto;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;
import static com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum.JY_APP;
import static com.jd.bluedragon.utils.TimeUtils.yyyyMMdd;

@Service
@Slf4j
public class JyNoTaskSendServiceImpl implements JyNoTaskSendService {
    @Autowired
    JyTransportManager jyTransportManager;
    @Autowired
    JySendTransferService jySendTransferService;
    @Autowired
    DeliveryService deliveryService;
    @Autowired
    GroupBoardManager groupBoardManager;
    @Autowired
    SendMService sendMService;
    @Autowired
    JyVehicleSendRelationService jyVehicleSendRelationService;
    @Autowired
    SendCodeService sendCodeService;
    @Autowired
    JyBizTaskSendVehicleDetailService jyBizTaskSendVehicleDetailService;
    @Autowired
    private IDeliveryOperationService deliveryOperationService;
    @Autowired
    JyBizTaskSendVehicleService jyBizTaskSendVehicleService;
    @Autowired
    @Qualifier("redisClientCache")
    protected Cluster redisClientCache;
    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;
    @Autowired
    @Qualifier("redisJySendBizIdSequenceGen")
    private JimdbSequenceGen redisJyBizIdSequenceGen;
    @Autowired
    IJySendVehicleService iJySendVehicleService;
    @Autowired
    private SendDetailService sendDetailService;
    @Autowired
    SortingService sortingService;
    @Autowired
    private IJySendVehicleService jySendVehicleService;
    @Autowired
    BaseMajorManager baseMajorManager;
    @Autowired
    JySendTransferLogService jySendTransferLogService;
    @Autowired
    private IJySendService jySendService;
    @Autowired
    private NewSealVehicleService newsealVehicleService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyNoTaskSendServiceImpl.listVehicleType", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<VehicleSpecResp>> listVehicleType() {
        CommonDto<List<BasicVehicleTypeDto>> rs = jyTransportManager.getVehicleTypeList();
        if (null != rs && rs.getCode() == Constants.RESULT_SUCCESS) {
            //按照车长做groupBy
            Map<String, List<VehicleTypeDto>> groupByVehicleLength = new HashMap<>();
            for (BasicVehicleTypeDto basicVehicleTypeDto : rs.getData()) {
                String vehicleLength = basicVehicleTypeDto.getVehicleLength();
                if (ObjectHelper.isNotNull(vehicleLength)) {
                    VehicleTypeDto vehicleTypeDto = BeanUtils.copy(basicVehicleTypeDto, VehicleTypeDto.class);
                    final BigDecimal vehicleLengthVal = new BigDecimal(vehicleLength);
                    final BigDecimal vehicleLengthGroupVal = vehicleLengthVal.divide(new BigDecimal(100), 1, RoundingMode.DOWN);
                    if (groupByVehicleLength.containsKey(vehicleLengthGroupVal.toString())) {
                        groupByVehicleLength.get(vehicleLengthGroupVal.toString()).add(vehicleTypeDto);
                    } else {
                        List<VehicleTypeDto> vehicleTypeDtoList = new ArrayList<>();
                        vehicleTypeDtoList.add(vehicleTypeDto);
                        groupByVehicleLength.put(vehicleLengthGroupVal.toString(), vehicleTypeDtoList);
                    }
                }
            }
            //封装树形结构响应体
            List<VehicleSpecResp> vehicleSpecRespList = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("###.0");
            for (Map.Entry<String, List<VehicleTypeDto>> entry : groupByVehicleLength.entrySet()) {
                String key = entry.getKey();
                List<VehicleTypeDto> value = entry.getValue();
                VehicleSpecResp vehicleSpecResp = new VehicleSpecResp();
                vehicleSpecResp.setVehicleLength(new BigDecimal(key).multiply(new BigDecimal(10)).intValue());
                vehicleSpecResp.setName(new BigDecimal(key) + "米");
                vehicleSpecResp.setVehicleTypeDtoList(value);
                vehicleSpecRespList.add(vehicleSpecResp);
            }
            Collections.sort(vehicleSpecRespList, new VehicleTypeComparator());
            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, vehicleSpecRespList);
        }
        return new InvokeResult(RESULT_NODATA_GETCARTYPE_CODE, RESULT_NODATA_GETCARTYPE_MESSAGE);
    }

    class VehicleTypeComparator implements Comparator<VehicleSpecResp> {
        @Override
        public int compare(VehicleSpecResp o1, VehicleSpecResp o2) {
            return o1.getVehicleLength() - o2.getVehicleLength();
        }
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyNoTaskSendServiceImpl.createVehicleTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<CreateVehicleTaskResp> createVehicleTask(CreateVehicleTaskReq createVehicleTaskReq) {
        JyBizTaskSendVehicleEntity jyBizTaskSendVehicleEntity = initJyBizTaskSendVehicle(createVehicleTaskReq);
        jyBizTaskSendVehicleService.saveSendVehicleTask(jyBizTaskSendVehicleEntity);
        CreateVehicleTaskResp createVehicleTaskResp = new CreateVehicleTaskResp();
        createVehicleTaskResp.setBizId(jyBizTaskSendVehicleEntity.getBizId());
        createVehicleTaskResp.setBizNo(jyBizTaskSendVehicleEntity.getBizNo());
        createVehicleTaskResp.setTaskName("自建" + jyBizTaskSendVehicleEntity.getBizNo());
        createVehicleTaskResp.setCreateUserErp(createVehicleTaskReq.getUser().getUserErp());
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, createVehicleTaskResp);
    }

    private JyBizTaskSendVehicleEntity initJyBizTaskSendVehicle(CreateVehicleTaskReq createVehicleTaskReq) {
        JyBizTaskSendVehicleEntity entity = new JyBizTaskSendVehicleEntity();
        entity.setBizId(genMainTaskBizId());
        String bizNo = genSendVehicleTaskBizNo(createVehicleTaskReq);
        entity.setBizNo(bizNo);
        entity.setTaskName("自建" + bizNo);
        entity.setStartSiteId(Long.valueOf(createVehicleTaskReq.getCurrentOperate().getSiteCode()));
        entity.setManualCreatedFlag(1);
        entity.setVehicleType(createVehicleTaskReq.getVehicleType());
        entity.setVehicleTypeName(createVehicleTaskReq.getVehicleTypeName());
        entity.setCreateUserErp(createVehicleTaskReq.getUser().getUserErp());
        entity.setCreateUserName(createVehicleTaskReq.getUser().getUserName());
        entity.setYn(0);
        Date now = new Date();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        return entity;
    }

    private String genSendVehicleTaskBizNo(CreateVehicleTaskReq createVehicleTaskReq) {
        String bizNoKey = "bizNo:" + createVehicleTaskReq.getCurrentOperate().getSiteCode() + ":" + TimeUtils.date2string(new Date(), yyyyMMdd + ":");
        long bizNo = 0;
        if (!ObjectHelper.isNotNull(redisClientCache.get(bizNoKey))) {
            redisClientCache.set(bizNoKey, "0", 24 * 60, TimeUnit.MINUTES, false);
        }
        try {
            bizNo = redisClientCache.incr(bizNoKey);
        } catch (Exception e) {
            return "";
        }
        return String.valueOf(bizNo);
    }

    private String genMainTaskBizId() {
        String ownerKey = String.format(JyBizTaskSendVehicleEntity.BIZ_PREFIX_NOTASK, DateHelper.formatDate(new Date(), DateHelper.DATE_FORMATE_yyMMdd));
        return ownerKey + StringHelper.padZero(redisJyBizIdSequenceGen.gen(ownerKey));
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyNoTaskSendServiceImpl.deleteVehicleTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public InvokeResult deleteVehicleTask(DeleteVehicleTaskReq deleteVehicleTaskReq) {
        log.info("删除自建任务,deleteVehicleTaskReq:{}", JsonHelper.toJson(deleteVehicleTaskReq));
        JyBizTaskSendVehicleEntity task = jyBizTaskSendVehicleService.findByBizId(deleteVehicleTaskReq.getBizId());
        if (task.hasBeenBindedOrDeleted()) {
            return new InvokeResult(NO_RE_DETELE_TASK_CODE, NO_RE_DETELE_TASK_MESSAGE);
        }
        //删除主任务
        JyBizTaskSendVehicleEntity entity = new JyBizTaskSendVehicleEntity();
        entity.setBizId(deleteVehicleTaskReq.getBizId());
        entity.setYn(0);
        Date now = new Date();
        entity.setUpdateTime(now);
        entity.setUpdateUserErp(deleteVehicleTaskReq.getUser().getUserErp());
        entity.setUpdateUserName(deleteVehicleTaskReq.getUser().getUserName());
        jyBizTaskSendVehicleService.updateSendVehicleTask(entity);
        //删除子任务
        JyBizTaskSendVehicleDetailEntity detailEntity = new JyBizTaskSendVehicleDetailEntity();
        detailEntity.setSendVehicleBizId(deleteVehicleTaskReq.getBizId());
        detailEntity.setYn(0);
        detailEntity.setUpdateTime(now);
        detailEntity.setUpdateUserErp(deleteVehicleTaskReq.getUser().getUserErp());
        detailEntity.setUpdateUserName(deleteVehicleTaskReq.getUser().getUserName());
        jyBizTaskSendVehicleDetailService.updateDateilTaskByVehicleBizId(detailEntity);
        //删除任务-发货绑定关系+取消发货
        List<String> sendCodeList = jyVehicleSendRelationService.querySendCodesByVehicleBizId(deleteVehicleTaskReq.getBizId());
        if (ObjectHelper.isNotNull(sendCodeList) && sendCodeList.size() > 0) {
            JySendCodeDto dto = new JySendCodeDto();
            dto.setSendVehicleBizId(deleteVehicleTaskReq.getBizId());
            dto.setUpdateUserErp(deleteVehicleTaskReq.getUser().getUserErp());
            dto.setUpdateUserName(deleteVehicleTaskReq.getUser().getUserName());
            jyVehicleSendRelationService.deleteVehicleSendRelationByVehicleBizId(dto);

            for (String sendCode : sendCodeList) {
                SendM sendM = new SendM();
                sendM.setSendCode(sendCode);
                sendM.setCreateSiteCode(deleteVehicleTaskReq.getCurrentOperate().getSiteCode());
                sendM.setUpdateTime(new Date());
                sendM.setUpdaterUser(deleteVehicleTaskReq.getUser().getUserName());
                sendM.setUpdateUserCode(deleteVehicleTaskReq.getUser().getUserCode());
                //如果已封车的批次不触发取消发货
                if (!newsealVehicleService.newCheckSendCodeSealed(sendCode, new StringBuffer())) {
                    ThreeDeliveryResponse tDResponse = deliveryService.dellCancelDeliveryMessageWithServerTime(sendM, true);
                    if (!tDResponse.getCode().equals(RESULT_SUCCESS_CODE)) {
                        return new InvokeResult(tDResponse.getCode(), tDResponse.getMessage());
                    }
                }

            }
        }
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyNoTaskSendServiceImpl.listVehicleTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<VehicleTaskResp> listVehicleTask(VehicleTaskReq vehicleTaskReq) {
        return jySendVehicleService.fetchSendTaskForBinding(vehicleTaskReq);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyNoTaskSendServiceImpl.listVehicleTaskSupportTransfer", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<VehicleTaskResp> listVehicleTaskSupportTransfer(TransferVehicleTaskReq transferVehicleTaskReq) {
        if (ObjectHelper.isNotNull(transferVehicleTaskReq.getBarCode())){
            return jySendVehicleService.fetchSendTaskForTransferV2(transferVehicleTaskReq);
        }
        return jySendVehicleService.fetchSendTaskForTransfer(transferVehicleTaskReq);
    }


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyNoTaskSendServiceImpl.bindVehicleDetailTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public InvokeResult bindVehicleDetailTask(BindVehicleDetailTaskReq bindVehicleDetailTaskReq) {
        log.info("自建任务绑定运输任务,bindVehicleDetailTaskReq:{}", JsonHelper.toJson(bindVehicleDetailTaskReq));
        JyBizTaskSendVehicleEntity task = jyBizTaskSendVehicleService.findByBizId(bindVehicleDetailTaskReq.getFromSendVehicleBizId());
        if (task.hasBeenBindedOrDeleted()) {
            return new InvokeResult(NO_RE_BIND_TASK_CODE, NO_RE_BIND_TASK_MESSAGE);
        }
        //更新任务与发货批次的关联关系
        List<String> sendCodeList = jyVehicleSendRelationService.querySendCodesByVehicleBizId(bindVehicleDetailTaskReq.getFromSendVehicleBizId());
        if (ObjectHelper.isNotNull(sendCodeList) && sendCodeList.size() > 0) {
            JyBizTaskSendVehicleDetailEntity queryFromDetailTaskParams = new JyBizTaskSendVehicleDetailEntity();
            queryFromDetailTaskParams.setSendVehicleBizId(bindVehicleDetailTaskReq.getFromSendVehicleBizId());
            queryFromDetailTaskParams.setStartSiteId(Long.valueOf(bindVehicleDetailTaskReq.getCurrentOperate().getSiteCode()));
            JyBizTaskSendVehicleDetailEntity fromSvdTask = jyBizTaskSendVehicleDetailService.findSendDetail(queryFromDetailTaskParams);
            if (!ObjectHelper.isNotNull(fromSvdTask)) {
                return new InvokeResult(DETAIL_TASK_NO_FOUND_BY_MAIN_TASK_CODE, DETAIL_TASK_NO_FOUND_BY_MAIN_TASK_MESSAGE);
            }
            if (jySendVehicleService.checkIfSealed(fromSvdTask)) {
                return new InvokeResult(FORBID_BIND_FOR_SEALED_DETAIL_CODE, FORBID_BIND_FOR_SEALED_DETAIL_MESSAGE);
            }

            JyBizTaskSendVehicleDetailEntity queryToDetailTaskParams = new JyBizTaskSendVehicleDetailEntity();
            queryToDetailTaskParams.setSendVehicleBizId(bindVehicleDetailTaskReq.getToSendVehicleBizId());
            queryToDetailTaskParams.setStartSiteId(Long.valueOf(bindVehicleDetailTaskReq.getCurrentOperate().getSiteCode()));
            queryToDetailTaskParams.setEndSiteId(fromSvdTask.getEndSiteId());
            List<JyBizTaskSendVehicleDetailEntity> taskSendDetails = jyBizTaskSendVehicleDetailService.findEffectiveSendVehicleDetail(queryToDetailTaskParams);
            JyBizTaskSendVehicleDetailEntity toSvdTask =jySendVehicleService.pickUpOneUnSealedDetail(taskSendDetails,fromSvdTask.getEndSiteId());
            if (!ObjectHelper.isNotNull(toSvdTask)) {
                return new InvokeResult(FORBID_BIND_TO_SEALED_DETAIL_CODE, FORBID_BIND_TO_SEALED_DETAIL_MESSAGE);
            }
            VehicleSendRelationDto dto = BeanUtils.copy(bindVehicleDetailTaskReq, VehicleSendRelationDto.class);
            dto.setFromSendVehicleDetailBizId(fromSvdTask.getBizId());
            dto.setToSendVehicleDetailBizId(toSvdTask.getBizId());
            dto.setSendCodes(sendCodeList);
            dto.setUpdateUserErp(bindVehicleDetailTaskReq.getUser().getUserErp());
            dto.setUpdateUserName(bindVehicleDetailTaskReq.getUser().getUserName());
            dto.setCreateSiteId(Long.valueOf(bindVehicleDetailTaskReq.getCurrentOperate().getSiteCode()));
            dto.setSameWayFlag(true);
            dto.setBindFlag(true);
            dto.setSource(TransferLogTypeEnum.SAME_WAY_BIND.getCode());
            jyVehicleSendRelationService.updateVehicleSendRelation(dto);
            jySendTransferLogService.saveTransferLog(dto);
            jySendService.updateTransferProperBySendCode(dto);

            //更新流向任务和主任的状态-为发货状态
            JyBizTaskSendVehicleEntity toSvTask = new JyBizTaskSendVehicleEntity();
            toSvTask.setBizId(dto.getToSendVehicleBizId());
            toSvTask.setVehicleStatus(JyBizTaskSendStatusEnum.SENDING.getCode());
            toSvTask.setPreVehicleStatus(JyBizTaskSendStatusEnum.TO_SEND.getCode());
            jyBizTaskSendVehicleService.updateBizTaskSendStatus(toSvTask);

            JyBizTaskSendVehicleDetailEntity toSvDetailTask = new JyBizTaskSendVehicleDetailEntity();
            toSvDetailTask.setBizId(dto.getToSendVehicleDetailBizId());
            toSvDetailTask.setVehicleStatus(JyBizTaskSendStatusEnum.SENDING.getCode());
            toSvDetailTask.setPreVehicleStatus(JyBizTaskSendStatusEnum.TO_SEND.getCode());
            jyBizTaskSendVehicleDetailService.updateBizTaskSendDetailStatus(toSvDetailTask);

            //删除自建主任务
            JyBizTaskSendVehicleEntity fromSvTask = new JyBizTaskSendVehicleEntity();
            fromSvTask.setBizId(bindVehicleDetailTaskReq.getFromSendVehicleBizId());
            fromSvTask.setYn(Constants.YN_NO);
            fromSvTask.setBindFlag(Constants.YN_YES);
            Date now = new Date();
            fromSvTask.setUpdateTime(now);
            fromSvTask.setUpdateUserErp(bindVehicleDetailTaskReq.getUser().getUserErp());
            fromSvTask.setUpdateUserName(bindVehicleDetailTaskReq.getUser().getUserName());
            jyBizTaskSendVehicleService.updateSendVehicleTask(fromSvTask);
            //删除自建子任务
            JyBizTaskSendVehicleDetailEntity fromSvDetailTask = new JyBizTaskSendVehicleDetailEntity();
            fromSvDetailTask.setSendVehicleBizId(bindVehicleDetailTaskReq.getFromSendVehicleBizId());
            fromSvDetailTask.setYn(Constants.YN_NO);
            fromSvDetailTask.setUpdateTime(now);
            fromSvDetailTask.setUpdateUserErp(bindVehicleDetailTaskReq.getUser().getUserErp());
            fromSvDetailTask.setUpdateUserName(bindVehicleDetailTaskReq.getUser().getUserName());
            jyBizTaskSendVehicleDetailService.updateDateilTaskByVehicleBizId(fromSvDetailTask);

            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
        }
        return new InvokeResult(NO_SEND_DATA_UNDER_TASK_CODE, NO_SEND_DATA_UNDER_TASK_MESSAGE);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyNoTaskSendServiceImpl.transferSendTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public InvokeResult transferSendTask(TransferSendTaskReq transferSendTaskReq) {
        log.info("任务迁移,transferSendTaskReq:{}", JsonHelper.toJson(transferSendTaskReq));
        //查询要迁移的批次信息-sendCodes
        JyBizTaskSendVehicleDetailEntity fromSvd = jyBizTaskSendVehicleDetailService.findByBizId(transferSendTaskReq.getFromSendVehicleDetailBizId());
        if (jySendVehicleService.checkIfSealed(fromSvd)) {
            return new InvokeResult(FORBID_TRANS_FRROM_SEALED_DETAIL_CODE, FORBID_TRANS_FRROM_SEALED_DETAIL_MESSAGE);
        }
        JyBizTaskSendVehicleDetailEntity toSvd = jyBizTaskSendVehicleDetailService.findByBizId(transferSendTaskReq.getToSendVehicleDetailBizId());
        if (jySendVehicleService.checkIfSealed(toSvd)) {
            return new InvokeResult(FORBID_TRANS_TO_SEALED_DETAIL_CODE, FORBID_TRANS_TO_SEALED_DETAIL_MESSAGE);
        }
        List<String> sendCodeList = (!transferSendTaskReq.getTotalTransFlag() && transferSendTaskReq.getSendCodeList() != null) ?
                transferSendTaskReq.getSendCodeList() : jyVehicleSendRelationService.querySendCodesByVehicleDetailBizId(transferSendTaskReq.getFromSendVehicleDetailBizId());
        if (ObjectHelper.isNotNull(sendCodeList) && sendCodeList.size() > 0) {
            sendCodeList = filterEmptySendCode(transferSendTaskReq.getCurrentOperate().getSiteCode(), sendCodeList);
            if (sendCodeList.size() <= 0) {
                return new InvokeResult(FORBID_TRANS_FOR_EMPTY_BATCH_CODE, FORBID_TRANS_FOR_EMPTY_BATCH_MESSAGE);
            }
            if (log.isInfoEnabled()){
                log.info("迁出任务{}待迁移批次：{}",transferSendTaskReq.getFromSendVehicleDetailBizId(),sendCodeList);
            }
            VehicleSendRelationDto dto = BeanUtils.copy(transferSendTaskReq, VehicleSendRelationDto.class);
            dto.setSendCodes(sendCodeList);
            dto.setUpdateUserErp(transferSendTaskReq.getUser().getUserErp());
            dto.setUpdateUserName(transferSendTaskReq.getUser().getUserName());
            dto.setUpdateUserCode(transferSendTaskReq.getUser().getUserCode());
            dto.setCreateSiteId(Long.valueOf(transferSendTaskReq.getCurrentOperate().getSiteCode()));

            if (ObjectHelper.isTrue(transferSendTaskReq.getSameWayFlag())) {
                //同流向--直接变更绑定关系
                dto.setSource(TransferLogTypeEnum.SAME_WAY_TRANSFER.getCode());
                jyVehicleSendRelationService.updateVehicleSendRelation(dto);
                jySendTransferLogService.saveTransferLog(dto);
                jySendService.updateTransferProperBySendCode(dto);

            } else {
                //删除原绑定关系
                jyVehicleSendRelationService.deleteVehicleSendRelation(dto);
                //增加新流向绑定关系
                String newSendCode = generateSendCode(toSvd, transferSendTaskReq.getUser().getUserErp());
                JySendCodeEntity jySendCodeEntity = initJySendCodeEntity(transferSendTaskReq, newSendCode);
                jyVehicleSendRelationService.add(jySendCodeEntity);
                dto.setNewSendCode(newSendCode);
                jySendTransferLogService.saveTransferLog(dto);
                jySendService.updateTransferProperBySendCode(dto);
                //生成迁移任务，异步执行迁移逻辑
                for (String sendCode : sendCodeList) {
                    List<SendM> sendMList = sendMService.selectBySiteAndSendCode(transferSendTaskReq.getCurrentOperate().getSiteCode(), sendCode);
                    deliveryOperationService.asyncHandleTransfer(sendMList, dto);
                }
            }
            JyBizTaskSendVehicleEntity toSvTask = new JyBizTaskSendVehicleEntity();
            toSvTask.setBizId(dto.getToSendVehicleBizId());
            toSvTask.setVehicleStatus(JyBizTaskSendStatusEnum.SENDING.getCode());
            toSvTask.setPreVehicleStatus(JyBizTaskSendStatusEnum.TO_SEND.getCode());
            jyBizTaskSendVehicleService.updateBizTaskSendStatus(toSvTask);

            JyBizTaskSendVehicleDetailEntity toSvDetailTask = new JyBizTaskSendVehicleDetailEntity();
            toSvDetailTask.setBizId(dto.getToSendVehicleDetailBizId());
            toSvDetailTask.setVehicleStatus(JyBizTaskSendStatusEnum.SENDING.getCode());
            toSvDetailTask.setPreVehicleStatus(JyBizTaskSendStatusEnum.TO_SEND.getCode());
            jyBizTaskSendVehicleDetailService.updateBizTaskSendDetailStatus(toSvDetailTask);

            //迁移完毕，判断迁出的流向任务是否有被打cancel的label，有执行作废
            doCancelForLabelCanceldTask(transferSendTaskReq.getFromSendVehicleDetailBizId());

            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
        }
        return new InvokeResult(NO_SEND_DATA_UNDER_TASK_CODE, NO_SEND_DATA_UNDER_TASK_MESSAGE);
    }

    private List<String> filterEmptySendCode(int siteCode, List<String> sendCodeList) {
        List<String> notEmptyList = new ArrayList<>();
        for (String sendCode : sendCodeList) {
            List<SendM> sendMList = sendMService.selectBySiteAndSendCode(siteCode, sendCode);
            if (ObjectHelper.isNotNull(sendMList) && sendMList.size() > 0) {
                notEmptyList.add(sendCode);
            }
        }
        return notEmptyList;
    }

    private void doCancelForLabelCanceldTask(String fromSendVehicleDetailBizId) {
        JyBizTaskSendVehicleDetailEntity detailEntity = jyBizTaskSendVehicleDetailService.findByBizId(fromSendVehicleDetailBizId);
        if (ObjectHelper.isNotNull(detailEntity)
                && ObjectHelper.isNotNull(detailEntity.getExcepLabel())
                && SendTaskExcepLabelEnum.CANCEL.getCode().equals(detailEntity.getExcepLabel())) {
            try {
                jyBizTaskSendVehicleDetailService.cancelDetailTaskAndMainTask(detailEntity);
            } catch (Exception e) {
                log.error("对打标cancel的流向任务执行取消异常", e);
            }
        }
    }

    private JySendCodeEntity initJySendCodeEntity(TransferSendTaskReq transferSendTaskReq, String sendCode) {
        JySendCodeEntity jySendCodeEntity = new JySendCodeEntity();
        jySendCodeEntity.setSendCode(sendCode);
        jySendCodeEntity.setSendVehicleBizId(transferSendTaskReq.getToSendVehicleBizId());
        jySendCodeEntity.setSendDetailBizId(transferSendTaskReq.getToSendVehicleDetailBizId());
        jySendCodeEntity.setSource(TransferLogTypeEnum.NOT_SAME_WAY_TRANSFER_NEW_BATCH.getCode());
        Date now = new Date();
        jySendCodeEntity.setCreateTime(now);
        jySendCodeEntity.setUpdateTime(now);
        jySendCodeEntity.setCreateUserErp(transferSendTaskReq.getUser().getUserErp());
        jySendCodeEntity.setCreateUserName(transferSendTaskReq.getUser().getUserName());
        return jySendCodeEntity;
    }

    private String generateSendCode(JyBizTaskSendVehicleDetailEntity sendVehicleDetail, String createUser) {
        Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, String> attributeKeyEnumObjectMap = new HashMap<>();
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code, String.valueOf(sendVehicleDetail.getStartSiteId()));
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code, String.valueOf(sendVehicleDetail.getEndSiteId()));
        //attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.is_fresh, "false");
        return sendCodeService.createSendCode(attributeKeyEnumObjectMap, JY_APP, createUser);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyNoTaskSendServiceImpl.cancelSendTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<CancelSendTaskResp> cancelSendTask(CancelSendTaskReq request) {
        log.info("jy取消发货，按{}进行取消,扫描号码：{}", CancelSendTypeEnum.getReportTypeName(request.getType()), request.getCode());
        validateCancelReq(request);
        SendM sendM = toSendM(request);
        CancelSendTaskResp cancelSendTaskResp = new CancelSendTaskResp();
        cancelSendTaskResp.setCancelCode(request.getCode());

        //按运单
        if (CancelSendTypeEnum.WAYBILL_CODE.getCode().equals(request.getType())) {
            String wayBillCode = WaybillUtil.isPackageCode(request.getCode()) ? WaybillUtil.getWaybillCode(request.getCode()) : request.getCode();
            sendM.setBoxCode(wayBillCode);
            cancelSendTaskResp.setCancelCode(wayBillCode);
        }
        //按箱
        if (CancelSendTypeEnum.BOX_CODE.getCode().equals(request.getType())) {
            String boxCode = request.getCode();
            if (WaybillUtil.isPackageCode(request.getCode())) {
                //寻找箱号
                List<Sorting> sortingList = sortingService.findByPackageCode(request.getCurrentOperate().getSiteCode(), request.getCode());
                if (!ObjectHelper.isNotNull(sortingList) || sortingList.size() != 1) {
                    log.info("按箱号取消，根据包裹号{}查询箱号数据异常", request.getCode());
                    return new InvokeResult(BOX_NO_FOUND_BY_PACKAGE_CODE, BOX_NO_FOUND_BY_PACKAGE_MESSAGE);
                }
                boxCode = sortingList.get(0).getBoxCode();
            }
            sendM.setBoxCode(boxCode);
            cancelSendTaskResp.setCancelCode(boxCode);
        }
        //按板
        if (CancelSendTypeEnum.BOARD_NO.getCode().equals(request.getType())) {
            String boardCode = request.getCode();
            if (WaybillUtil.isPackageCode(request.getCode()) || BusinessUtil.isBoxcode(request.getCode())) {
                Response<Board> boardResponse = groupBoardManager.getBoardByBoxCode(request.getCode(), request.getCurrentOperate().getSiteCode());
                if (!JdCResponse.CODE_SUCCESS.equals(boardResponse.getCode())) {
                    log.info("按板取消发货-未根据箱号/包裹号找到匹配的板号！");
                    return new InvokeResult(MSCodeMapping.NO_BOARD_BY_PACKAGECODE.getCode(), MSCodeMapping.NO_BOARD_BY_PACKAGECODE.getMessage());
                }
                log.info("============按板取消发货，扫描的包裹号/箱号：{}，板号：{}", request.getCode(), boardResponse.getData().getCode());
                boardCode = boardResponse.getData().getCode();
            }
            sendM.setBoxCode(boardCode);
            cancelSendTaskResp.setCancelCode(boardCode);
        }

        if (CancelSendTypeEnum.BOARD_NO.getCode().equals(request.getType()) && BusinessUtil.isBoardCode(sendM.getBoxCode())) {
            //查询组板信息
            Response<List<String>> response = groupBoardManager.getBoxesByBoardCode(sendM.getBoxCode());
            if (!(JdCResponse.CODE_SUCCESS.equals(response.getCode()) && null != response.getData() && response.getData().size() > 0)) {
                log.info("根据板号：{}未查到包裹/箱号信息", sendM.getBoxCode());
                return new InvokeResult(RESULT_NO_FOUND_BY_BOARD_CODE, RESULT_NO_FOUND_BY_BOARD_MESSAGE);
            }
            List<String> packOrBoxCodes = response.getData();
            List<String> packageCodes = getPackageCodesFromPackOrBoxCodes(packOrBoxCodes, request.getCurrentOperate().getSiteCode());
            cancelSendTaskResp.setCanclePackageCount(packageCodes.size());

            //查询一下sendCode信息
            SendM sendMDto = sendMService.selectSendByBoardCode(request.getCurrentOperate().getSiteCode(), sendM.getBoxCode(), 1);
            if (sendMDto == null) {
                log.info("按板取消发货==========没有找到按板的sendM(发货)记录");
                return new InvokeResult(NO_SEND_DATA_UNDER_BOARD_CODE, NO_SEND_DATA_UNDER_BOARD_MESSAGE);
            }
            sendM.setSendCode(sendMDto.getSendCode());
        }
        //执行取消发货
        ThreeDeliveryResponse tDResponse = deliveryService.dellCancelDeliveryMessageWithServerTime(sendM, true);
        if (ObjectHelper.isNotNull(tDResponse) && JdCResponse.CODE_SUCCESS.equals(tDResponse.getCode())) {
            if (cancelSendTaskResp.getCanclePackageCount() == null) {
                cancelSendTaskResp.setCanclePackageCount(sendM.getCancelPackageCount());
            }
            String sendCode = getSendCodeByScanCode(request.getCode(), request.getCurrentOperate().getSiteCode());
            if (BusinessUtil.isSendCode(sendCode)) {
                BaseSiteInfoDto baseSiteInfoDto = baseMajorManager.getBaseSiteInfoBySiteId(BusinessUtil.getReceiveSiteCodeFromSendCode(sendCode));
                if (ObjectHelper.isNotNull(baseSiteInfoDto) && ObjectHelper.isNotNull(baseSiteInfoDto.getSiteName())) {
                    cancelSendTaskResp.setEndSiteName(baseSiteInfoDto.getSiteName());
                }
            } else {
                log.info("jy取消发货-根据扫描code获取sendCode异常！");
            }
            return new InvokeResult(tDResponse.getCode(), tDResponse.getMessage(), cancelSendTaskResp);
        }
        return new InvokeResult(tDResponse.getCode(), tDResponse.getMessage());
    }

    private String getSendCodeByScanCode(String code, int createSiteCode) {
        /**
         * 包裹 运单 查sendD  箱号或者板号 查sendM
         */
        if (WaybillUtil.isPackageCode(code)) {
            List<SendDetail> sendDetailList = sendDetailService.findByWaybillCodeOrPackageCode(createSiteCode, null, code);
            if (ObjectHelper.isNotNull(sendDetailList) && sendDetailList.size() > 0) {
                return sendDetailList.get(0).getSendCode();
            }
        } else if (WaybillUtil.isWaybillCode(code)) {
            List<SendDetail> sendDetailList = sendDetailService.findByWaybillCodeOrPackageCode(createSiteCode, code, null);
            if (ObjectHelper.isNotNull(sendDetailList) && sendDetailList.size() > 0) {
                return sendDetailList.get(0).getSendCode();
            }
        } else if (BusinessUtil.isBoxcode(code)) {
            List<SendM> sendMList = sendMService.findDeliveryRecord(createSiteCode, code);
            if (ObjectHelper.isNotNull(sendMList) && sendMList.size() > 0) {
                return sendMList.get(0).getSendCode();
            }
        } else if (BusinessUtil.isBoardCode(code)) {
            SendM sendM = sendMService.selectSendByBoardCode(createSiteCode, code, SendStatusEnum.HAS_BEEN_SENDED.getCode());
            if (ObjectHelper.isNotNull(sendM)) {
                return sendM.getSendCode();
            }
        }
        return null;
    }

    private void validateCancelReq(CancelSendTaskReq request) {
        if (request.getType() == null) {
            throw new JyBizException("取消扫描类型不能为空！");
        }
        if (CancelSendTypeEnum.getReportTypeName(request.getType()) == null) {
            throw new JyBizException("不支持该扫描类型！");
        }
        //按运单-支持运单和包裹
        if (CancelSendTypeEnum.WAYBILL_CODE.getCode().equals(request.getType())) {
            if (!(WaybillUtil.isPackageCode(request.getCode()) || WaybillUtil.isWaybillCode(request.getCode()))) {
                throw new JyBizException("无效条码，请扫描运单号或者包裹号！");
            }
        }
        //按箱-支持箱和包裹
        else if (CancelSendTypeEnum.BOX_CODE.getCode().equals(request.getType())) {
            if (!(BusinessUtil.isBoxcode(request.getCode()) || WaybillUtil.isPackageCode(request.getCode()))) {
                throw new JyBizException("无效条码，请扫描箱号或者包裹号！");
            }
        }
        //按板-支持板 箱 包裹
        else if (CancelSendTypeEnum.BOARD_NO.getCode().equals(request.getType())) {
            if (!(BusinessUtil.isBoardCode(request.getCode()) || BusinessUtil.isBoxcode(request.getCode()) || WaybillUtil.isPackageCode(request.getCode()))) {
                throw new JyBizException("无效条码，请扫描板号、箱号或者包裹号！");
            }
        } else {
            if (!WaybillUtil.isPackageCode(request.getCode())) {
                throw new JyBizException("无效条码，请扫描包裹号！");
            }
        }
    }

    private List<String> getPackageCodesFromPackOrBoxCodes(List<String> packOrBoxCodes, Integer siteCode) {
        List<String> packageCodes = new ArrayList<>();
        for (String code : packOrBoxCodes) {
            if (BusinessUtil.isBoxcode(code)) {
                log.info("=====getPackageCodesFromPackOrBoxCodes=======根据箱号获取集包包裹 {}", code);
                List<String> pCodes = getPackageCodesByBoxCodeOrSendCode(code, siteCode);
                if (pCodes != null && pCodes.size() > 0) {
                    log.info("======getPackageCodesFromPackOrBoxCodes======根据sendD找到包裹信息{}", pCodes.toString());
                    packageCodes.addAll(pCodes);
                }
            } else {
                packageCodes.add(code);
            }
        }
        return packageCodes;
    }

    private SendDetailDto initSendDetail(String barcode, int createSiteCode) {
        SendDetailDto sendDetail = new SendDetailDto();
        sendDetail.setIsCancel(0);
        sendDetail.setCreateSiteCode(createSiteCode);
        if (BusinessUtil.isBoxcode(barcode)) {
            sendDetail.setBoxCode(barcode);
        }
        if (BusinessUtil.isSendCode(barcode)) {
            sendDetail.setSendCode(barcode);
        }
        return sendDetail;
    }

    private List<String> getPackageCodesByBoxCodeOrSendCode(String boxOrSendCode, Integer siteCode) {
        //构建查询sendDetail的查询参数
        SendDetailDto sendDetail = initSendDetail(boxOrSendCode, siteCode);
        if (BusinessUtil.isBoxcode(boxOrSendCode)) {
            return sendDetailService.queryPackageCodeByboxCode(sendDetail);
        }
        return sendDetailService.queryPackageCodeBySendCode(sendDetail);
    }

    private SendM toSendM(CancelSendTaskReq request) {
        SendM sendM = new SendM();
        sendM.setBoxCode(request.getCode());
        sendM.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
        sendM.setUpdaterUser(request.getUser().getUserName());
        sendM.setSendType(10);
        sendM.setUpdateUserCode(request.getUser().getUserCode());
        Date now = new Date();
        sendM.setOperateTime(now);
        sendM.setUpdateTime(now);
        sendM.setYn(0);
        return sendM;
    }

    public static void main(String[] args) {
        Integer a =0;
        System.out.println(a.longValue());
    }
}
