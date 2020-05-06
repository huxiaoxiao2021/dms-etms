package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.send.request.DeliveryVerifyRequest;
import com.jd.bluedragon.common.dto.send.request.DifferentialQueryRequest;
import com.jd.bluedragon.common.dto.send.request.SinglePackageSendRequest;
import com.jd.bluedragon.common.dto.send.request.TransPlanRequest;
import com.jd.bluedragon.common.dto.send.response.SendThreeDetailDto;
import com.jd.bluedragon.common.dto.send.response.TransPlanDto;
import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.request.PackageSendRequest;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.external.service.DmsDeliveryService;
import com.jd.bluedragon.distribution.rest.send.DeliveryResource;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.service.DeliveryVerifyService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.external.gateway.service.SendGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.List;

/**
 * <p>
 * Created by lixin39 on 2018/11/9.
 */
@Deprecated
@Service("dmsDeliveryService")
public class DmsDeliveryServiceImpl implements DmsDeliveryService,SendGatewayService {

    @Autowired
    @Qualifier("deliveryResource")
    private DeliveryResource deliveryResource;

    @Autowired
    @Qualifier("deliveryVerifyService")
    private DeliveryVerifyService deliveryVerifyService;

    @Override
    public InvokeResult<AbstractMap.Entry<Integer, String>> checkSendCodeStatus(String sendCode) {
        return deliveryResource.checkSendCodeStatus(sendCode);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsDeliveryServiceImpl.newPackageSend", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<SendResult> newPackageSend(PackageSendRequest request) {
        // 安卓PDA发货
        request.setBizSource(SendBizSourceEnum.ANDROID_PDA_SEND.getCode());
        return deliveryResource.newPackageSend(request);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsDeliveryServiceImpl.checkDeliveryInfo", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public DeliveryResponse checkDeliveryInfo(String boxCode, String siteCode, String receiveSiteCode, String businessType) {
        return deliveryResource.checkDeliveryInfo(boxCode, siteCode, receiveSiteCode, businessType);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsDeliveryServiceImpl.cancelDeliveryInfo", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult cancelDeliveryInfo(DeliveryRequest request) {
        ThreeDeliveryResponse response = deliveryResource.cancelDeliveryInfo(request);
        if (response != null) {
            InvokeResult result = new InvokeResult();
            result.setCode(response.getCode());
            result.setMessage(response.getMessage());
            result.setData(response.getData());
            return result;
        }
        return null;
    }

    @Override
    @Deprecated
    @JProfiler(jKey = "DMSWEB.DmsDeliveryServiceImpl.packageSendVerifyForBox", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public JdVerifyResponse packageSendVerifyForBox(DeliveryVerifyRequest request) {
        return deliveryVerifyService.packageSendVerifyForBoxCode(request);
    }

    @Override
    @Deprecated
    public JdVerifyResponse newPackageSendGoods(SinglePackageSendRequest request) {
        return null;
    }

    @Override
    @Deprecated
    public JdCResponse<List<TransPlanDto>> getTransPlan(TransPlanRequest request){
        return null;
    }

    @Override
    @Deprecated
    public JdCResponse<Boolean> checkJpWaybill(com.jd.bluedragon.common.dto.send.request.DeliveryRequest request){
        return null;
    }

    @Override
    @Deprecated
    public JdCResponse<Boolean> checkThreeDeliveryNew(com.jd.bluedragon.common.dto.send.request.DeliveryRequest request){
        return null;
    }

    @Override
    @Deprecated
    public JdCResponse<Boolean> cancelLastDeliveryInfo(com.jd.bluedragon.common.dto.send.request.DeliveryRequest request){
        return null;
    }

    @Override
    @Deprecated
    public JdCResponse<Boolean> checkThreeDelivery(List<com.jd.bluedragon.common.dto.send.request.DeliveryRequest> request){
        return null;
    }

    @Override
    @Deprecated
    public JdCResponse<Boolean> coldChainSendDelivery(List<com.jd.bluedragon.common.dto.send.request.DeliveryRequest> request){
        return null;
    }

    @Override
    @Deprecated
    public JdCResponse<List<SendThreeDetailDto>> differentialQuery(DifferentialQueryRequest request){
        return null;
    }
}
