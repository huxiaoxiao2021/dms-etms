package com.jd.bluedragon.core.message.consumer.urban;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.core.message.consumer.sendCar.SendCarContext;
import com.jd.bluedragon.distribution.urban.domain.UrbanWaybill;
import com.jd.bluedragon.distribution.urban.service.UrbanWaybillService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;

/**
 * 
* @ClassName: UrbanWaybillConsumer
* @Description: TODO(城配运单MQ消费者)
* @author wuyoude
* @date 2017年4月13日 下午6:09:59
*
 */
@Service("urbanWaybillSyncConsumer")
public class UrbanWaybillSyncConsumer extends MessageBaseConsumer{
    private static final Log logger= LogFactory.getLog(UrbanWaybillSyncConsumer.class);

    @Autowired
    private UrbanWaybillService urbanWaybillService;
    @Override
    public void consume(Message message) {
        if(!JsonHelper.isJsonString(message.getText())){
            logger.warn(MessageFormat.format("城配运单推送MQ-消息体非JSON格式，内容为【{0}】", message.getText()));
            return;
        }
        UrbanWaybill data = JsonHelper.fromJsonUseGson(message.getText(), UrbanWaybill.class);
        if(data == null|| StringHelper.isEmpty(data.getWaybillCode())){
            logger.info(MessageFormat.format("城配运单没有运单号，分拣中心抛弃，内容为【{0}】",message.getText()));
            return;
        }
        data.setOperateUserErp(data.getOperateUserCode());
        urbanWaybillService.save(data);
    }
}
