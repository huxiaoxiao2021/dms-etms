package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ps.data.epf.dto.ExpefNotify;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 三无匹配结果通知接口
 */
@Service("jyExpefNotifyConsumer")
public class JyExpefNotifyConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(JyExpefNotifyConsumer.class);

    @Autowired
    private JyExceptionService jyExceptionService;

    @Override
    @JProfiler(jKey = "DMS.WORKER.jyExpefNotifyConsumer.consume",
            jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("jyExpefNotifyConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("jyExpefNotifyConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }

        ExpefNotify mqDto = JsonHelper.fromJson(message.getText(), ExpefNotify.class);
        jyExceptionService.expefNotifyProcesser(mqDto);
    }


}
