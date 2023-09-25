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
 *       取消最近一次的发货逻辑
 */
@Service
public class SendCancelLastHandler extends SendDimensionStrategyHandler {


    @Autowired
    private DeliveryService deliveryService;

    /**
     * 取消最近一次发货逻辑
     * 所有发货维度执行逻辑一样，重新此方法即可
     * @param context
     * @return
     */
    @Override
    public boolean doHandler(SendOfCAContext context) {

        boolean isCancelLastSend = context.getRequest().getIsCancelLastSend();

        //取消上次发货逻辑 只有使用者选择时取消时再去取消
        if (isCancelLastSend) {
            deliveryService.doCancelLastSend(context.getRequestTurnToSendM());
        }

        return true;
    }
}
