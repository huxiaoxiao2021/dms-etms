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
 * @Description:
 *
 * 中转发货处理逻辑
 *
 */
@Service
public class SendTransitHandler extends SendDimensionStrategyHandler {


    @Autowired
    private DeliveryService deliveryService;

    /**
     * 中转发货逻辑智处理按箱发货维度
     * @param context
     * @return
     */
    @Override
    public boolean doBoxHandler(SendOfCAContext context) {
        //不关心此方法的返回值，只按成功处理
        deliveryService.transitSend(context.getRequestTurnToSendM());

        return true;
    }
}
