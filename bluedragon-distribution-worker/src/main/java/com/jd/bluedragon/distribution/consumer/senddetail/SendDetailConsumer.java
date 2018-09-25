package com.jd.bluedragon.distribution.consumer.senddetail;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.rma.service.RmaHandOverWaybillService;
import com.jd.bluedragon.distribution.send.domain.SendDetailMessage;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.jmq.common.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;


/**
 * 发货明细MQ[dmsWorkSendDetail] 消费逻辑
 *
 * @author lixin39
 */
@Service("sendDetailConsumer")
public class SendDetailConsumer extends MessageBaseConsumer {

    private static final Log logger = LogFactory.getLog(SendDetailConsumer.class);

    @Autowired
    private RmaHandOverWaybillService rmaHandOverWaybillService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Override
    public void consume(Message message) {
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn(MessageFormat.format("发货明细消费[dmsWorkSendDetail]MQ-消息体非JSON格式，内容为【{0}】", message.getText()));
            return;
        }
        SendDetailMessage context = JsonHelper.fromJsonUseGson(message.getText(), SendDetailMessage.class);
        try {
            this.doConsume(context);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage() + "，MQ message body:" + message.getText(), e);
        }
    }

    private void doConsume(SendDetailMessage sendDetail) {
        String packageBarCode = sendDetail.getPackageBarcode();
        if (SerialRuleUtil.isWaybillOrPackageNo(packageBarCode)) {
            BaseEntity<BigWaybillDto> baseEntity = getWaybillBaseEntity(SerialRuleUtil.getWaybillCode(packageBarCode));
            if (baseEntity.getData() != null && baseEntity.getData().getWaybill() != null) {
                Waybill waybill = baseEntity.getData().getWaybill();
                if (BusinessHelper.isRMA(waybill.getWaybillSign())) {
                    if (!rmaHandOverWaybillService.buildAndStorage(sendDetail, waybill, baseEntity.getData().getGoodsList())) {
                        throw new RuntimeException("[消费发货明细MQ消息]存储RMA订单数据失败，packageBarCode:" + packageBarCode);
                    }
                }
            } else {
                throw new RuntimeException("[消费发货明细MQ消息]根据运单号获取运单信息为空，packageBarCode:" + packageBarCode);
            }
        } else {
            logger.warn("[消费发货明细MQ消息]无效的运单号/包裹号，packageBarCode:" + packageBarCode);
        }
    }

    /**
     * 调用运单JSF接口获取运单基础数据信息
     *
     * @param waybillCode
     * @return
     */
    private BaseEntity<BigWaybillDto> getWaybillBaseEntity(String waybillCode) {
        WChoice choice = new WChoice();
        choice.setQueryWaybillC(true);
        choice.setQueryWaybillE(true);
        choice.setQueryWaybillM(true);
        choice.setQueryGoodList(true);
        return waybillQueryManager.getDataByChoice(waybillCode, choice);
    }

}
