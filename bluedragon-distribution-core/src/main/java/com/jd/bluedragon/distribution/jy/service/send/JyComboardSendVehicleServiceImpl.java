package com.jd.bluedragon.distribution.jy.service.send;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.operation.workbench.enums.BarCodeLabelOptionEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleLabelOptionEnum;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SelectSealDestRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendAbnormalPackRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendDetailRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleProgressRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendAbnormalBarCode;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendDestDetail;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendScanBarCode;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendTaskInfo;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendTaskItemDetail;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleDetail;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleProgress;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.ToSealDestAgg;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.ToSealDestDetail;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleStatusStatis;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BasicQueryWSManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendCountDto;
import com.jd.bluedragon.distribution.jy.dto.send.QueryTaskSendDto;
import com.jd.bluedragon.distribution.jy.enums.*;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendSortTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyComboardLineTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.SendBarCodeQueryEntranceEnum;
import com.jd.bluedragon.distribution.jy.manager.IJyComboardJsfManager;
import com.jd.bluedragon.distribution.jy.service.seal.JySealVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.NumberHelper;

import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.tms.jdi.dto.TransWorkBillDto;
import java.math.BigDecimal;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.es.comboard.ComboardScanedDto;
import com.jdl.jy.realtime.model.es.comboard.JyComboardPackageDetail;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.jy.comboard.JyComboardAggsEntity;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardAggsService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.tms.basic.dto.BasicVehicleTypeDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.jd.bluedragon.Constants.DOUBLE_ZERO;
import static com.jd.bluedragon.Constants.LONG_ZERO;


@Slf4j
@Service("jyComboardSendVehicleService")
public class JyComboardSendVehicleServiceImpl extends JySendVehicleServiceImpl{

  @Autowired
  private JyBizTaskSendVehicleDetailService taskSendVehicleDetailService;

  @Autowired
  private JyComboardAggsService jyComboardAggsService;
  @Autowired
  IJyComboardJsfManager comboardJsfManager;

  @Autowired
  private BasicQueryWSManager basicQueryWSManager;
  @Autowired
  DmsConfigManager dmsConfigManager;
  @Autowired
  @Qualifier("jyBaseSealVehicleService")
  JySealVehicleService jySealVehicleService;

  @Override
  @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyComboardSendVehicleServiceImpl.sendDestDetail", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
  public InvokeResult<List<SendDestDetail>> sendDestDetail(SendDetailRequest request) {
    InvokeResult<List<SendDestDetail>> invokeResult = new InvokeResult<>();

    if (request.getCurrentOperate() == null
            || request.getCurrentOperate().getSiteCode() <= 0
            || StringUtils.isBlank(request.getSendVehicleBizId())) {
      invokeResult.parameterError();
      return invokeResult;
    }

    try {
      List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(new JyBizTaskSendVehicleDetailEntity((long) request.getCurrentOperate().getSiteCode(), request.getSendVehicleBizId()));
      if (CollectionUtils.isEmpty(vehicleDetailList)) {
        invokeResult.hintMessage("发货流向为空");
        return invokeResult;
      }
      List<SendDestDetail> sendDestDetails = new ArrayList<>();
      invokeResult.setData(sendDestDetails);

      List<Integer> endSiteIdList = new ArrayList<>();
      for (JyBizTaskSendVehicleDetailEntity entity : vehicleDetailList) {
        endSiteIdList.add(entity.getEndSiteId().intValue());
      }
      List<JyComboardAggsEntity> aggsList = null;
      try {
        aggsList = jyComboardAggsService.queryComboardAggs(request.getCurrentOperate().getSiteCode(),endSiteIdList);
      }catch (Exception e) {
        if (log.isErrorEnabled()) {
          log.error("查询统计数据异常：{}", JsonHelper.toJson(vehicleDetailList),e);
        }
      }
      Map<Integer, JyComboardAggsEntity> sendAggMap = new HashMap<>();
      if (CollectionUtils.isNotEmpty(aggsList)) {
        for (JyComboardAggsEntity aggEntity : aggsList) {
          sendAggMap.put(aggEntity.getReceiveSiteId(), aggEntity);
        }
      }

      for (JyBizTaskSendVehicleDetailEntity detailEntity : vehicleDetailList) {
        SendDestDetail sendDestDetail = new SendDestDetail();
        sendDestDetail.setEndSiteId(detailEntity.getEndSiteId().intValue());
        sendDestDetail.setEndSiteName(detailEntity.getEndSiteName());
        sendDestDetail.setPlanDepartTime(detailEntity.getPlanDepartTime());
        sendDestDetail.setVehicleStatus(convertVehicleStatus(detailEntity.getVehicleStatus()));

        if (sendAggMap.containsKey(detailEntity.getEndSiteId().intValue())) {
          JyComboardAggsEntity itemAgg = sendAggMap.get(detailEntity.getEndSiteId().intValue());
          if (itemAgg.getWaitScanCount() != null ) {
            sendDestDetail.setToScanPackCount(itemAgg.getWaitScanCount().longValue());
          }
          if (itemAgg.getScannedCount() != null ) {
            sendDestDetail.setScannedPackCount(itemAgg.getScannedCount().longValue());
          }
        }

        sendDestDetails.add(sendDestDetail);
      }
    } catch (Exception ex) {
      log.error("查询发货任务流向失败. {}", JsonHelper.toJson(request), ex);
      invokeResult.error("服务器异常，查询发货任务流向异常，请咚咚联系分拣小秘！");
    }

    return invokeResult;  }

