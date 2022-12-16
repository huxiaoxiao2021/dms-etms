package com.jd.bluedragon.distribution.consumer.device;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.request.client.DeviceLocationUploadPo;
import com.jd.bluedragon.distribution.consumer.sendCar.SendCarArriveStatusConsumer;
import com.jd.bluedragon.distribution.device.service.DeviceLocationService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 设备位置上传消息处理
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-12-08 14:15:18 周四
 */
@Service("dmsUploadDeviceLocationConsumer")
public class DmsUploadDeviceLocationConsumer extends MessageBaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(SendCarArriveStatusConsumer.class);

    @Resource
    private DeviceLocationService deviceLocationService;

    @Override
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("DmsUploadDeviceLocationConsumer 消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        try {
            DeviceLocationUploadPo deviceLocationUploadPo = JsonHelper.fromJsonUseGson(message.getText(), DeviceLocationUploadPo.class);
            if (deviceLocationUploadPo == null) {
                log.error("DmsUploadDeviceLocationConsumer 消息丢弃,入参：{}", message.getText());
                return;
            }
            final Result<Boolean> handleResult = deviceLocationService.handleDmsUploadDeviceLocationConsume(deviceLocationUploadPo);
            if (handleResult == null || !handleResult.isSuccess()) {
                log.error("DmsUploadDeviceLocationConsumer handleDmsUploadDeviceLocationConsume fail, param: {}, result: {}", JsonHelper.toJson(deviceLocationUploadPo), JsonHelper.toJson(handleResult));
            } else {
                log.info("DmsUploadDeviceLocationConsumer handleDmsUploadDeviceLocationConsume success, param {}", JsonHelper.toJson(deviceLocationUploadPo));
            }
        } catch (Exception e) {
            log.error("DmsUploadDeviceLocationConsumer exception 消费消息异常 {}", message.getText(), e);
            throw e;
        }
    }
}
