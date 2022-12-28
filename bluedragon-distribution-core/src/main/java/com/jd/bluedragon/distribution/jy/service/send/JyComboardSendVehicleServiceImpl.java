package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendDetailRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendDestDetail;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

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
