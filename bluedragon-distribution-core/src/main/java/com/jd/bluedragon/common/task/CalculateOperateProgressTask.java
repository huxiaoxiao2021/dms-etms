package com.jd.bluedragon.common.task;

import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.service.send.IJySendVehicleService;
import com.jd.bluedragon.distribution.jy.service.send.JySendAggsService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;

/**
 * 计算装车进度并生产消息
 */
public class CalculateOperateProgressTask implements Runnable{

  private JyBizTaskSendVehicleEntity taskSend;
  private JySendAggsService sendAggService;
  private IJySendVehicleService jySendVehicleService;

  public CalculateOperateProgressTask(
      JyBizTaskSendVehicleEntity taskSend,
      JySendAggsService sendAggService,
      IJySendVehicleService jySendVehicleService) {
    this.taskSend = taskSend;
    this.sendAggService = sendAggService;
    this.jySendVehicleService = jySendVehicleService;
  }

  @Override
  public void run() {
      JySendAggsEntity sendAgg = sendAggService.getVehicleSendStatistics(taskSend.getBizId());
      jySendVehicleService.calculateOperateProgress(sendAgg,true);
  }
}
