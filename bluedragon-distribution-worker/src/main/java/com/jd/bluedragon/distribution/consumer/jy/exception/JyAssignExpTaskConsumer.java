package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.exception.JyAssignExpTaskMQ;
import com.jd.bluedragon.distribution.jy.service.exception.JySanwuExceptionService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/6/1 13:52
 * @Description: 异常任务指派任务MQ 消费
 */
@Service("jyAssignExpTaskConsumer")
public class JyAssignExpTaskConsumer  extends MessageBaseConsumer {

    @Autowired
    private JySanwuExceptionService jySanwuExceptionService;

    private static final Logger logger = LoggerFactory.getLogger(JyAssignExpTaskConsumer.class);

    @Override
    @JProfiler(jKey = "DMS.WORKER.JyAssignExpTaskConsumer.consume",
            jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {

        if(logger.isInfoEnabled()){
            logger.info("JyAssignExpTaskConsumer consume --> 消息体:{}", JSON.toJSONString(message.getText()));
        }
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("JyAssignExpTaskConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("JyAssignExpTaskConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        JyAssignExpTaskMQ mqDto = JsonHelper.fromJson(message.getText(), JyAssignExpTaskMQ.class);

        jySanwuExceptionService.dealAssignTaskData(mqDto);
        if(StringUtils.isBlank(mqDto.getBizId()) || StringUtils.isBlank(mqDto.getAssignHandlerErp())){
            throw new RuntimeException("指派异常任务数据异常!");
        }

    }
}
