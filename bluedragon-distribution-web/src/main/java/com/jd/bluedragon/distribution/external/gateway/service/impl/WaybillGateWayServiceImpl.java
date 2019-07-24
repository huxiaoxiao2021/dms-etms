package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.rest.base.BaseResource;
import com.jd.bluedragon.external.gateway.service.WaybillGateWayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

import javax.annotation.Resource;
import java.util.List;

/**
 * 运单相关
 * @author : xumigen
 * @date : 2019/7/16
 */
public class WaybillGateWayServiceImpl implements WaybillGateWayService {

    @Resource
    private BaseResource baseResource;

    @Override
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

}
