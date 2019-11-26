package com.jd.bluedragon.distribution.consumer.carScheduleFromTMS;

import com.google.gson.*;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.carSchedule.domain.CarScheduleTo;
import com.jd.bluedragon.distribution.carSchedule.service.CarScheduleService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by wuzuxiang on 2017/3/3.
 */
@Service("carScheduleFromTMSConsumer")
public class CarScheduleFromTMSConsumer extends MessageBaseConsumer{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CarScheduleService carScheduleService;

    private static final Gson GSON_MILLIONSECOND_FORMAT=new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setExclusionStrategies()
            .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .registerTypeAdapter(Timestamp.class, new JsonSerializer<Timestamp>() {
                @Override
                public JsonElement serialize(Timestamp src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(src));
                }
            })
            .create();

    /**
     * 消费TMS的发车消息：发车计划
     * @param message
     * @throws Exception
     */
    @Override
    public void consume(Message message) throws Exception {
        if(null == message || StringUtils.isEmpty(message.getText())){
            this.log.warn("来自TMS的车辆调度任务消息内容为空：{}" , JsonHelper.toJson(message));
            return;
        }
        String body = message.getText();
        this.log.debug("来自TMS的车辆调度任务消息内容为空,消息businessID：{},消息内容为：{}",message.getBusinessId(),body);
        CarScheduleTo carScheduleTo = new CarScheduleTo();
        try{
            carScheduleTo = JsonHelper.fromJsonUseGson(body,CarScheduleTo.class);
        }catch (JsonSyntaxException e){
            this.log.error("来自TMS的车辆调度任务消息序列化失败--消息businessID为：{}，消息内容为：{}",message.getBusinessId(),body,e);
            throw new Exception("来自TMS的车辆调度任务消息消费序列化失败--消息businessId："+message.getBusinessId()+",消息内容为："+body,e);
        }
        if(null == carScheduleTo){
            return;
        }

        /**来自TMS的车辆任务持久化**/
        try{
            carScheduleService.persistData(carScheduleTo);
        }catch(Exception e){
            this.log.error("来自TMS的车辆运输任务消息持久化失败,消息内容为：{}",body,e);
            throw new Exception("来自TMS的车辆运输任务消息持久化失败,消息内容为："+body,e);
        }
    }
}
