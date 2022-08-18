package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.api.BizTaskService;
import com.jd.bluedragon.distribution.jy.api.BizType;
import com.jd.bluedragon.distribution.jy.dto.JyBizTaskMessage;
import com.jd.bluedragon.distribution.jy.dto.exception.ExpefResultNotify;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
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

/**
 * 三无匹配结果通知接口
 */
@Service("jyExpefNotifyConsumer")
public class JyExpefNotifyConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(JyExpefNotifyConsumer.class);

    @Autowired
    private JyExceptionService jyExceptionService;

    @Override
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("jyExpefNotifyConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("jyExpefNotifyConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }

        ExpefResultNotify mqDto = JsonHelper.fromJson(message.getText(), ExpefResultNotify.class);
        //todo jy_exception补充包裹号，jy_biz_task_exception 修改状态，完成情况需要，告诉任务调度系统任务完成
        jyExceptionService.expefResultNotify(mqDto);
    }


}
