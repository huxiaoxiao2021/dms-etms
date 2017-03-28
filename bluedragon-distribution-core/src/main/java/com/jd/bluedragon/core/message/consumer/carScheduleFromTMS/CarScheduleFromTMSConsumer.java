package com.jd.bluedragon.core.message.consumer.carScheduleFromTMS;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.carSchedule.domain.CarScheduleTo;
import com.jd.bluedragon.distribution.carSchedule.service.CarScheduleService;
import com.jd.jmq.common.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * Created by wuzuxiang on 2017/3/3.
 */
@Service("carScheduleFromTMSConsumer")
public class CarScheduleFromTMSConsumer extends MessageBaseConsumer{
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    CarScheduleService carScheduleService;

    private static final Gson GSON_MILLIONSECOND_FORMAT=new GsonBuilder()
            .enableComplexMapKeySerialization()
            .serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .setPrettyPrinting().create();

    /**
     * 消费TMS的发车消息：发车计划
     * @param message
     * @throws Exception
     */
    @Override
    public void consume(Message message) throws Exception {
        if(null == message && null == message.getText() && "".equals(message.getText())){
            this.logger.error("来自TMS的车辆调度任务消息内容为空,消息businessID：" + message.getBusinessId());
        }
        String body = message.getText();
        this.logger.info(MessageFormat.format("来自TMS的车辆调度任务消息内容为空,消息businessID：{0},消息内容为：{1}",message.getBusinessId(),body));
        CarScheduleTo carScheduleTo = new CarScheduleTo();
        try{
            carScheduleTo = GSON_MILLIONSECOND_FORMAT.fromJson(body,CarScheduleTo.class);
        }catch (JsonSyntaxException e){
            this.logger.error(MessageFormat.format("来自TMS的车辆调度任务消息序列化失败--消息businessID为：{0}，消息内容为：{1}",message.getBusinessId(),body),e);
            throw new Exception("来自TMS的车辆调度任务消息消费序列化失败--消息businessId："+message.getBusinessId()+",消息内容为："+body,e);
        }
        if(null == carScheduleTo){
            return;
        }

        /**来自TMS的车辆任务持久化**/
        try{
            carScheduleService.persistData(carScheduleTo);
        }catch(Exception e){
            this.logger.error("来自TMS的车辆运输任务消息持久化失败,消息内容为："+body,e);
            throw new Exception("来自TMS的车辆运输任务消息持久化失败,消息内容为："+body,e);
        }
    }
}
