package com.jd.bluedragon.distribution.consumer.reverse;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.waybill.domain.WaybillInfo;
import com.jd.bluedragon.external.crossbow.ems.domain.EMSResponse;
import com.jd.bluedragon.external.crossbow.ems.manager.EMSBusinessManager;
import com.jd.bluedragon.external.service.EmsServiceManager;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.jmq.common.message.Message;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

/**
 * Created by wuzuxiang on 2017/2/7.
 */
@Service("reverseDeliveryToEmsConsumer")
public class ReverseDeliveryToEmsConsumer extends MessageBaseConsumer{

    private static final Logger log = LoggerFactory.getLogger(ReverseDeliveryToEmsConsumer.class);

    @Autowired
    private EMSBusinessManager emsBusinessManager;

    @Override
    public void consume(Message message) throws Exception {
        log.debug("推送全国EMS的自消费类型的MQ：内容为:{}" , message.getText());
        String body = message.getText();

        WaybillInfo waybillInfo = JsonHelper.fromJson(body, WaybillInfo.class);
        if (null == waybillInfo) {
            log.error("推送全国EMS的消息序列化失败：内容为:{}" , message.getText());
            return;
        }
        EMSResponse response = emsBusinessManager.doRestInterface(waybillInfo);
        if (response == null) {
            log.warn("toEmsServer CXF return null :{}",body);
        } else {
            //记录systemLog 方便查询 参数顺序依次为 1.waybillCode，2.packageCode，3.mq的topic，4.接口返回的code，5.武汉邮政返回结果，6.自定义的type
            SystemLogUtil.log(message.getBusinessId(),waybillInfo.getPackageBarcode(),message.getTopic(),
                    Long.parseLong(response.getResult()),JsonHelper.toJson(response),89757L);
        }
    }
}
