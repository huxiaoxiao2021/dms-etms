package com.jd.bluedragon.distribution.consumer.jy.send;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.jmq.common.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author weixiaofeng12
 * @date 2022-12-10 11:45
 */
@Service("boxCancelSendConsumer")
@Slf4j
public class BoxCancelSendConsumer extends MessageBaseConsumer  {
    @Autowired
    DeliveryService deliveryService;
    
    @Override
    public void consume(Message message) throws Exception {
        if (ObjectHelper.isEmpty(message)){
            log.error("boxCancelSendConsumer 获取消息为空！");
            return;
        }
        SendM sendM = JsonHelper.fromJson(message.getText(),SendM.class);
        if (ObjectHelper.isEmpty(sendM)){
            log.error("boxCancelSendConsumer body体为空！");
            return;
        }

        if (ObjectHelper.isEmpty(sendM.getBoxCode()) || !BusinessUtil.isBoxcode(sendM.getBoxCode())){
            log.error("非箱号类型的拆分组板任务，不支持！");
            return;
        }
        log.info("BoxCancelSendConsumer data:{}",message.getText());

        ThreeDeliveryResponse tDResponse = deliveryService.dellCancelDeliveryMessageWithServerTime(sendM, true);
        if (ObjectHelper.isNotNull(tDResponse) && !JdCResponse.CODE_SUCCESS.equals(tDResponse.getCode())) {
            log.info("大箱取消发货拆分成消息执行取消发货失败：取消：{},结果:{}",JsonHelper.toJson(sendM),JsonHelper.toJson(tDResponse));
        }
    }
}
