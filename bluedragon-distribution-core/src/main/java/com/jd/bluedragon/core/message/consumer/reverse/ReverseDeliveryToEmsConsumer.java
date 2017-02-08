package com.jd.bluedragon.core.message.consumer.reverse;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.jmq.common.message.Message;
import com.jd.postal.GetPrintDatasPortType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

/**
 * Created by wuzuxiang on 2017/2/7.
 */
@Service("reverseDeliveryToEmsConsumer")
public class ReverseDeliveryToEmsConsumer extends MessageBaseConsumer{
    private static final Log logger = LogFactory.getLog(ReverseDeliveryToEmsConsumer.class);

    @Autowired
    private GetPrintDatasPortType getPrintDatasPortType;

    @Override
    public void consume(Message message) throws Exception {
        this.logger.info("推送全国EMS的自消费类型的MQ：内容为" + message.getText());
        if(message == null || "".equals(message.getText()) || null == message.getText() ){
            this.logger.error("推送EMS的消息类型为空");
            return;
        }
        String body = message.getText();
        Base64 base64 = new Base64();
        String emsstring=null;
        try {
            body =new String(base64.encode(body.getBytes("utf-8")), Charset.forName("UTF-8"));
            emsstring = getPrintDatasPortType.printEMSDatas(body);
        } catch (Exception e) {
            this.logger.error("全国邮政消费mq异常,订单号为：" + body,e);
        }

        if (null == emsstring || "".equals(emsstring.trim())) {
            this.logger
                    .error("toEmsServer CXF return null :");
        }else{
            emsstring = new String(base64.decode(emsstring),Charset.forName("UTF-8"));
            this.logger.error("全国邮政返回" + emsstring);
        }
    }
}
