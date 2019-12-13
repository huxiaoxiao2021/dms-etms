package com.jd.bluedragon.distribution.consumer.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.weightAndMeasure.service.DmsOutWeightAndVolumeService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("dmsDeliveryCancelSendConsumer")
public class DmsDeliveryCancelSendConsumer extends MessageBaseConsumer {
    private static final Logger log = LoggerFactory.getLogger(DmsDeliveryCancelSendConsumer.class);

    @Autowired
    private DmsOutWeightAndVolumeService dmsOutWeightAndVolumeService;

    @Override
    @JProfiler(jKey = "DmsDeliveryCancelSendConsumer.consume", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.Heartbeat})
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("取消发货消息bd_dms_delivery_cancel_send-消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }

        /**将mq消息体转换成SendDetail对象**/
        SendDetail sendDetailMQ = JsonHelper.jsonToObject(message.getText(), SendDetail.class);
        if (sendDetailMQ == null || StringHelper.isEmpty(sendDetailMQ.getPackageBarcode())) {
            log.warn("取消发货消息bd_dms_delivery_cancel_send-消息体[{}]转换实体失败或没有合法的包裹号",message.getText());
            return;
        }

        if(sendDetailMQ.getCreateSiteCode() == null || sendDetailMQ.getCreateSiteCode() < 1){
            log.warn("取消发货消息bd_dms_delivery_cancel_send-消息体[{}]转换实体失败或没有始发分拣中心",message.getText());
            return;
        }
        //从表里删除
        String barCode = sendDetailMQ.getPackageBarcode();
        if(BusinessHelper.isBoxcode(sendDetailMQ.getBoxCode())){
            barCode = sendDetailMQ.getBoxCode();
        }

        dmsOutWeightAndVolumeService.deleteByBarCodeAndDms(barCode,sendDetailMQ.getCreateSiteCode());
    }
}
