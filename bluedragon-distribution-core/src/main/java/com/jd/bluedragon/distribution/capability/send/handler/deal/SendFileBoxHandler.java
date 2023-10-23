package com.jd.bluedragon.distribution.capability.send.handler.deal;

import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.handler.SendDimensionStrategyHandler;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/9/9
 * @Description:
 */
@Service
public class SendFileBoxHandler extends SendDimensionStrategyHandler {


    @Autowired
    private DeliveryService deliveryService;

    /**
     * 文件箱套箱处理逻辑
     * @param context
     * @return
     */
    @Override
    public boolean doBoxHandler(SendOfCAContext context) {
        // 处理普通箱子内嵌套文件箱逻辑

        deliveryService.dealFileBoxSingleCarSend(
                SendBizSourceEnum.getEnum(context.getRequest().getBizSource()),
                context.getRequestTurnToSendM());

        return true;

    }
}
