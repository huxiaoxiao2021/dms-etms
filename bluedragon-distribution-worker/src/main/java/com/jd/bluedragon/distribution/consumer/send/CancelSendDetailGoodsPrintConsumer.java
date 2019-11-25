package com.jd.bluedragon.distribution.consumer.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.GoodsPrintEsManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.goodsPrint.service.GoodsPrintService;
import com.jd.bluedragon.distribution.send.domain.SendDetailMessage;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.report.domain.GoodsPrintDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author tangchunqing
 * @Description: 取消发货明细消费 托寄物品名打印用
 * @date 2018年11月21日 17时:47分
 */
@Service("cancelSendDetailGoodsPrintConsumer")
public class CancelSendDetailGoodsPrintConsumer extends MessageBaseConsumer {
    private static final Logger log = LoggerFactory.getLogger(CancelSendDetailGoodsPrintConsumer.class);

    @Autowired
    private GoodsPrintEsManager goodsPrintEsManager;


    @Autowired
    private GoodsPrintService goodsPrintService;

    @Override
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("CancelSendDetailGoodsPrintConsumer取消发货消息bd_dms_delivery_cancel_send-消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }

        /**将mq消息体转换成SendDetail对象**/
        SendDetailMessage sendDetailMQ = JsonHelper.fromJsonUseGson(message.getText(), SendDetailMessage.class);
        if (sendDetailMQ == null || StringHelper.isEmpty(sendDetailMQ.getPackageBarcode())) {
            log.warn("CancelSendDetailGoodsPrintConsumer取消发货消息bd_dms_delivery_cancel_send取消发货消息bd_dms_delivery_cancel_send-消息体[{}]转换实体失败或没有合法的包裹号"
                        ,message.getText());
            return;
        }
        String waybillCode = WaybillUtil.getWaybillCode(sendDetailMQ.getPackageBarcode());
        if (waybillCode==null){
            log.warn("CancelSendDetailGoodsPrintConsumer取消发货消息bd_dms_delivery_cancel_send取消发货消息bd_dms_delivery_cancel_send-消息体[{}]运单为空",message.getText());
            return;
        }

        //将运单维度的 托寄物品名是数据 写到es
        //缓存中有 表示不久前，已操作过同运单的包裹，不需要重复写入es了
        String key0 = Constants.GOODS_PRINT_WAYBILL_STATUS_0 + Constants.SEPARATOR_HYPHEN + sendDetailMQ.getSendCode() + Constants.SEPARATOR_HYPHEN + waybillCode;
        String key1 = Constants.GOODS_PRINT_WAYBILL_STATUS_1 + Constants.SEPARATOR_HYPHEN + sendDetailMQ.getSendCode() + Constants.SEPARATOR_HYPHEN + waybillCode + Constants.SEPARATOR_HYPHEN + (BusinessUtil.isBoxcode(sendDetailMQ.getBoxCode()) ? sendDetailMQ.getBoxCode() : "");
        if (goodsPrintService.getWaybillFromEsOperator(key0)) {
            return;
        }
        //缓存中没有有2种情况 1：缓存过期 2：es就一直没存过
        //查es 是否有该运单
        GoodsPrintDto goodsPrintDto = goodsPrintEsManager.findGoodsPrintBySendCodeAndWaybillCode(sendDetailMQ.getSendCode(), waybillCode);
        //es中查不到，就是真没有了， insert该运单
        if (goodsPrintDto != null) {
            //取消发货，清空箱号
            goodsPrintDto.setBoxCode("");
            //改为取消状态
            goodsPrintDto.setSendStatus(Constants.GOODS_PRINT_WAYBILL_STATUS_0);
            if (goodsPrintEsManager.insertOrUpdate(goodsPrintDto)) {
                goodsPrintService.setWaybillFromEsOperator(key0);
                goodsPrintService.deleteWaybillFromEsOperator(key1);
            }
        }

    }
}
