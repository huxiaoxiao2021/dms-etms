package com.jd.bluedragon.distribution.consumer.debon;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.debon.Dto.ReturnScheduleMq;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskMq;
import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybill;
import com.jd.bluedragon.distribution.reassignWaybill.service.ReassignWaybillService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/26 11:00
 * @Description:
 */
@Slf4j
@Service("debonReturnScheduleConsumer")
public class DebonReturnScheduleConsumer extends MessageBaseConsumer {

    @Autowired
    private ReassignWaybillService reassignWaybillService;

    @Override
    public void consume(Message message) throws Exception {

        if(log.isInfoEnabled()){
            log.info("debonReturnScheduleConsumer -message {}", message.getText());
        }
        if (StringHelper.isEmpty(message.getText())) {
            log.warn("debonReturnScheduleConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("debonReturnScheduleConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        ReassignWaybill reassignWaybill = JsonHelper.fromJson(message.getText(), ReassignWaybill.class);
        if (reassignWaybill == null) {
            log.error("debonReturnScheduleConsumer 消息丢弃,入参：{}", message.getText());
            return;
        }
        try{
            reassignWaybillService.addToDebon(reassignWaybill);
        }catch (Exception e){
            log.error("debonReturnScheduleConsumer  {} {}",  JsonHelper.toJson(message), JsonHelper.toJson(reassignWaybill));
            throw new RuntimeException("debonReturnScheduleConsumer 处理德邦返调度消息异常! ");
        }
    }
}
