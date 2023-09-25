package com.jd.bluedragon.distribution.capability.send.handler.deal;

import com.jd.bluedragon.distribution.capability.send.domain.SendOfCAContext;
import com.jd.bluedragon.distribution.capability.send.handler.SendDimensionStrategyHandler;
import com.jd.bluedragon.distribution.abnormal.service.DmsOperateHintService;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/8/27
 * @Description:  发货加急提示
 */
@Service
public class SendUrgentHintHandler extends SendDimensionStrategyHandler {

    @Autowired
    private DmsOperateHintService dmsOperateHintService;

    @Autowired
    private DeliveryService deliveryService;

    /**
     * 加急提示只处理包裹维度
     * @param context
     * @return
     */
    @Override
    public boolean doPackHandler(SendOfCAContext context) {
        String hints = dmsOperateHintService.getDeliveryHintMessageByWaybillCode(WaybillUtil.getWaybillCode(context.getRequestTurnToSendM().getBoxCode()));
        if (StringUtils.isNotBlank(hints)) {
            //发货环节产生加急提示，发加急提示追踪的mq消息
            deliveryService.sendDmsOperateHintTrackMQ(context.getRequestTurnToSendM());
            context.getResponse().getData().setKey(SendResult.CODE_WARN);
            context.getResponse().getData().setValue(hints);
        }
        return true;
    }


}
