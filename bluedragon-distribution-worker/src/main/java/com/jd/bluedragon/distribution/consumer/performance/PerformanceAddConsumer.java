package com.jd.bluedragon.distribution.consumer.performance;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.storage.domain.FulfillmentOrderDto;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

/**
 * 履约单关系下发同步发货状态
 */
@Service("performanceAddConsumer")
public class PerformanceAddConsumer extends MessageBaseConsumer {

    private final Log logger = LogFactory.getLog(PerformanceAddConsumer.class);

    @Autowired
    private StoragePackageMService storagePackageMService;

    @Override
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.error(MessageFormat.format("[金鹏]消费履约单运单下发-消息体非JSON格式，内容为【{0}】", message.getText()));
            return;
        }

        MessageDto dto = JsonHelper.fromJsonUseGson(message.getText(), PerformanceAddConsumer.MessageDto.class);

        if(dto!=null){

            if(StringUtils.isNotBlank(dto.getFulfillmentOrderId())){

                storagePackageMService.updateStoragePackageMStatusForSendOfParentOrderId(dto.getFulfillmentOrderId());

            }else{
                logger.error(MessageFormat.format("[金鹏]消费履约单运单下发-履约单为空，内容为【{0}】", message.getText()));
            }
        }

    }

    /**
     * 履约单下发关系消息实体
     */
    public class MessageDto{

        //履约关系
        private List<FulfillmentOrderDto> deliveryOrderDtoList;

        //履约单号
        private String fulfillmentOrderId;

        public List<FulfillmentOrderDto> getDeliveryOrderDtoList() {
            return deliveryOrderDtoList;
        }

        public void setDeliveryOrderDtoList(List<FulfillmentOrderDto> deliveryOrderDtoList) {
            this.deliveryOrderDtoList = deliveryOrderDtoList;
        }

        public String getFulfillmentOrderId() {
            return fulfillmentOrderId;
        }

        public void setFulfillmentOrderId(String fulfillmentOrderId) {
            this.fulfillmentOrderId = fulfillmentOrderId;
        }
    }
}