  @Override
  @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyComboardSendVehicleServiceImpl.loadProgress",
          jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
  public InvokeResult<SendVehicleProgress> loadProgress(SendVehicleProgressRequest request) {
    return super.loadProgress(request);
  }
  @Override
  @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyComboardSendVehicleServiceImpl.selectSealDest",
          jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
  public InvokeResult<ToSealDestAgg> selectSealDest(SelectSealDestRequest request) {
    return super.selectSealDest(request);
  }

  public List<Integer> assembleStatusCon(Integer vehicleStatus){
    List<Integer> queryStatus =new ArrayList<>();
    if (JyBizTaskSendStatusEnum.SEALED.getCode().equals(vehicleStatus)){
      queryStatus.add(vehicleStatus);
    }
    else {
      queryStatus.add(JyBizTaskSendStatusEnum.TO_SEND.getCode());
      queryStatus.add(JyBizTaskSendStatusEnum.SENDING.getCode());
      queryStatus.add(JyBizTaskSendStatusEnum.TO_SEAL.getCode());
      queryStatus.add(JyBizTaskSendStatusEnum.CANCEL.getCode());
    }
    return queryStatus;
  }

  @Override
  public void assembleSendTaskStatus(SendTaskInfo sendTaskInfo,
      JyBizTaskSendVehicleEntity sendVehicleEntity) {
    if (JyBizTaskSendStatusEnum.SEALED.getCode().equals(sendVehicleEntity.getVehicleStatus())){
      sendTaskInfo.setVehicleStatus(sendVehicleEntity.getVehicleStatus());
    }
    else {
      sendTaskInfo.setVehicleStatus(JyBizTaskSendStatusEnum.TO_SEAL.getCode());
    }
    sendTaskInfo.setVehicleStatusName(JySendVehicleStatusEnum.getNameByCode(sendTaskInfo.getVehicleStatus()));
  }

  @Override
  public void assembleSendTaskDetailStatus(JyBizTaskSendVehicleDetailEntity sendVehicleDetailEntity,
      SendTaskItemDetail sendTaskItemDetail) {
    if (JyBizTaskSendStatusEnum.SEALED.getCode().equals(sendVehicleDetailEntity.getVehicleStatus())){
      sendTaskItemDetail.setVehicleStatus(sendVehicleDetailEntity.getVehicleStatus());
    }
    else {
      sendTaskItemDetail.setVehicleStatus(JyBizTaskSendStatusEnum.TO_SEAL.getCode());
    }
    sendTaskItemDetail.setVehicleStatusName(JySendVehicleStatusEnum.getNameByCode(sendTaskItemDetail.getVehicleStatus()));
  }

  @Override
  public List<JyBizTaskSendVehicleEntity> querySendTaskOfPage(
      JyBizTaskSendVehicleEntity queryCondition, QueryTaskSendDto queryTaskSendDto,
      JyBizTaskSendSortTypeEnum orderTypeEnum) {
    List<Integer> queryStatus =assembleStatusCon(queryTaskSendDto.getVehicleStatuses().get(0));


    if (!dmsConfigManager.getPropertyConfig().getCzQuerySwitch()){
      log.info("=============3.走兼容模式================");
      queryCondition.setLineType(null);
    }
    else {
      log.info("=============3.走原有模式================");
    }
    return taskSendVehicleService.querySendTaskOfPage(queryCondition, queryTaskSendDto.getSendVehicleBizList(), orderTypeEnum,
        queryTaskSendDto.getPageNumber(), queryTaskSendDto.getPageSize(), queryStatus);
  }

