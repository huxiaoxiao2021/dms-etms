package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.distribution.api.request.DeliveryRequest;
import com.jd.bluedragon.distribution.api.request.PackageSendRequest;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.external.service.DmsDeliveryService;
import com.jd.bluedragon.distribution.rest.send.DeliveryResource;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;

/**
 * <p>
 * Created by lixin39 on 2018/11/9.
 */
@Service("dmsDeliveryService")
public class DmsDeliveryServiceImpl implements DmsDeliveryService {

    @Autowired
    @Qualifier("deliveryResource")
    private DeliveryResource deliveryResource;

    @Override
    public InvokeResult<AbstractMap.Entry<Integer, String>> checkSendCodeStatus(String sendCode) {
        return deliveryResource.checkSendCodeStatus(sendCode);
    }

    @Override
    public InvokeResult<SendResult> newPackageSend(PackageSendRequest request) {
        // 安卓PDA发货
        request.setBizSource(SendBizSourceEnum.ANDROID_PDA_SEND.getCode());
        return deliveryResource.newPackageSend(request);
    }

    @Override
    public DeliveryResponse checkDeliveryInfo(String boxCode, String siteCode, String receiveSiteCode, String businessType) {
        return deliveryResource.checkDeliveryInfo(boxCode, siteCode, receiveSiteCode, businessType);
    }

    @Override
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
}
