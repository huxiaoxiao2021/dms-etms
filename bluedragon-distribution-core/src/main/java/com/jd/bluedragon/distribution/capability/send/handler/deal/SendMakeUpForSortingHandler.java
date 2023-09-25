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
 * @Description:  触发补充分拣逻辑
 */
@Service
public class SendMakeUpForSortingHandler extends SendDimensionStrategyHandler {

    @Autowired
    private DeliveryService deliveryService;

    /**
     * 包裹维度处理逻辑
     * @param context
     * @return
     */
    @Override
    public boolean doPackHandler(SendOfCAContext context) {
        deliveryService.pushSorting(context.getRequestTurnToSendM());
        return true;
    }

}
