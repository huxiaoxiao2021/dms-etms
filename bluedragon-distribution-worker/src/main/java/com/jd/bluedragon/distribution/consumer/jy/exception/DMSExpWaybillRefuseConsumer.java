package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.exception.JyExpWaybillDeliveryDto;
import com.jd.bluedragon.distribution.jy.service.exception.JyDamageExceptionService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.util.StringUtils;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/8/10 11:31
 * @Description: 运单妥投-检验异常破损单子是否妥投
 */
@Service("dmsExpWaybillRefuseConsumer")
public class DMSExpWaybillRefuseConsumer extends MessageBaseConsumer {
    private static final Logger logger = LoggerFactory.getLogger(DMSExpWaybillRefuseConsumer.class);

    @Autowired
    private JyDamageExceptionService jyDamageExceptionService;

    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("DMSExpWaybillRefuseConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            logger.info("运单拒收消息体-{}",message.getText());
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("运单拒收消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            JyExpWaybillDeliveryDto deliveryDto = JsonHelper.fromJson(message.getText(), JyExpWaybillDeliveryDto.class);
            if(deliveryDto == null || StringUtils.isEmpty(deliveryDto.getWaybillCode())){
                logger.warn("运单拒收消息体异常，内容为【{}】", message.getText());
                return;
            }
            jyDamageExceptionService.dealDamageExpTaskStatus(deliveryDto.getWaybillCode(),null);

        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("运单拒收消息体处理异常, 消息体:{}", message.getText(), e);
            throw e;
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
