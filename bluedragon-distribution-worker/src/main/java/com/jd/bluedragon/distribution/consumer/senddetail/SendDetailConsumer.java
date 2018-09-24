package com.jd.bluedragon.distribution.consumer.senddetail;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.rma.service.RmaHandOverWaybillService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;


/**
 * 运单报文消费dmsWorkSendDetail
 * Created by zhanghao141 on 2018/9/21
 */
@Service("sendDetailConsumer")
public class SendDetailConsumer extends MessageBaseConsumer {

    private static final Log logger = LogFactory.getLog(SendDetailConsumer.class);

    @Autowired
    private RmaHandOverWaybillService rmaHandOverWaybillService;

    @Override
    public void consume(Message message) {
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn(MessageFormat.format("发货明细消费[dmsWorkSendDetail]MQ-消息体非JSON格式，内容为【{0}】", message.getText()));
            return;
        }
        SendDetail context = JsonHelper.fromJsonUseGson(message.getText(), SendDetail.class);
        try {
            rmaHandOverWaybillService.addConsumer(context);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