  @Override
  public boolean checkBeforeFetchTask(SendVehicleTaskRequest request,
      InvokeResult<SendVehicleTaskResponse> result) {
    if (request.getVehicleStatus() == null) {
      result.parameterError("请选择车辆状态！");
      return false;
    }
    if (!supportStatus(request.getVehicleStatus())){
      result.parameterError("暂不支持该状态查询！");
      return false;
    }
    if (!NumberHelper.gt0(request.getPageSize()) || !NumberHelper.gt0(request.getPageNumber())) {
      result.parameterError("缺少分页参数！");
      return false;
    }
    if (request.getCurrentOperate() == null || !NumberHelper.gt0(request.getCurrentOperate().getSiteCode())) {
      result.parameterError("缺少当前场地信息！");
      return false;
    }
    if (request.getLineType() == null) {
      result.parameterError("请选择线路类型！");
      return false;
    }
    if (!supportLineType(request.getLineType())){
      result.parameterError("暂不支持该线路类型！");
      return false;
    }
    return true;
  }

  private boolean supportStatus(Integer vehicleStatus) {
      if (JyBizTaskSendStatusEnum.TO_SEAL.getCode().equals(vehicleStatus)
      || JyBizTaskSendStatusEnum.SEALED.getCode().equals(vehicleStatus)){
        return true;
    }
    return false;
  }

  private boolean supportLineType(Integer lineType) {
    boolean support =false;
    for (JyComboardLineTypeEnum lineTypeEnum : JyComboardLineTypeEnum.values()) {
      if (lineTypeEnum.getCode().equals(lineType)){
        support =true;
        break;
      }
    }
    return support;
  }

  @Override
  public void assembleSendVehicleStatusAgg(List<JyBizTaskSendCountDto> vehicleStatusAggList,
      SendVehicleTaskResponse response) {
    List<VehicleStatusStatis> statusAgg = Lists.newArrayListWithCapacity(2);
    response.setStatusAgg(statusAgg);

    VehicleStatusStatis toSeal = new VehicleStatusStatis();
    toSeal.setVehicleStatus(JyBizTaskSendStatusEnum.TO_SEAL.getCode());
    toSeal.setVehicleStatusName(JyBizTaskSendStatusEnum.TO_SEAL.getName());
    toSeal.setTotal(LONG_ZERO);

    VehicleStatusStatis sealed = new VehicleStatusStatis();
    sealed.setVehicleStatus(JyBizTaskSendStatusEnum.SEALED.getCode());
    sealed.setVehicleStatusName(JyBizTaskSendStatusEnum.SEALED.getName());
    sealed.setTotal(LONG_ZERO);

    statusAgg.add(toSeal);
    statusAgg.add(sealed);

    for (JyBizTaskSendCountDto countDto : vehicleStatusAggList) {
      if (JyBizTaskSendStatusEnum.SEALED.getCode().equals(countDto.getVehicleStatus())){
        sealed.setTotal(countDto.getTotal());
      }
      else {
        toSeal.setTotal(toSeal.getTotal()+countDto.getTotal());
      }
    }
  }

