package com.jd.bluedragon.distribution.cyclebox;

import com.jd.bluedragon.common.dto.box.response.BoxCodeGroupBinDingDto;
import com.jd.bluedragon.distribution.api.request.BoxMaterialRelationRequest;
import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.request.OrderBindMessageRequest;
import com.jd.bluedragon.distribution.api.request.RecyclableBoxRequest;
import com.jd.bluedragon.distribution.api.request.WaybillCodeListRequest;
import com.jd.bluedragon.distribution.api.response.box.BCGroupBinDingDto;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelation;
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


  /**
   * 绑定集包袋校验逻辑
   *  1.集包袋规则校验
   *  2.该箱号已发货，不能再绑定集包袋
   *  3. 如果本地场地已经绑定了箱号 而且已在4小时内发货 ，不能再绑定
   *  4. 若循环集包袋绑定的最近2个箱号在本场地发货过，，且发货的目的地相同时，弹窗提示「疑似虚假操作，当前AD码已锁定，请使用其它循环袋」，并禁止操作集包袋绑定
   *  5.此集包袋号曾经存在绑定箱号时做此校验 此场景使用的箱号是集包袋曾经绑定的箱号 集包袋绑定的箱号，在当前场地操作收箱 集包袋绑定的箱号的目的地不是本场地
   * @param request
   * @return
   */
  InvokeResult boxMaterialRelationAlterOfCheck(BoxMaterialRelationRequest request);

  /**
   * 外单靑流箱绑定发MQ
   * @param request
   * @return
   */
    InvokeResult cycleBoxBindToWD(OrderBindMessageRequest request) throws Exception;

  /**
   * 获取箱号绑定状态
   * @param request
   * @return
   */
  InvokeResult<BoxCodeGroupBinDingDto> checkBingResult(BoxMaterialRelationRequest request);

  /**
   * 校验一组BC箱号绑定情况
   * @param request
   * @return
   */
  InvokeResult<BCGroupBinDingDto> checkGroupBingResult(BoxMaterialRelationRequest request);

  /**
   * 根据箱号批量获取箱号绑定的集包袋
   * @param boxCodeList
   * @return
   */
   List<BoxMaterialRelation> getBoxMaterialRelationList(List<String> boxCodeList);
}
