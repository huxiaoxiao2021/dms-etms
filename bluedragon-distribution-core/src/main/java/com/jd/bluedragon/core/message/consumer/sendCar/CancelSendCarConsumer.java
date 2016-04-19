package com.jd.bluedragon.core.message.consumer.sendCar;

import java.text.MessageFormat;

import com.jd.jmq.common.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.batch.service.BatchSendService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;

/**
 * 取消发车MQ消费
 * Created by wangtingwei on 2015/4/8.
 */
@Service("cancelSendCarConsumer")
public class CancelSendCarConsumer extends MessageBaseConsumer{
    private static final Log logger= LogFactory.getLog(CancelSendCarConsumer.class);

    @Autowired
    private BatchSendService batchSendService;
    @Override
    public void consume(Message message) {
        if(!JsonHelper.isJsonString(message.getText())){
            logger.warn(MessageFormat.format("运单推送取消发车MQ-消息体非JSON格式，内容为【{0}】", message.getText()));
            return;
        }
        SendCarContext context=null;
        context= JsonHelper.fromJsonUseGson(message.getText(), SendCarContext.class);
        if(null==context.getBatchType()||!context.getBatchType().equals("1")){
            logger.info(MessageFormat.format("运单推送非批次取消发车信息，分拣中心抛弃，内容为【{0}】",message.getText()));
            return;
        }
        batchSendService.cancelSendCar(context.getBatchCode(), DateHelper.parseDateTime(context.getOpeTime()));
    }
}
