package com.jd.bluedragon.distribution.capability.send.handler.verify;

import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.handler.SendDimensionStrategyHandler;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/8/27
 * @Description: 锁定校验
 */
@Service
public class SendLockVerifyHandler  extends SendDimensionStrategyHandler {


    @Autowired
    private DeliveryService deliveryService;


    /**
     * 包裹锁状态校验
     * @param context
     * @return
     */
    @Override
    public boolean doPackHandler(SendOfCAContext context) {

        if(deliveryService.isSendByWaybillProcessing(context.getRequestTurnToSendM())){
            context.getResponse().getData().init(SendResult.CODE_SENDED, HintService.getHint(HintCodeConstants.SEND_BY_WAYBILL_PROCESSING));
            return false;
        }
        return true;
    }

    /**
     * 运单锁状态校验
     * @param context
     * @return
     */
    @Override
    public boolean doWaybillHandler(SendOfCAContext context) {
        if(deliveryService.isSendByWaybillProcessing(context.getRequestTurnToSendM())){
            context.getResponse().getData().init(SendResult.CODE_SENDED, HintService.getHint(HintCodeConstants.SEND_BY_WAYBILL_PROCESSING));
            return false;
        }
        return true;
    }

}
