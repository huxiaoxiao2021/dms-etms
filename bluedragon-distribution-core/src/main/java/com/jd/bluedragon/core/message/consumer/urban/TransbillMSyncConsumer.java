package com.jd.bluedragon.core.message.consumer.urban;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.urban.domain.TransbillM;
import com.jd.bluedragon.distribution.urban.service.TransbillMService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;

/**
 * 城配运单MQ消费者
 * 
 * @ClassName: TransbillMSyncConsumer
 * @Description: (类描述信息)
 * @author wuyoude
 * @date 2017年5月2日 下午5:54:41
 *
 */
@Service("transbillMSyncConsumer")
public class TransbillMSyncConsumer extends MessageBaseConsumer{
    private static final Log logger= LogFactory.getLog(TransbillMSyncConsumer.class);

    @Autowired
    private TransbillMService transbillMService;
    @Override
    public void consume(Message message) {
        if(!JsonHelper.isJsonString(message.getText())){
            logger.warn(MessageFormat.format("城配运单推送MQ-消息体非JSON格式，内容为【{0}】", message.getText()));
            return;
        }
        TransbillM transbillM = JsonHelper.fromJsonUseGson(message.getText(), TransbillM.class);
        transbillMService.save(transbillM);
    }
}
