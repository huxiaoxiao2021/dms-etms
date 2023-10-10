package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionContrabandDto;
import com.jd.bluedragon.distribution.jy.service.exception.JyContrabandExceptionService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.util.StringUtils;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/8/16 15:36
 * @Description:
 */
@Service("jyExceptionContrabandUploadConsumer")
public class JyExceptionContrabandUploadConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(JyExceptionContrabandUploadConsumer.class);

    @Autowired
    private JyContrabandExceptionService jyContrabandExceptionService;


    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("DMSExpWaybillDeliveryConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            logger.info("违禁品上报息体-{}",message.getText());
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("违禁品上报消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            JyExceptionContrabandDto contrabandDto = JsonHelper.fromJson(message.getText(), JyExceptionContrabandDto.class);
            if(contrabandDto == null
                || StringUtils.isEmpty(contrabandDto.getBizId())
                || Objects.isNull(contrabandDto.getContrabandType())){
                logger.warn("违禁品上报消息体异常，内容为【{}】", message.getText());
                return;
            }
            jyContrabandExceptionService.dealContrabandUploadData(contrabandDto);
        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("违禁品上报消息体处理异常, 消息体:{}", message.getText(), e);
            throw e;
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
