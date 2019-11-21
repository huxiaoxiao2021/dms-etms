package com.jd.bluedragon.distribution.consumer.carScheduleFromTMS;

import com.google.gson.JsonSyntaxException;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.carSchedule.domain.CancelScheduleTo;
import com.jd.bluedragon.distribution.carSchedule.service.CarScheduleService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by wuzuxiang on 2017/3/6.
 */
@Service("cancelScheduleFromTMSConsumer")
public class CancelScheduleFromTMSConsumer extends MessageBaseConsumer{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CarScheduleService carScheduleService;

    /**
     * 取消发车
     * @param message
     * @throws Exception
     */
    @Override
    public void consume(Message message) throws Exception {
        if(null == message || null == message.getText()){
            this.log.warn("来自TMS的取消发车消息为空，执行结束。。。");
            return;
        }
        this.log.info("来自TMS的取消发车消息,消息businessID为{},消息体为{}",message.getBusinessId(),message.getText());
        String body = message.getText();
        CancelScheduleTo cancelScheduleTo = new CancelScheduleTo();
        try{
            cancelScheduleTo = JsonHelper.fromJsonUseGson(body,CancelScheduleTo.class);
        }catch(JsonSyntaxException e){
            this.log.error("来自TMS的取消发车消息消费失败，消息businessID为{}，内容为{}",message.getBusinessId(),message.getText(),e);
            throw new JMQException(e,400);
        }

        if(null == cancelScheduleTo){
            return;
        }
        /**
         * 来自TMS的取消发车的消息持久化
         */
        Boolean bool = Boolean.FALSE;
        try{
            bool = carScheduleService.cancelSchedule(cancelScheduleTo);
        }catch(Exception e){
            this.log.error("小概率事件-->执行carScheduleService.cancelSchedule方法异常：{}",body,e);
            throw new JMQException(e,400);
        }

        if(bool == Boolean.FALSE){
            this.log.warn("取消发车的消息消费失败，请检查是否有发车记录:{}",body);
//            throw new JMQException("can not deal with the cancel_car_message,maybe the send_car_info is on it's slow way.please check it the message:"+body,400);
        }

    }
}
