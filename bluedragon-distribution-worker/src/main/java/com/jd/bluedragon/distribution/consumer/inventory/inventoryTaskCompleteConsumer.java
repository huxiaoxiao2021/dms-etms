package com.jd.bluedragon.distribution.consumer.inventory;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.inventory.domain.InventoryBaseRequest;
import com.jd.bluedragon.distribution.inventory.service.InventoryExceptionService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("inventoryTaskCompleteConsumer")
public class inventoryTaskCompleteConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(inventoryTaskCompleteConsumer.class);

    @Autowired
    private InventoryExceptionService inventoryExceptionService;

    @Override
    public void consume(Message message) throws Exception {
        // 处理消息体
        log.debug("inventoryTaskCompleteConsumer consume --> 消息Body为【{}】",message.getText());

        if (StringHelper.isEmpty(message.getText())) {
            log.warn("inventoryTaskCompleteConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            log.warn("inventoryTaskCompleteConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        InventoryBaseRequest inventoryBaseRequest = JsonHelper.fromJson(message.getText(), InventoryBaseRequest.class);
        if (inventoryBaseRequest == null) {
            log.warn("inventoryTaskCompleteConsumer consume --> 消息转换对象失败：{}" , message.getText());
            return;
        }
        inventoryExceptionService.generateInventoryException(inventoryBaseRequest);

    }
}
