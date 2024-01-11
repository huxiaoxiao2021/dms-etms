package com.jd.bluedragon.distribution.capability.send.handler.lock;

import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.handler.SendDimensionStrategyHandler;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


 /**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/8/27
 * @Description: 发货锁，目前暂时沿用老锁，待后续有时间后重构锁
 */
@Service
public class SendLockHandler extends SendDimensionStrategyHandler {

    @Autowired
    private DeliveryService deliveryService;

    /**
     * 运单锁
     * @param context
     * @return
     */
    @Override
    public boolean doWaybillHandler(SendOfCAContext context) {
        if(!deliveryService.lockWaybillSend(context.getWaybill().getWaybillCode(),
                context.getRequestTurnToSendM().getCreateSiteCode(), context.getWaybill().getQuantity())){
            context.getResponse().getData().init(SendResult.CODE_SENDED, DeliveryResponse.MESSAGE_DELIVERY_ALL_PROCESSING);
            return false;
        }
        return true;
    }
}
