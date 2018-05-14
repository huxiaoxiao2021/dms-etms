package com.jd.bluedragon.distribution.consumer.reverse;

import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.external.service.EmsServiceManager;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.jmq.common.message.Message;

/**
 * Created by wuzuxiang on 2017/2/7.
 */
@Service("reverseDeliveryToEmsConsumer")
public class ReverseDeliveryToEmsConsumer extends MessageBaseConsumer{
    private static final Log logger = LogFactory.getLog(ReverseDeliveryToEmsConsumer.class);

    @Autowired
    private EmsServiceManager emsServiceManager;

    @Override
    public void consume(Message message) throws Exception {
        logger.info("推送全国EMS的自消费类型的MQ：内容为" + message.getBusinessId());
        if(message == null || "".equals(message.getText()) || null == message.getText() ){
            logger.error("推送EMS的消息类型为空");
            return;
        }
        String body = message.getText();
        Base64 base64 = new Base64();
        String emsstring=null;
        body =new String(base64.encode(body.getBytes("utf-8")), Charset.forName("UTF-8"));
        emsstring = emsServiceManager.printEMSDatas(body);
        if (null == emsstring || "".equals(emsstring.trim())) {
            logger
                    .error("toEmsServer CXF return null :");
        }else{
            emsstring = new String(base64.decode(emsstring),Charset.forName("UTF-8"));
            logger.info("全国邮政返回" + emsstring);
        }

        //添加systemLog日志
        SystemLogUtil.log(message.getBusinessId(),body,"bd_dms_ems_mq",1,emsstring,89757L);
    }
}