  public <T> List<String> resolveSearchKeywordRaw(InvokeResult<T> result, QueryTaskSendDto queryTaskSendDto) {
    long startSiteId = queryTaskSendDto.getStartSiteId();
    if (!StringUtils.isBlank(queryTaskSendDto.getKeyword())) {
    Long endSiteId = null;

    // 扫描包裹号取路由流向
    if (WaybillUtil.isPackageCode(queryTaskSendDto.getKeyword())) {
      endSiteId = getWaybillNextRouter(WaybillUtil.getWaybillCode(queryTaskSendDto.getKeyword()),
          startSiteId);
    }//扫箱号取箱号目的地（传站）
    else if (BusinessUtil.isBoxcode(queryTaskSendDto.getKeyword())
        && JyComboardLineTypeEnum.TRANSFER.getCode().equals(queryTaskSendDto.getLineType())) {
      endSiteId = getBoxEndSiteId(queryTaskSendDto.getKeyword());
    }//取批次目的地
    else if (BusinessUtil.isSendCode(queryTaskSendDto.getKeyword())) {
      endSiteId = Long
          .valueOf(BusinessUtil.getReceiveSiteCodeFromSendCode(queryTaskSendDto.getKeyword()));
    }//根据任务简码获取派车单号码
    else if (BusinessUtil.isTaskSimpleCode(queryTaskSendDto.getKeyword())) {
      List<String> sendVehicleBizList = querySendVehicleBizIdByTaskSimpleCode(queryTaskSendDto);
      if (ObjectHelper.isNotNull(sendVehicleBizList) && sendVehicleBizList.size() > 0) {
        return sendVehicleBizList;
      }
      result.hintMessage("未检索到相应的派车任务！");
      return null;
    }//完整车牌号检索
    else if (!NumberHelper.isNumber(queryTaskSendDto.getKeyword())
        && (queryTaskSendDto.getKeyword().matches(DmsConstants.CHINESE_PREFIX)
        || queryTaskSendDto.getKeyword().matches(DmsConstants.CODE_PREFIX))){
      String chineseCarNum = "";
      if (queryTaskSendDto.getKeyword().matches(DmsConstants.CHINESE_PREFIX)){
        chineseCarNum =queryTaskSendDto.getKeyword();
      }
      else if (queryTaskSendDto.getKeyword().matches(DmsConstants.CODE_PREFIX)){
        chineseCarNum =jySealVehicleService.transformLicensePrefixToChinese(queryTaskSendDto.getKeyword());
      }
      if (ObjectHelper.isNotNull(chineseCarNum)){
        queryTaskSendDto.setKeyword(chineseCarNum);
        List<String> sendVehicleBizList = querySendVehicleBizIdByVehicleFuzzy(queryTaskSendDto);
        if (ObjectHelper.isNotNull(sendVehicleBizList) && sendVehicleBizList.size() > 0) {
          return sendVehicleBizList;
        }
      }
      result.hintMessage("未检索到相应的派车任务！");
      return null;
    }
    else if (queryTaskSendDto.getKeyword().startsWith("TW") &&  queryTaskSendDto.getKeyword().length()==16){
      List<String> sendVehicleBizList = querySendVehicleBizIdByTransWorkCode(queryTaskSendDto);
      if (ObjectHelper.isNotNull(sendVehicleBizList) && sendVehicleBizList.size() > 0) {
        return sendVehicleBizList;
      }
      result.hintMessage("未检索到相应的派车任务！");
      return null;
    }
    else if (!NumberHelper.gt0(queryTaskSendDto.getKeyword()) && VEHICLE_NUMBER_FOUR ==queryTaskSendDto.getKeyword().length()){
      List<String> sendVehicleBizList = querySendVehicleBizIdByVehicleFuzzy(queryTaskSendDto);
      if (ObjectHelper.isNotNull(sendVehicleBizList) && sendVehicleBizList.size() > 0) {
        return sendVehicleBizList;
      }
      result.hintMessage("未检索到相应的派车任务！");
      return null;
    }
    else if (NumberHelper.gt0(queryTaskSendDto.getKeyword())){
      //四位 优先按车牌号
      if (VEHICLE_NUMBER_FOUR ==queryTaskSendDto.getKeyword().length()){
        //没查到再按目的站点匹配
        List<String> sendVehicleBizList = querySendVehicleBizIdByVehicleFuzzy(queryTaskSendDto);
        if (ObjectHelper.isNotNull(sendVehicleBizList) && sendVehicleBizList.size() > 0) {
          return sendVehicleBizList;
        }
        endSiteId =Long.valueOf(queryTaskSendDto.getKeyword());
        queryTaskSendDto.setEndSiteId(endSiteId);
      }
      //按站点匹配
      else {
        endSiteId = Long.valueOf(queryTaskSendDto.getKeyword());
        queryTaskSendDto.setEndSiteId(endSiteId);
      }
    }
    else {
      //不支持该类型
      result.hintMessage("暂不支持该类型条码！");
      return null;
    }

    if (endSiteId == null) {
      result.hintMessage("运单的路由没有当前场地！");
      return null;
    }

    // 根据路由下一节点查询发货流向的任务
    JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(startSiteId,
        endSiteId);
    List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService
        .findBySiteAndStatus(detailQ, null);
    if (CollectionUtils.isEmpty(vehicleDetailList)) {
      result.hintMessage("未检索到相应的派车任务！");
      return null;
    }
    Set<String> sendVehicleBizSet = new HashSet<>();
    for (JyBizTaskSendVehicleDetailEntity entity : vehicleDetailList) {
      sendVehicleBizSet.add(entity.getSendVehicleBizId());
    }

    return new ArrayList<>(sendVehicleBizSet);
  } else if (ObjectHelper.isNotNull(queryTaskSendDto.getEndSiteId())){
      // 根据路由下一节点查询发货流向的任务
      JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(startSiteId,
          queryTaskSendDto.getEndSiteId());
      List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService
          .findBySiteAndStatus(detailQ, null);
      if (CollectionUtils.isEmpty(vehicleDetailList)) {
        result.hintMessage("未检索到该流向的派车任务，请联系运输调度下发派车任务！");
        return null;
      }
      Set<String> sendVehicleBizSet = new HashSet<>();
      for (JyBizTaskSendVehicleDetailEntity entity : vehicleDetailList) {
        sendVehicleBizSet.add(entity.getSendVehicleBizId());
      }

      return new ArrayList<>(sendVehicleBizSet);

    }
    return null;
  }
  public <T> List<String> resolveSearchKeywordCZ(InvokeResult<T> result, QueryTaskSendDto queryTaskSendDto) {
    long startSiteId = queryTaskSendDto.getStartSiteId();
    if (!StringUtils.isBlank(queryTaskSendDto.getKeyword())) {
      Long endSiteId = null;

      // 取当前操作网点的路由下一节点
      if (WaybillUtil.isPackageCode(queryTaskSendDto.getKeyword())) {
        endSiteId = getWaybillNextRouter(WaybillUtil.getWaybillCode(queryTaskSendDto.getKeyword()),
            startSiteId);
      } else if (BusinessUtil.isBoxcode(queryTaskSendDto.getKeyword())
          && JyComboardLineTypeEnum.TRANSFER.getCode().equals(queryTaskSendDto.getLineType())) {
        endSiteId = getBoxEndSiteId(queryTaskSendDto.getKeyword());
      } else if (BusinessUtil.isSendCode(queryTaskSendDto.getKeyword())) {
        endSiteId = Long
            .valueOf(BusinessUtil.getReceiveSiteCodeFromSendCode(queryTaskSendDto.getKeyword()));
      } else if (BusinessUtil.isTaskSimpleCode(queryTaskSendDto.getKeyword())) {
        List<String> sendVehicleBizList = querySendVehicleBizIdByTaskSimpleCode(queryTaskSendDto);
        if (ObjectHelper.isNotNull(sendVehicleBizList) && sendVehicleBizList.size() > 0) {
          return sendVehicleBizList;
        }
        result.hintMessage("未检索到相应的派车任务！");
        return null;
      } else {
        //车牌号后四位检索
        if (queryTaskSendDto.getKeyword().length() == VEHICLE_NUMBER_FOUR) {
          List<String> sendVehicleBizList = querySendVehicleBizIdByVehicleFuzzy(queryTaskSendDto);
          if (ObjectHelper.isNotNull(sendVehicleBizList) && sendVehicleBizList.size() > 0) {
            return sendVehicleBizList;
          }
          result.hintMessage("未检索到相应的派车任务！");
        } else {
          result.hintMessage("输入位数错误，未检索到相应的派车任务！");
        }
        return null;
      }

      if (endSiteId == null) {
        result.hintMessage("运单的路由没有当前场地！");
        return null;
      }

      // 根据路由下一节点查询发货流向的任务
      JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(startSiteId,
          endSiteId);
      List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService
          .findBySiteAndStatus(detailQ, null);
      if (CollectionUtils.isEmpty(vehicleDetailList)) {
        result.hintMessage("未检索到相应的派车任务！");
        return null;
      }
      Set<String> sendVehicleBizSet = new HashSet<>();
      for (JyBizTaskSendVehicleDetailEntity entity : vehicleDetailList) {
        sendVehicleBizSet.add(entity.getSendVehicleBizId());
      }

      return new ArrayList<>(sendVehicleBizSet);
    }
    else {
      //按照时间检索明细数据 获取bizId
      JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity();
      detailQ.setStartSiteId(startSiteId);
      if (ObjectHelper.isNotNull(queryTaskSendDto.getEndSiteId())){
        detailQ.setEndSiteId(queryTaskSendDto.getEndSiteId());
      }
      detailQ.setLineType(queryTaskSendDto.getLineType());
      List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findBySiteAndStatus(detailQ,null);
      if (CollectionUtils.isEmpty(vehicleDetailList)) {
        //result.hintMessage("未查询到传站类型的任务数据！");
        result.hintMessage("");
        return null;
      }
      Set<String> sendVehicleBizSet = new HashSet<>();
      for (JyBizTaskSendVehicleDetailEntity entity : vehicleDetailList) {
        sendVehicleBizSet.add(entity.getSendVehicleBizId());
      }
      return new ArrayList<>(sendVehicleBizSet);
    }
  }

