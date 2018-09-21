package com.jd.bluedragon.distribution.consumer.wayBill;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.batch.service.BatchSendService;
import com.jd.bluedragon.distribution.consumer.sendCar.SendCarContext;
import com.jd.bluedragon.distribution.rma.service.RmaHandOverWaybillService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.DateHelper;
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
@Service("sendCarConsumer")
public class WayBillConsumer extends MessageBaseConsumer {

    private static final Log logger= LogFactory.getLog(WayBillConsumer.class);

    @Autowired
    private RmaHandOverWaybillService rmaHandOverWaybillService;

    @Override
    public void consume(Message message) {
        if(!JsonHelper.isJsonString(message.getText())){
            logger.warn(MessageFormat.format("运单报文消费dmsWorkSendDetailMQ-消息体非JSON格式，内容为【{0}】",message.getText()));
            return;
        }
        SendDetail context=JsonHelper.fromJsonUseGson(message.getText(),SendDetail.class);
        //判断第31位是否是1
        //todo
        try {
            rmaHandOverWaybillService.addConsumer(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
