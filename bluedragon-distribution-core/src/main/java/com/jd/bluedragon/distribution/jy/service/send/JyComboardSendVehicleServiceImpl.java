package com.jd.bluedragon.distribution.jy.service.send;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.operation.workbench.enums.BarCodeLabelOptionEnum;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SelectSealDestRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendAbnormalPackRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendDetailRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleProgressRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendAbnormalBarCode;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendDestDetail;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendScanBarCode;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleDetail;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleProgress;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.ToSealDestAgg;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.ToSealDestDetail;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleStatusStatis;
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
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.utils.NumberHelper;

import java.math.BigDecimal;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.es.comboard.ComboardScanedDto;
import com.jdl.jy.realtime.model.es.comboard.JyComboardPackageDetail;
import java.util.ArrayList;
import java.util.HashMap;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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

  @Override
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
  public List<JyBizTaskSendVehicleEntity> querySendTaskOfPage(
      JyBizTaskSendVehicleEntity queryCondition, QueryTaskSendDto queryTaskSendDto,
      JyBizTaskSendSortTypeEnum orderTypeEnum) {
    List<Integer> queryStatus =assembleStatusCon(queryTaskSendDto.getVehicleStatuses().get(0));
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
    toSeal.setTotal(Long.valueOf(Constants.YN_NO));

    VehicleStatusStatis sealed = new VehicleStatusStatis();
    sealed.setVehicleStatus(JyBizTaskSendStatusEnum.SEALED.getCode());
    sealed.setVehicleStatusName(JyBizTaskSendStatusEnum.SEALED.getName());
    sealed.setTotal(Long.valueOf(Constants.YN_NO));

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

  @Override
  public List<SendVehicleDetail> getSendVehicleDetail(QueryTaskSendDto queryTaskSendDto,
      JyBizTaskSendStatusEnum curQueryStatus, JyBizTaskSendVehicleEntity entity) {
    JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(queryTaskSendDto.getStartSiteId(), queryTaskSendDto.getEndSiteId(), entity.getBizId());
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
   * 待发货 发货中 待封车 已作废 都转换成带封车状态
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
      sendDestDetail.setItemStatusDesc(JyBizTaskSendDetailStatusEnum.getNameByCode(detailEntity.getVehicleStatus()));

      sendDestDetail.setEndSiteId(detailEntity.getEndSiteId().intValue());
      sendDestDetail.setEndSiteName(detailEntity.getEndSiteName());
      sendDestDetail.setPlanDepartTime(detailEntity.getPlanDepartTime());

      if (comboardAggMap.containsKey(detailEntity.getEndSiteId().intValue())) {
        JyComboardAggsEntity itemAgg = comboardAggMap.get(detailEntity.getEndSiteId().intValue());
        if (itemAgg.getWaitScanCount() != null) {
          sendDestDetail.setToScanPackCount(itemAgg.getWaitScanCount().longValue());
        }
        if (itemAgg.getPackageScannedCount() != null) {
          sendDestDetail.setScannedPackCount(itemAgg.getPackageScannedCount().longValue());
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
      double loadVolume = 0.00;
      double loadWeight = 0.00;
      long waitScanCount = 0L;
      long scannedPackCount = 0L;
      long scannedBoxCount = 0L;
      long interceptedPackCount = 0L;
      for (JyComboardAggsEntity comboardAgg : comboardAggs) {
        loadVolume += comboardAgg.getVolume() == null ? 0.00 : comboardAgg.getVolume();
        loadWeight += comboardAgg.getWeight() == null ? 0.00 : comboardAgg.getWeight();
        waitScanCount += comboardAgg.getWaitScanCount() == null ? 0 : comboardAgg.getWaitScanCount();
        scannedPackCount += comboardAgg.getPackageScannedCount() == null ? 0 : comboardAgg.getPackageScannedCount();
        scannedBoxCount += comboardAgg.getBoxScannedCount() == null ? 0 : comboardAgg.getBoxScannedCount();
        interceptedPackCount += comboardAgg.getInterceptCount() == null ? 0 : comboardAgg.getInterceptCount();
      }
      progress.setLoadRate(dealLoadRate(BigDecimal.valueOf(loadVolume), convertTonToKg(BigDecimal.valueOf(basicVehicleType.getWeight()))));
      progress.setLoadVolume(BigDecimal.valueOf(loadVolume));
      progress.setLoadWeight(BigDecimal.valueOf(loadWeight));
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
    barCodeVo.setTotal(0L);
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
}
