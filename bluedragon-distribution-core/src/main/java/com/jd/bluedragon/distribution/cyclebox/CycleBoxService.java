package com.jd.bluedragon.distribution.cyclebox;

import com.jd.bluedragon.distribution.api.request.BoxMaterialRelationRequest;
import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.request.RecyclableBoxRequest;
import com.jd.bluedragon.distribution.api.request.WaybillCodeListRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.cyclebox.domain.CycleBox;
import com.jd.bluedragon.distribution.task.domain.Task;

import java.util.List;

public interface CycleBoxService {
  /**
   * 获取青流箱数量
   *
   * @param request
   * @return
   */
  CycleBox getCycleBoxNum(List<DeliveryRequest> request);

  /**
   * 生成同步清流箱状态的任务
   *
   * @param request
   */
  void addCycleBoxStatusTask(WaybillCodeListRequest request);

  /**
   * 根据序列号获取青流箱信息
   *
   * @param batchCode
   * @return
   */
  CycleBox getCycleBoxByBatchCode(String batchCode);

  /**
   * 同步青流箱状态
   *
   * @param task
   */
  boolean pushCycleBoxStatus(Task task);

  /**
   * 循环箱发MQ
   *
   * @param request
   * @return
   */
  void recyclableBoxSend(RecyclableBoxRequest request) throws Exception;

    /**
     * 根据箱号获取箱号绑定的集包袋
     * @param boxCode
     * @return
     */
  String getBoxMaterialRelation(String boxCode);

    /**
     * 绑定、删除集包袋
     * @param request
     * @return
     */
    InvokeResult boxMaterialRelationAlter(BoxMaterialRelationRequest request);


}
