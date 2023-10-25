package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.collectpackage.request.*;
import com.jd.bluedragon.common.dto.collectpackage.response.*;

/**
 * 小件集包网关服务
 */
public interface JyCollectPackageGatewayService {

  /**
   * 集包扫描
   */
  JdCResponse<CollectPackageResp> collectScan(CollectPackageReq request);

  /**
   * 查询集包任务列表
   * @param request
   * @return
   */
  JdCResponse<CollectPackageTaskResp> listCollectPackageTask(CollectPackageTaskReq request);

  /**
   * 检索集包任务
   * @param request
   * @return
   */
  JdCResponse<CollectPackageTaskResp> searchPackageTask(SearchPackageTaskReq request);


  /**
   * 查询任务详情
   * @param request
   * @return
   */
  JdCResponse<TaskDetailResp> queryTaskDetail(TaskDetailReq request);


  /**
   * 封箱（支持单个/批量箱号进行封箱）
   * @param request
   * @return
   */
  JdCResponse<SealingBoxResp> sealingBox(SealingBoxReq request);

  /**
   * 绑定集包袋
   * @param request
   * @return
   */
  JdCResponse<BindCollectBagResp> bindCollectBag(BindCollectBagReq request);

  /**
   *  取消集包
   * @param request
   * @return
   */
  JdCResponse<CancelCollectPackageResp> cancelCollectPackage(CancelCollectPackageReq request);

  /**
   * 查询任务下的统计数据
   * @param request
   * @return
   */
  JdCResponse<StatisticsUnderTaskQueryResp> queryStatisticsUnderTask(StatisticsUnderTaskQueryReq request);


  /**
   * 查询任务某个流向下的统计数据
   * @param request
   * @return
   */
  JdCResponse<StatisticsUnderFlowQueryResp> queryStatisticsUnderFlow(StatisticsUnderFlowQueryReq request);




}