  @Override
  public <T> List<String> resolveSearchKeyword(InvokeResult<T> result, QueryTaskSendDto queryTaskSendDto) {
    if (dmsConfigManager.getPropertyConfig().getCzQuerySwitch()){
      log.info("=============1.走原有模式================");
      return resolveSearchKeywordRaw(result,queryTaskSendDto);
    }
    log.info("=============1.走兼容模式================");
    return resolveSearchKeywordCZ(result,queryTaskSendDto);
  }

  @Override
  List<JyBizTaskSendCountDto> sumTaskByVehicleStatus(JyBizTaskSendVehicleEntity condition,
      List<String> sendVehicleBizList) {
    if (!dmsConfigManager.getPropertyConfig().getCzQuerySwitch()){
      log.info("=============2.走兼容模式================");
      condition.setLineType(null);
    }
    else {
      log.info("=============2.走原有模式================");
    }
    return taskSendVehicleService.sumTaskByVehicleStatusForTransfer(condition, sendVehicleBizList);
  }

  @Override
  public QueryTaskSendDto setQueryTaskSendDto(SendVehicleTaskRequest request) {
    QueryTaskSendDto queryTaskSendDto = new QueryTaskSendDto();
    queryTaskSendDto.setPageNumber(request.getPageNumber());
    queryTaskSendDto.setPageSize(request.getPageSize());
    queryTaskSendDto.setVehicleStatuses(Collections.singletonList(request.getVehicleStatus()));
    queryTaskSendDto.setLineType(request.getLineType());
    queryTaskSendDto.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
    queryTaskSendDto.setEndSiteId(request.getEndSiteId());
    queryTaskSendDto.setKeyword(request.getKeyword());
    //设置默认预计发货时间查询范围
    try {
      Date now =new Date();
      queryTaskSendDto.setLastPlanDepartTimeBegin(DateHelper.addHoursByDay(now,-Double.valueOf(dmsConfigManager.getPropertyConfig().getJyCzSendTaskPlanTimeBeginDay())));
      queryTaskSendDto.setLastPlanDepartTimeEnd(DateHelper.addHoursByDay(now, Double.valueOf(dmsConfigManager.getPropertyConfig().getJyCzSendTaskPlanTimeEndDay())));
      queryTaskSendDto.setCreateTimeBegin(DateHelper.addHoursByDay(now, -Double.valueOf(dmsConfigManager.getPropertyConfig().getJySendTaskCreateTimeBeginDay())));

    } catch (Exception e) {
      log.error("查询传站运输任务设置默认查询条件异常，入参{}", JsonHelper.toJson(request), e);
    }
    return queryTaskSendDto;
  }

