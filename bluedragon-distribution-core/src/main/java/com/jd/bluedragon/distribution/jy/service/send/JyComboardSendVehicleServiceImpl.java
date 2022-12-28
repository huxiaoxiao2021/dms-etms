package com.jd.bluedragon.distribution.jy.service.send;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendDetailRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.BaseSendVehicle;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendDestDetail;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleData;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleDetail;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleStatusStatis;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendCountDto;
import com.jd.bluedragon.distribution.jy.dto.send.QueryTaskSendDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendSortTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyComboardLineTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JySendLineTypeEnum;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.utils.NumberHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.jy.comboard.JyComboardAggsEntity;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardAggsService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jddl.executor.function.scalar.filter.In;
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

      List<JyComboardAggsEntity> aggsList = jyComboardAggsService.queryComboardAggs(request.getCurrentOperate().getSiteCode(),endSiteIdList);
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
      detailVo.setItemStatus(vehicleDetail.getVehicleStatus());
      detailVo.setItemStatusDesc("");
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
}
