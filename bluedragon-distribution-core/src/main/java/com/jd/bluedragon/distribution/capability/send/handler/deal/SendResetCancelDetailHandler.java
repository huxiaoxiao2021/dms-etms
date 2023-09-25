package com.jd.bluedragon.distribution.capability.send.handler.deal;

import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.handler.SendDimensionStrategyHandler;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/8/27
 * @Description:
 */
@Service
public class SendResetCancelDetailHandler extends SendDimensionStrategyHandler {

    @Autowired
    private DeliveryService deliveryService;

    /**
     * 只需要处理按箱发货维度
     * @param context
     * @return
     */
    @Override
    public boolean doBoxHandler(SendOfCAContext context) {
        //重置发货明细取消状态
        SendM domain = context.getRequestTurnToSendM();
        // 按箱
        SendDetail sendDetail = new SendDetail();
        sendDetail.setBoxCode(domain.getBoxCode());
        sendDetail.setCreateSiteCode(domain.getCreateSiteCode());
        sendDetail.setReceiveSiteCode(domain.getReceiveSiteCode());
        //更新SEND_D状态
        deliveryService.updateCancel(sendDetail);

        return true;
    }
}
