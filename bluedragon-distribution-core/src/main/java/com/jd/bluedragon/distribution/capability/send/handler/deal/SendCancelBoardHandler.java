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
 * @Description:  自动取消组板逻辑处理,目前线上只配置了一个场地是否考虑可下线此逻辑
 */
@Service
public class SendCancelBoardHandler extends SendDimensionStrategyHandler {

    @Autowired
    private DeliveryService deliveryService;

    /**
     * 所有发货维度处理逻辑一样
     * @param context
     * @return
     */
    @Override
    public boolean doHandler(SendOfCAContext context) {

        // 自动取消组板
        deliveryService.autoBoardCombinationCancel(context.getRequestTurnToSendM());

        return true;
    }
}
