package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.abnormal.request.AbnormalOrdRequest;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.AbnormalOrderRequest;
import com.jd.bluedragon.distribution.api.response.AbnormalOrderResponse;
import com.jd.bluedragon.distribution.rest.abnormalorder.AbnormalOrderResource;
import com.jd.bluedragon.external.gateway.service.AbnormalOrderGatewayService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author : xumigen
 * @date : 2019/9/10
 */
public class AbnormalOrderGatewayServiceImpl implements AbnormalOrderGatewayService {

    @Resource
    private AbnormalOrderResource abnormalOrderResource;

    @Override
    @JProfiler(jKey = "DMSWEB.AbnormalOrderGatewayServiceImpl.pushAbnormalOrder",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> pushAbnormalOrder(AbnormalOrdRequest abnormalOrdRequest){
        JdCResponse<Void> jdCResponse = new JdCResponse<>();
        AbnormalOrderRequest request = new AbnormalOrderRequest();
        request.setWaveBusinessId(abnormalOrdRequest.getWaveBusinessId());
        request.setOrderId(abnormalOrdRequest.getOrderId());
        request.setAbnormalCode1(abnormalOrdRequest.getAbnormalCode1());
        request.setAbnormalReason1(abnormalOrdRequest.getAbnormalReason1());
        request.setAbnormalCode2(abnormalOrdRequest.getAbnormalCode2());
        request.setAbnormalReason2(abnormalOrdRequest.getAbnormalReason2());
        request.setCreateUserErp(abnormalOrdRequest.getUser().getUserErp());
        request.setTrackContent(abnormalOrdRequest.getTrackContent());
        request.setUserCode(abnormalOrdRequest.getUser().getUserCode());
        request.setUserName(abnormalOrdRequest.getUser().getUserName());
        request.setSiteCode(abnormalOrdRequest.getCurrentOperate().getSiteCode());
        request.setSiteName(abnormalOrdRequest.getCurrentOperate().getSiteName());
        request.setOperateTime(DateHelper.formatDate(abnormalOrdRequest.getCurrentOperate().getOperateTime(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));
        AbnormalOrderResponse response = abnormalOrderResource.pushAbnormalOrder(request);
        if(Objects.equals(JdResponse.CODE_OK,response.getCode())){
            jdCResponse.toSucceed(response.getMessage());
            return jdCResponse;
        }
        jdCResponse.toError(response.getMessage());
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.AbnormalOrderGatewayServiceImpl.queryAbnormalorder",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Void> queryAbnormalorder(String orderId,Integer type){
        JdCResponse<Void> jdCResponse = new JdCResponse<>();
        AbnormalOrderResponse abnormalOrderResponse = abnormalOrderResource.queryAbnormalorder(orderId,type);
        if(Objects.equals(JdResponse.CODE_OK,abnormalOrderResponse.getCode())){
            jdCResponse.toSucceed(abnormalOrderResponse.getMessage());
            return jdCResponse;
        }
        jdCResponse.toError(abnormalOrderResponse.getMessage());
        return jdCResponse;
    }
}
