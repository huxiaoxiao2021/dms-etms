package com.jd.bluedragon.distribution.external.service.jos.service.impl;

import com.jd.bluedragon.distribution.external.jos.service.JosReadService;
import com.jd.bluedragon.distribution.jsf.domain.WhemsWaybillResponse;
import com.jd.bluedragon.distribution.send.service.ReverseDeliveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yangbo7 on 2015/9/1.
 */
@Service("josReadService")
public class JosReadServiceImpl implements JosReadService {

    private final Logger log = LoggerFactory.getLogger(JosReadServiceImpl.class);

    @Autowired
    ReverseDeliveryService reverseDelivery;

    @Override
    public WhemsWaybillResponse getWhemsWaybill(List<String> request) {
        this.log.warn("武汉邮政接口调用运单数据，运单号请求列表：{}", request.toString());
        if (null == request || request.size() > 100){
            this.log.warn("武汉邮政接口调用运单数据，请求运单数量大于100条");
            return new WhemsWaybillResponse(400,"超出一百条限制");
        }
        return reverseDelivery.getWhemsWaybill(request);
    }
}
