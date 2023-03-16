package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.exception.JyExCustomerNotifyMQ;
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
 * 拣运异常-客服系统回传消息消费
 *
 * @author hujiping
 * @date 2023/3/16 10:39 AM
 */
@Service("dmsExCustomerNotifyConsumer")
public class DmsExCustomerNotifyConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DmsExScrapNoticeConsumer.class);

    @Autowired
    private JyExceptionService jyExceptionService;
    
    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("DmsExCustomerNotifyConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("客服回传消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            JyExCustomerNotifyMQ jyExCustomerNotifyMQ = JsonHelper.fromJson(message.getText(), JyExCustomerNotifyMQ.class);
            if(jyExCustomerNotifyMQ == null || StringUtils.isEmpty(jyExCustomerNotifyMQ.getBusinessId())
                    || jyExCustomerNotifyMQ.getNotifyStatus() == null) {
                logger.warn("客户回传消息体异常，内容为【{}】", message.getText());
                return;
            }
            
            // 处理客服回传消息 
            jyExceptionService.dealCustomerNotifyResult(jyExCustomerNotifyMQ);

        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("客服回传消息处理异常, 消息体:{}", message.getText(), e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
