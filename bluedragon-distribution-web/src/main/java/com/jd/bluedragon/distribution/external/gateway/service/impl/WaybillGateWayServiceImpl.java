package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.waybill.request.WaybillRouterReq;
import com.jd.bluedragon.common.dto.waybill.request.WaybillTrackReq;
import com.jd.bluedragon.common.dto.waybill.request.WaybillTrackResponse;
import com.jd.bluedragon.distribution.api.request.WaybillTrackReqVO;
import com.jd.bluedragon.distribution.api.response.WaybillTrackResVO;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.rest.base.BaseResource;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.track.WaybillTrackQueryService;
import com.jd.bluedragon.external.gateway.service.WaybillGateWayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 运单相关
 * @author : xumigen
 * @date : 2019/7/16
 */
public class WaybillGateWayServiceImpl implements WaybillGateWayService {

    private Logger log = LoggerFactory.getLogger(WaybillGateWayService.class);


    @Resource
    private BaseResource baseResource;

    @Autowired
    private WaybillTrackQueryService waybillTrackQueryService;

    @Autowired
    private RouterService routerService;
    
    @Override
    @BusinessLog(sourceSys = 1,bizType = 2006,operateType = 20061)
    @JProfiler(jKey = "DMSWEB.WaybillGateWayServiceImpl.getPerAndSfSiteByWaybill", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<List<Integer>> getPerAndSfSiteByWaybill(String waybillCode){
        InvokeResult<List<Integer>> invokeResult = baseResource.perAndSelfSite(waybillCode);
        JdCResponse<List<Integer>> jdCResponse = new JdCResponse<>();
        if(InvokeResult.RESULT_SUCCESS_CODE == invokeResult.getCode()){
            jdCResponse.toSucceed(invokeResult.getMessage());
            jdCResponse.setData(invokeResult.getData());
            return jdCResponse;
        }
        jdCResponse.toError(invokeResult.getMessage());
        return jdCResponse;
    }

    /**
     * 查询运单包裹列表，顺序从小到大
     * @param waybillCode
     * @return
     */
    @Override
    public JdCResponse<List<String>> queryPackageCodes(String waybillCode) {
        return retJdCResponse(waybillTrackQueryService.queryPackageCodes(waybillCode));
    }

    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        if(invokeResult == null){
            return null;
        }
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }

    /**
     * 运单全程跟踪历史记录
     * @param erp
     * @return
     */
    @Override
    public JdCResponse<List<String>> queryWaybillTrackHistory(String erp) {
        return retJdCResponse(waybillTrackQueryService.queryWaybillTrackHistory(erp));
    }

    @Override
    public JdCResponse<List<WaybillTrackResponse>> queryWaybillTrack(WaybillTrackReq waybillTrackReq) {
        JdCResponse<List<WaybillTrackResponse>> jdCResponse = new JdCResponse<>();
        WaybillTrackReqVO 
                commonRequest = new WaybillTrackReqVO();
        BeanUtils.copyProperties(waybillTrackReq, commonRequest);
        InvokeResult<List<WaybillTrackResVO>> commonResult = waybillTrackQueryService.queryWaybillTrack(commonRequest);
        if(commonResult == null){
            return null;
        }
        jdCResponse.setCode(commonResult.getCode());
        jdCResponse.setMessage(commonResult.getMessage());
        if(!CollectionUtils.isEmpty(commonResult.getData())){
            List<WaybillTrackResponse> list = Lists.newArrayList();
            for (WaybillTrackResVO tmp : commonResult.getData()) {
                WaybillTrackResponse waybillTrackResponse = new WaybillTrackResponse();
                BeanUtils.copyProperties(tmp, waybillTrackResponse);
                list.add(waybillTrackResponse);
            }
            jdCResponse.setData(list);
        }
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.WaybillGateWayServiceImpl.getRouterNextSiteByWaybillCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdCResponse<Integer> getRouterNextSiteByWaybillCode(WaybillRouterReq req) {
        JdCResponse<Integer> response = new JdCResponse<>();
        if (Objects.isNull(req) || StringUtils.isBlank(req.getWaybillCode())){
            response.toError("运单号不能为空");
            return response;
        }
        BaseStaffSiteOrgDto routerNextSite = routerService.getRouterNextSite(req.getCurrentOperate().getSiteCode(), req.getWaybillCode());
        if (log.isInfoEnabled()){
            log.info("WaybillGateWayServiceImpl.getRouterNextSite 返回：{}", JsonHelper.toJson(routerNextSite));
        }
        if (Objects.nonNull(routerNextSite)){
            response.toSucceed();
            response.setData(routerNextSite.getSiteCode());
        }else {
            response.toError("当前包裹查询路由下一节点失败");
        }
        return response;
    }
}
