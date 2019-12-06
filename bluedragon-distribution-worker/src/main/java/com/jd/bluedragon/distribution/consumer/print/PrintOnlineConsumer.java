package com.jd.bluedragon.distribution.consumer.print;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.printOnline.service.IPrintOnlineService;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("printOnlineConsumer")
public class PrintOnlineConsumer extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(PrintOnlineConsumer.class);

    @Autowired
    private IPrintOnlineService printOnlineService;

    @Override
    public void consume(Message message) throws Exception {

        if(message == null || "".equals(message.getText()) || null == message.getText()){
            this.log.warn("线上签推送的消息体内容为空");
            return;
        }
        if(!printOnlineService.reversePrintOnline(message.getText())){
            throw new RuntimeException("线上签推送失败转重试"+message.getText());
        }
    }
}
