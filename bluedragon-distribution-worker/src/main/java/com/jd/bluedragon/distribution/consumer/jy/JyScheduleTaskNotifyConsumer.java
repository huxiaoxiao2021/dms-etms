package com.jd.bluedragon.distribution.consumer.jy;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.api.BizTaskService;
import com.jd.bluedragon.distribution.jy.api.BizType;
import com.jd.bluedragon.distribution.jy.dto.JyBizTaskMessage;
import com.jd.bluedragon.utils.*;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;


@Service("jyScheduleTaskNotifyConsumer")
public class JyScheduleTaskNotifyConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(JyScheduleTaskNotifyConsumer.class);

    @Autowired
    private UccPropertyConfiguration uccConfig;

    @Override
    @JProfiler(jKey = "DMS.WORKER.jyScheduleTaskNotifyConsumer.consume",
            jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("JyScheduleTaskConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("JyScheduleTaskConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }

        JyBizTaskMessage mqDto = JsonHelper.fromJson(message.getText(), JyBizTaskMessage.class);
        BizTaskService bizTaskService = bizTaskServoce(mqDto.getTaskType());
        bizTaskService.bizTaskNotify(mqDto);
    }

    private BizTaskService bizTaskServoce(String taskType) {
        Map<String, BizTaskService> beans = SpringHelper.getBeans(BizTaskService.class);
        for (Map.Entry<String,BizTaskService> entry:beans.entrySet()){
            BizType annotation = entry.getClass().getAnnotation(BizType.class);
            for (JyScheduleTaskTypeEnum en:annotation.value()){
                if (Objects.equals(en.getCode(),taskType)){
                    return entry.getValue();
                }
            }
        }
        return null;
    }

}
