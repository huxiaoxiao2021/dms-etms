package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.comboard.request.FindGoodsReq;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

public interface JyFindGoodsService {

  InvokeResult findGoodsScan(FindGoodsReq request);
}
