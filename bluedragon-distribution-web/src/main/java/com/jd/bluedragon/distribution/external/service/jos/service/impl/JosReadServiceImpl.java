package com.jd.bluedragon.distribution.external.service.jos.service.impl;

import com.jd.bluedragon.distribution.external.jos.service.JosReadService;
import com.jd.bluedragon.distribution.jsf.domain.WhemsWaybillResponse;
import com.jd.bluedragon.distribution.send.service.ReverseDeliveryService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yangbo7 on 2015/9/1.
 */
@Service("josReadService")
public class JosReadServiceImpl implements JosReadService {

    private final Logger logger = Logger.getLogger(JosReadServiceImpl.class);

    @Autowired
    ReverseDeliveryService reverseDelivery;

    @Override
    public WhemsWaybillResponse getWhemsWaybill(List<String> request) {
        this.logger.error("武汉邮政接口调用运单数据，运单号请求列表："+ request.toString());
        return reverseDelivery.getWhemsWaybill(request);
    }
}
