package com.jd.bluedragon.distribution.material.service.impl;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationMQ;
import com.jd.bluedragon.distribution.material.service.DeliveryGoodsNoticeService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @ClassName DeliveryGoodsNoticeServiceImpl
 * @Description
 * @Author wyh
 * @Date 2020/8/24 10:48
 **/
@Service
public class DeliveryGoodsNoticeServiceImpl implements DeliveryGoodsNoticeService {

    @Autowired
    @Qualifier("deliverGoodsNoticeSendMQ")
    private DefaultJMQProducer deliverGoodsNoticeSendMQ;

    @Override
    public JdResult<Boolean> deliverySendGoodsMessage(BoxMaterialRelationMQ relationMQ) {
        JdResult<Boolean> result = new JdResult<>();
        result.toSuccess();
        String boxCode = relationMQ.getBoxCode();
        if (BusinessHelper.isBoxcode(boxCode)) {
            deliverGoodsNoticeSendMQ.sendOnFailPersistent(boxCode, JsonHelper.toJson(relationMQ));
        }
        return result;
    }
}
