package com.jd.bluedragon.distribution.capability.send.handler.deal;

import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.handler.SendDimensionStrategyHandler;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/8/27
 * @Description: 循环物资通知使用
 */
@Service
public class SendGoodsNoticeHandler extends SendDimensionStrategyHandler {

    @Autowired
    private DeliveryService deliveryService;

    /**
     * 所有维度处理逻辑一样
     * @param context
     * @return
     */
    @Override
    public boolean doHandler(SendOfCAContext context) {

        //发送发货业务通知MQ 目前只在循环集包袋场景使用
        deliveryService.deliverGoodsNoticeMQ(context.getRequestTurnToSendM());

        return true;
    }
}
