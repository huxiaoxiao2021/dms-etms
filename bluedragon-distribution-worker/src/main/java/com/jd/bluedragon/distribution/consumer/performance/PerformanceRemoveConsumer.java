package com.jd.bluedragon.distribution.consumer.performance;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.storage.domain.WaybillOrderFlagMessage;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.List;


/**
 * 履约单 剔除消费逻辑
 *
 * 剔除对应运单。剩余运单更新状态
 *
 * 2018年8月28日14:15:55
 *
 * 刘铎
 */
@Service("performanceRemoveConsumer")
public class PerformanceRemoveConsumer  extends MessageBaseConsumer {

    private final Log logger = LogFactory.getLog(PerformanceRemoveConsumer.class);

    private static final Type LIST_TYPE =
            new TypeToken<List<WaybillOrderFlagMessage>>() {
            }.getType();

    @Autowired
    private StoragePackageMService storagePackageMService;


    @Override
    public void consume(Message message) throws Exception {

        if (!JsonHelper.isJsonString(message.getText())) {
            logger.error(MessageFormat.format("[金鹏]消费履约单运单剔除-消息体非JSON格式，内容为【{0}】", message.getText()));
            return;
        }

        List<WaybillOrderFlagMessage> dtos = JsonHelper.fromJsonUseGson(message.getText(), LIST_TYPE);

        for(WaybillOrderFlagMessage dto : dtos){
            if(dto.getOrderFlag() == null){
                logger.error(MessageFormat.format("[金鹏]消费履约单运单剔除-剔除类型为空，内容为【{0}】", JsonHelper.toJson(dto)));
                break;
            }
            if(dto.needDeal()){
                if(StringUtils.isBlank(dto.getWaybillCode())){
                    logger.error(MessageFormat.format("[金鹏]消费履约单运单剔除-运单号为空，内容为【{0}】", JsonHelper.toJson(dto)));
                    break;
                }
                if(StringUtils.isBlank(dto.getFulfillmentOrderId())){
                    logger.warn(MessageFormat.format("[金鹏]消费履约单运单剔除-履约单号为空，内容为【{0}】", JsonHelper.toJson(dto)));
                    break;
                }
                storagePackageMService.removeWaybill(dto.getWaybillCode(),dto.getFulfillmentOrderId());
            }

        }


    }
}
