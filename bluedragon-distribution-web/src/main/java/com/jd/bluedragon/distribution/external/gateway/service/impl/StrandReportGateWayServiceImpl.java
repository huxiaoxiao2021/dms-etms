package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.strandreport.request.StrandReportReq;
import com.jd.bluedragon.distribution.abnormal.domain.StrandReportRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.rest.abnormal.StrandResouce;
import com.jd.bluedragon.external.gateway.service.StrandReportGateWayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class StrandReportGateWayServiceImpl implements StrandReportGateWayService {
    private final Logger logger = LoggerFactory.getLogger(StrandReportGateWayServiceImpl.class);

    @Autowired
    StrandResouce strandResouce;

  @Override
  @JProfiler(
    jKey = "DMSWEB.StrandReportGateWayServiceImpl.report",
    jAppName = Constants.UMP_APP_NAME_DMSWEB,
    mState = {JProEnum.TP, JProEnum.FunctionError}
  )
  public JdCResponse<Boolean> report(StrandReportReq request) {
    JdCResponse<Boolean> res = new JdCResponse<>();
    res.toSucceed();

    if (null == request) {
      res.toFail("入参不能为空");
      return res;
    }

    StrandReportRequest Resouce_req = new StrandReportRequest();
    if (null != request.getUser()) {
      Resouce_req.setUserCode(request.getUser().getUserCode());
      Resouce_req.setUserName(request.getUser().getUserName());
    }
    if (null != request.getCurrentOperate()) {
      Resouce_req.setSiteCode(request.getCurrentOperate().getSiteCode());
      Resouce_req.setSiteName(request.getCurrentOperate().getSiteName());
    }
    Resouce_req.setReasonCode(request.getReasonCode());
    Resouce_req.setReasonMessage(request.getReasonMessage());
    Resouce_req.setBarcode(request.getBarcode());
    Resouce_req.setReportType(request.getReportType());

    InvokeResult<Boolean> invokeResult = strandResouce.report(Resouce_req);
    if (null == invokeResult) {
      res.toFail("上报异常");
    } else {
      res.setCode(invokeResult.getCode());
      res.setMessage(invokeResult.getMessage());
    }

    return res;
    }
}