  @Override
  public List<SendVehicleDetail> getSendVehicleDetail(QueryTaskSendDto queryTaskSendDto,
      JyBizTaskSendStatusEnum curQueryStatus, JyBizTaskSendVehicleEntity entity) {
    JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(queryTaskSendDto.getStartSiteId(), queryTaskSendDto.getEndSiteId(), entity.getBizId());
    if (!dmsConfigManager.getPropertyConfig().getCzQuerySwitch()){
      detailQ.setLineType(queryTaskSendDto.getLineType());
    }
    List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(detailQ);
    if (CollectionUtils.isEmpty(vehicleDetailList)) {
      return Lists.newArrayList();
    }
    List<SendVehicleDetail> sendDestList = Lists.newArrayListWithCapacity(vehicleDetailList.size());
    for (JyBizTaskSendVehicleDetailEntity vehicleDetail : vehicleDetailList) {
      SendVehicleDetail detailVo = new SendVehicleDetail();
      detailVo.setItemStatus(convertVehicleStatus(vehicleDetail.getVehicleStatus()));
      detailVo.setItemStatusDesc(JyBizTaskSendStatusEnum.getNameByCode(detailVo.getItemStatus()));
      detailVo.setPlanDepartTime(vehicleDetail.getPlanDepartTime());
      detailVo.setEndSiteId(vehicleDetail.getEndSiteId());
      detailVo.setEndSiteName(vehicleDetail.getEndSiteName());
      detailVo.setSendDetailBizId(vehicleDetail.getBizId());

      sendDestList.add(detailVo);
    }

    return sendDestList;
  }

  /**
   * 状态装换
   * 待发货 发货中 待封车 已作废 都转换待封车状态
   */
  private Integer convertVehicleStatus(Integer vehicleStatus) {
    if (JyBizTaskSendStatusEnum.SEALED.getCode().equals(vehicleStatus)) {
      return vehicleStatus;
    }
    return JyBizTaskSendStatusEnum.TO_SEAL.getCode();
  }


