package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.external.domain.BoardQueryRequest;
import com.jd.bluedragon.distribution.external.domain.BoardQueryResponse;
import com.jd.bluedragon.distribution.external.domain.CountBoardRequest;
import com.jd.bluedragon.distribution.external.domain.CountBoardResponse;
import com.jd.bluedragon.distribution.external.domain.QueryBelongBoardRequest;
import com.jd.bluedragon.distribution.external.domain.QueryBelongBoardResponse;

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
}
