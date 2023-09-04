package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.exception.JyExCustomerNotifyMQ;
import com.jd.bluedragon.distribution.jy.exception.JyExpCustomerReturnMQ;
import com.jd.bluedragon.distribution.jy.service.exception.JyDamageExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
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
 * 异常 破损-客服系统回传消息消费
 *
 * @author 陈亚国
 * @date 2023/3/16 10:39 AM
 */
@Service("dmsExpCustomerReturnConsumer")
public class DmsExpCustomerReturnConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DmsExpCustomerReturnConsumer.class);

    @Autowired
    private JyDamageExceptionService jyDamageExceptionService;
    
    @Override
    public void consume(Message message) throws Exception {
        logger.info("客服异常信息回传消息体");
        CallerInfo info = Profiler.registerInfo("DmsExpCustomerReturnConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            logger.info("客服异常信息回传消息体-{}",message.getText());
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("客服异常信息回传消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            JyExpCustomerReturnMQ returnMQ = JsonHelper.fromJson(message.getText(), JyExpCustomerReturnMQ.class);
            if(returnMQ == null || StringUtils.isEmpty(returnMQ.getExptId())
                    || StringUtils.isEmpty(returnMQ.getResultType())) {
                logger.warn("客户异常信息回传消息体异常，内容为【{}】", message.getText());
                return;
            }
            
            // 处理客服回传破损消息
            jyDamageExceptionService.dealCustomerReturnDamageResult(returnMQ);

        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("客服回异常信息传消息处理异常, 消息体:{}", message.getText(), e);
            throw e;
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