  @Override
  public List<ToSealDestDetail> setSendDestDetail(SelectSealDestRequest request,
                                                  List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList) {
    List<ToSealDestDetail> sendDestDetails = new ArrayList<>();
    List<Integer> endSiteCodeList = new ArrayList<>();
    for (JyBizTaskSendVehicleDetailEntity entity : vehicleDetailList) {
      endSiteCodeList.add(entity.getEndSiteId().intValue());
    }
    List<JyComboardAggsEntity> comboardAggList = null;
    try {
      comboardAggList = jyComboardAggsService.queryComboardAggs(request.getCurrentOperate().getSiteCode(),endSiteCodeList);
    } catch (Exception e) {
      if (log.isInfoEnabled()){
        log.info("查询统计数据失败：{}",JsonHelper.toJson(request),e);
      }
    }
    Map<Integer, JyComboardAggsEntity> comboardAggMap = new HashMap<>();
    if (CollectionUtils.isNotEmpty(comboardAggList)) {
      for (JyComboardAggsEntity aggEntity : comboardAggList) {
        comboardAggMap.put(aggEntity.getReceiveSiteId(), aggEntity);
      }
    }

    for (JyBizTaskSendVehicleDetailEntity detailEntity : vehicleDetailList) {
      ToSealDestDetail sendDestDetail = new ToSealDestDetail();
      sendDestDetail.setSendDetailBizId(detailEntity.getBizId());
      sendDestDetail.setItemStatus(detailEntity.getVehicleStatus());
      sendDestDetail.setItemStatusDesc(JyBizTaskSendDetailStatusEnum.getNameByCode(convertVehicleStatus(detailEntity.getVehicleStatus())));

      sendDestDetail.setEndSiteId(detailEntity.getEndSiteId().intValue());
      sendDestDetail.setEndSiteName(detailEntity.getEndSiteName());
      sendDestDetail.setPlanDepartTime(detailEntity.getPlanDepartTime());

      if (comboardAggMap.containsKey(detailEntity.getEndSiteId().intValue())) {
        JyComboardAggsEntity itemAgg = comboardAggMap.get(detailEntity.getEndSiteId().intValue());
        if (itemAgg.getWaitScanCount() != null) {
          sendDestDetail.setToScanPackCount(itemAgg.getWaitScanCount().longValue());
        }
        if (itemAgg.getPackageTotalScannedCount() != null) {
          sendDestDetail.setScannedPackCount(itemAgg.getPackageTotalScannedCount().longValue());
        }
      }

      sendDestDetails.add(sendDestDetail);
    }

    return sendDestDetails;
  }

  @Override
  public void setSendProgressData(JyBizTaskSendVehicleEntity taskSend, SendVehicleProgress progress) {

    JyBizTaskSendVehicleDetailEntity endSiteListQuery = new JyBizTaskSendVehicleDetailEntity();
    endSiteListQuery.setSendVehicleBizId(taskSend.getBizId());
    List<Long> endSiteList = taskSendVehicleDetailService.getAllSendDest(endSiteListQuery);

    List<Integer> codeList = new ArrayList<>();
    for (Long siteCode : endSiteList) {
      codeList.add(siteCode.intValue());
    }
    List<JyComboardAggsEntity> comboardAggs = null;
    try {
      comboardAggs = jyComboardAggsService.queryComboardAggs(taskSend.getStartSiteId().intValue(), codeList);
    } catch (Exception e) {
      log.info("查询统计数据失败：{}",JsonHelper.toJson(taskSend),e);
    }

    BasicVehicleTypeDto basicVehicleType = basicQueryWSManager.getVehicleTypeByVehicleType(taskSend.getVehicleType());
    if (basicVehicleType != null) {
      progress.setVolume(BigDecimal.valueOf(basicVehicleType.getVolume()));
      progress.setWeight(BigDecimal.valueOf(basicVehicleType.getWeight()));
    }

    if (!CollectionUtils.isEmpty(comboardAggs) && basicVehicleType != null) {
      BigDecimal loadVolume = BigDecimal.ZERO;
      BigDecimal loadWeight = BigDecimal.ZERO;
      long waitScanCount = LONG_ZERO;
      long scannedPackCount = LONG_ZERO;
      long scannedBoxCount = LONG_ZERO;
      long interceptedPackCount = LONG_ZERO;
      for (JyComboardAggsEntity comboardAgg : comboardAggs) {
        loadVolume = loadVolume.add(comboardAgg.getVolume() == null ? BigDecimal.ZERO : comboardAgg.getVolume()).setScale(6, RoundingMode.HALF_UP) ;
        loadWeight = loadWeight.add(comboardAgg.getWeight() == null ? BigDecimal.ZERO : comboardAgg.getWeight()).setScale(6, RoundingMode.HALF_UP);
        waitScanCount += comboardAgg.getWaitScanCount() == null ? LONG_ZERO : comboardAgg.getWaitScanCount();
        scannedPackCount += comboardAgg.getPackageScannedCount() == null ? LONG_ZERO : comboardAgg.getPackageScannedCount();
        scannedBoxCount += comboardAgg.getBoxScannedCount() == null ? LONG_ZERO : comboardAgg.getBoxScannedCount();
        interceptedPackCount += comboardAgg.getInterceptCount() == null ? LONG_ZERO : comboardAgg.getInterceptCount();
      }
      progress.setLoadRate(dealLoadRate(loadVolume, convertTonToKg(BigDecimal.valueOf(basicVehicleType.getWeight()))));
      progress.setLoadVolume(loadVolume);
      progress.setLoadWeight(loadWeight);
      progress.setToScanCount(waitScanCount);
      progress.setScannedPackCount(scannedPackCount);
      progress.setScannedBoxCount(scannedBoxCount);
      progress.setInterceptedPackCount(interceptedPackCount);
    }
    progress.setDestTotal(getDestTotal(taskSend.getBizId()));
    progress.setSealedTotal(getSealedDestTotal(taskSend.getBizId()));
  }

