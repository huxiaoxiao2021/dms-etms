package com.jd.bluedragon.distribution.capability.send.handler.lock;

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
 * @Description: 释放发货锁，目前暂时沿用老锁，待后续有时间后重构锁
 */
@Service
public class SendUnLockHandler extends SendDimensionStrategyHandler {

    @Autowired
    private DeliveryService deliveryService;

    /**
     * 是否必须执行，返回true时不允许跳过,必须执行。
     * @return
     */
    @Override
    public boolean mustHandler() {
        return true;
    }

    @Override
    public boolean doHandler(SendOfCAContext context) {
        //释放锁处理
        return super.doHandler(context);
    }

    /**
     * 运单维度释放锁
     * @param context
     * @return
     */
    @Override
    public boolean doWaybillHandler(SendOfCAContext context) {
        deliveryService.unlockWaybillSend(context.getWaybill().getWaybillCode(),
                context.getRequestTurnToSendM().getCreateSiteCode());
        return true;
    }
}
