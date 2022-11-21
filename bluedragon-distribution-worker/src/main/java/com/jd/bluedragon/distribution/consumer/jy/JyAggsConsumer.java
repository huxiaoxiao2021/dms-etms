package com.jd.bluedragon.distribution.consumer.jy;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.annotation.JyAggsType;
import com.jd.bluedragon.distribution.jy.api.BizTaskService;
import com.jd.bluedragon.distribution.jy.api.BizType;
import com.jd.bluedragon.distribution.jy.api.BizTypeProcessor;
import com.jd.bluedragon.distribution.jy.constants.JyAggsTypeEnum;
import com.jd.bluedragon.distribution.jy.dto.comboard.JyAggsDto;
import com.jd.bluedragon.distribution.jy.service.comboard.JyAggsService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SpringHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service("jyAggsConsumer")
public class JyAggsConsumer extends MessageBaseConsumer {
    private static final Logger logger = LoggerFactory.getLogger(JyAggsConsumer.class);

    @Autowired
    private BizTypeProcessor bizTypeProcessor;
    @Override
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("jyAggsConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("jyAggsConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        JyAggsDto jyAggsDto = JsonHelper.fromJson(message.getText(), JyAggsDto.class);
        if (jyAggsDto == null) {
            logger.error("jyAggsConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        if (JyAggsTypeEnum.JY_COMBOARD_AGGS == jyAggsDto.getJyAggsTypeEnum()) {
            //执行逻辑
        }
        JyAggsService jyAggsService = this.getJyAggsService(jyAggsDto.getJyAggsTypeEnum());


    }

    private JyAggsService getJyAggsService(JyAggsTypeEnum jyAggsTypeEnum){
        Map<String, JyAggsService> beans = SpringHelper.getBeans(JyAggsService.class);
        for (Map.Entry<String,JyAggsService> entry:beans.entrySet()){
            JyAggsType annotation = entry.getValue().getClass().getAnnotation(JyAggsType.class);
            for (JyAggsTypeEnum en:annotation.value()){
                if (Objects.equals(en,jyAggsTypeEnum)){
                    return entry.getValue();
                }
            }
        }
        return null;
    }

}
