package com.jd.bluedragon.distribution.delivery.processor;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.delivery.callback.IDeliveryProcessCallback;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author wyh
 * @className DeliveryCoreProcessor
 * @description
 * @date 2021/8/3 21:35
 **/
@Service("deliveryCoreProcessor")
public class DeliveryCoreProcessor implements IDeliveryCoreProcessor {

    private static final Logger log = LoggerFactory.getLogger(DeliveryCoreProcessor.class);

    @Autowired
    private DeliveryService deliveryService;

    @Override
    public DeliveryResponse process(List<SendM> sendMList, IDeliveryProcessCallback<SendM> callback) {

        deliveryService.deliveryCoreLogic(sendMList.get(0).getBizSource(), sendMList);

        // 回调
        callback.callback(sendMList);

        return new DeliveryResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
    }

}
