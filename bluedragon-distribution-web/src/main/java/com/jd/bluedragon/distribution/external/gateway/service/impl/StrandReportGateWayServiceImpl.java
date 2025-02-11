package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.strandreport.request.ConfigStrandReasonData;
import com.jd.bluedragon.common.dto.strandreport.request.StrandReportReq;
import com.jd.bluedragon.distribution.abnormal.domain.StrandReportRequest;
import com.jd.bluedragon.distribution.abnormal.service.StrandService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.external.gateway.service.StrandReportGateWayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class StrandReportGateWayServiceImpl implements StrandReportGateWayService {

    @Autowired
    StrandService strandService;

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
    Resouce_req.setOperateTime(request.getOperateTime());
    Resouce_req.setBusinessType(10);

    InvokeResult<Boolean> invokeResult = strandService.report(Resouce_req);
    if (null == invokeResult) {
      res.toFail("上报异常");
    } else {
      res.setCode(invokeResult.getCode());
      res.setMessage(invokeResult.getMessage());
    }

    return res;
    }

  /**
   * 查询默认
   * @return
   */
  @Override
  @JProfiler(
    jKey = "DMSWEB.StrandReportGateWayServiceImpl.queryReasonList",
    jAppName = Constants.UMP_APP_NAME_DMSWEB,
    mState = {JProEnum.TP, JProEnum.FunctionError}
  )
  public JdCResponse<List<ConfigStrandReasonData>> queryReasonList() {
    JdCResponse<List<ConfigStrandReasonData>> res = new JdCResponse<>();
    
    InvokeResult<List<ConfigStrandReasonData>> invokeResult = strandService.queryReasonList();
    if (null == invokeResult) {
      res.toFail("获取异常原因列表失败！");
    } else {
    	res.setData(invokeResult.getData());
    	res.setCode(invokeResult.getCode());
    	res.setMessage(invokeResult.getMessage());
    }
    res.toSucceed();
    return res;
  }

  /**
   * 查询全部的滞留原因
   * 默认+冷链
   * @return
   */
  @Override
  @JProfiler(
          jKey = "DMSWEB.StrandReportGateWayServiceImpl.queryAllReasonList",
          jAppName = Constants.UMP_APP_NAME_DMSWEB,
          mState = {JProEnum.TP, JProEnum.FunctionError}
  )
  public JdCResponse<List<ConfigStrandReasonData>> queryAllReasonList() {
    JdCResponse<List<ConfigStrandReasonData>> res = new JdCResponse<>();

    InvokeResult<List<ConfigStrandReasonData>> invokeResult = strandService.queryAllReasonList();
    if (null == invokeResult) {
      res.toFail("获取异常原因列表失败！");
    } else {
      res.setData(invokeResult.getData());
      res.setCode(invokeResult.getCode());
      res.setMessage(invokeResult.getMessage());
    }
    res.toSucceed();
    return res;
  }

}
