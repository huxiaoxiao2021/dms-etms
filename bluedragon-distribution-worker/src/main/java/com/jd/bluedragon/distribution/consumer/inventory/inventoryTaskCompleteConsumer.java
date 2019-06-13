package com.jd.bluedragon.distribution.consumer.inventory;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.inventory.domain.InventoryBaseRequest;
import com.jd.bluedragon.distribution.inventory.service.InventoryExceptionService;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.AbnormalPictureMq;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.report.ReportExternalService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ql.dms.report.domain.WeightVolumeQueryCondition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

@Service("inventoryTaskCompleteConsumer")
public class inventoryTaskCompleteConsumer extends MessageBaseConsumer {

    private final Log logger = LogFactory.getLog(inventoryTaskCompleteConsumer.class);

    @Autowired
    private InventoryExceptionService inventoryExceptionService;

    @Override
    public void consume(Message message) throws Exception {
        // 处理消息体
        this.logger.debug("inventoryTaskCompleteConsumer consume --> 消息Body为【" + message.getText() + "】");

        if (StringHelper.isEmpty(message.getText())) {
            this.logger.warn("inventoryTaskCompleteConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            logger.warn(MessageFormat.format("inventoryTaskCompleteConsumer consume -->消息体非JSON格式，内容为【{0}】", message.getText()));
            return;
        }
        InventoryBaseRequest inventoryBaseRequest = JsonHelper.fromJson(message.getText(), InventoryBaseRequest.class);
        if (inventoryBaseRequest == null) {
            this.logger.error("inventoryTaskCompleteConsumer consume --> 消息转换对象失败：" + message.getText());
            return;
        }
        inventoryExceptionService.generateInventoryException(inventoryBaseRequest);

    }
}
