package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.common.dto.comboard.request.BoardQueryReq;
import com.jd.bluedragon.common.dto.comboard.response.BoardQueryResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.external.domain.BoardQueryRequest;
import com.jd.bluedragon.distribution.external.domain.BoardQueryResponse;
import com.jd.bluedragon.distribution.external.domain.QueryBelongBoardRequest;
import com.jd.bluedragon.distribution.external.domain.QueryBelongBoardResponse;
import com.jd.bluedragon.distribution.external.service.DmsComboardService;
import com.jd.bluedragon.distribution.jy.service.seal.JySealVehicleService;
import com.jd.bluedragon.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("dmsComboardService")
public class DmsComboardServiceImpl implements DmsComboardService {


  @Override
  public InvokeResult<BoardQueryResponse> listComboardBySendFlow(BoardQueryRequest request) {
    return null;
  }

  @Override
  public InvokeResult<QueryBelongBoardResponse> queryBelongBoardByBarCode(
      QueryBelongBoardRequest request) {
    return null;
  }
}
