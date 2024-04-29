package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.external.domain.*;

public interface DmsComboardService {

  /**
   * 根据流向查询组板列表
   */
  InvokeResult<BoardQueryResponse> listComboardBySendFlow(BoardQueryRequest request);

  /**
   * 根据包裹号或者箱号 定位所在的板
   */
  InvokeResult<QueryBelongBoardResponse> queryBelongBoardByBarCode(QueryBelongBoardRequest request);


  /**
   * 统计派车任务下每个流向的组板数量
   * @param request
   * @return
   */
  InvokeResult<CountBoardResponse> countBoardGroupBySendFlow(CountBoardRequest request);

  /**
   * 根据bizId查询组板任务的司机违规举报数据
   * @param request
   * @return
   */
  InvokeResult<DriverViolationReportingResponse> getDriverViolationReportingByBizId(
      QueryDriverViolationReportingReq request);

}