  @Override
  public void querySendBarCodeList(InvokeResult<SendAbnormalBarCode> invokeResult,
      SendAbnormalPackRequest request, SendBarCodeQueryEntranceEnum entranceEnum,
      Integer queryFlag) {
    if (SendBarCodeQueryEntranceEnum.INTERCEPT!=entranceEnum){
      invokeResult.error("暂不支持该类型数据查询！");
      return;
    }

    SendAbnormalBarCode barCodeVo = new SendAbnormalBarCode();
    barCodeVo.setTotal(LONG_ZERO);
    List<SendScanBarCode> barCodeList = new ArrayList<>();
    barCodeVo.setBarCodeList(barCodeList);
    invokeResult.setData(barCodeVo);

    List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(new JyBizTaskSendVehicleDetailEntity((long) request.getCurrentOperate().getSiteCode(), request.getSendVehicleBizId()));
    if (CollectionUtils.isEmpty(vehicleDetailList)) {
      invokeResult.error("未查询到相关流向的信息！");
      return;
    }

    List<String> receiveSiteIdList=new ArrayList<>();
    for (JyBizTaskSendVehicleDetailEntity entity:vehicleDetailList){
      receiveSiteIdList.add(String.valueOf(entity.getEndSiteId()));
    }

    Pager<JyComboardPackageDetail> query =assembleQueryScanCondition(request,receiveSiteIdList);
    Pager<ComboardScanedDto> pager =comboardJsfManager.queryInterceptDetail(query);
    if (pager != null && CollectionUtils.isNotEmpty(pager.getData())) {
      barCodeVo.setTotal(pager.getTotal());
      List<LabelOption> tags = new ArrayList<>();
      tags.add(new LabelOption(BarCodeLabelOptionEnum.INTERCEPT.getCode(), BarCodeLabelOptionEnum.INTERCEPT.getName()));
      for (ComboardScanedDto comboardScanedDto : pager.getData()) {
        SendScanBarCode barCodeItem = new SendScanBarCode();
        barCodeItem.setBarCode(comboardScanedDto.getBarCode());
        barCodeItem.setTags(tags);
        barCodeList.add(barCodeItem);
      }
    }
  }

  private Pager<JyComboardPackageDetail> assembleQueryScanCondition(SendAbnormalPackRequest request,List<String> receiveSiteIdList) {
    Pager<JyComboardPackageDetail> pager = new Pager<>();
    JyComboardPackageDetail con =new JyComboardPackageDetail();
    con.setOperateSiteId(request.getCurrentOperate().getSiteCode());
    con.setReceiveSiteIdList(receiveSiteIdList);
    con.setInterceptFlag(Constants.YN_YES);
    pager.setSearchVo(con);
    pager.setPageNo(request.getPageNumber());
    pager.setPageSize(request.getPageSize());
    return pager;
  }

  @Override
  public List<LabelOption> resolveTaskTag(JyBizTaskSendVehicleEntity entity, TransWorkBillDto transWorkBillDto) {
    List<LabelOption> tagList = new ArrayList<>();

    // 司机是否领取任务
    if (transWorkBillDto != null) {
      // work_status = 20(已开始), status > 15(待接受)
      if (Objects.equals(TRANS_BILL_WORK_STATUS, transWorkBillDto.getWorkStatus()) && NumberHelper.gt(transWorkBillDto.getStatus(), TRANS_BILL_STATUS_CONFIRM)) {
        SendVehicleLabelOptionEnum driverRecvTaskTag = SendVehicleLabelOptionEnum.DRIVER_RECEIVE;
        tagList.add(new LabelOption(driverRecvTaskTag.getCode(), driverRecvTaskTag.getName(), driverRecvTaskTag.getDisplayOrder()));
      }
    }

    // 车长
    String carLengthDesc = setCarLength(entity);
    SendVehicleLabelOptionEnum carLengthTag = SendVehicleLabelOptionEnum.CAR_LENGTH;
    tagList.add(new LabelOption(carLengthTag.getCode(), carLengthDesc, carLengthTag.getDisplayOrder()));

    return tagList;
  }
}
