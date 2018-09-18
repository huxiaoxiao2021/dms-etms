package com.jd.bluedragon.distribution.consumer.sendDetail;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.consumer.gantry.GantryScanPackageConsumer;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service("dmsDeliveryCancelSendConsumer")
public class DmsDeliveryCancelSendConsumer extends MessageBaseConsumer {
    private static final Log logger = LogFactory.getLog(GantryScanPackageConsumer.class);

    @Override
    @JProfiler(jKey = "DmsDeliveryCancelSendConsumer.consume", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.Heartbeat})
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn(MessageFormat.format("取消发货消息bd_dms_delivery_cancel_send-消息体非JSON格式，内容为【{0}】", message.getText()));
            return;
        }

        /**将mq消息体转换成SendDetail对象**/
        SendDetail sendDetailMQ = JsonHelper.jsonToObject(message.getText(), SendDetail.class);
        if (sendDetailMQ == null || StringHelper.isEmpty(sendDetailMQ.getPackageBarcode())) {
            logger.error("取消发货消息bd_dms_delivery_cancel_send-消息体[" + message.getText() + "]转换实体失败或没有合法的包裹号");
            return;
        }

        //从表里删除

        // TODO: 2018/9/15  调es的接口，将取消发货的运单从es中删除


    }
}
