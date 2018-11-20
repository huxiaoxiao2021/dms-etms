package com.jd.bluedragon.distribution.consumer.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.GoodsPrintEsManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.goodsPrint.service.GoodsPrintService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.weightAndMeasure.service.DmsOutWeightAndVolumeService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.report.domain.GoodsPrintDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service("dmsDeliveryCancelSendConsumer")
public class DmsDeliveryCancelSendConsumer extends MessageBaseConsumer {
    private static final Log logger = LogFactory.getLog(DmsDeliveryCancelSendConsumer.class);

    @Autowired
    private DmsOutWeightAndVolumeService dmsOutWeightAndVolumeService;


    @Autowired
    private GoodsPrintEsManager goodsPrintEsManager;


    @Autowired
    private GoodsPrintService goodsPrintService;

    @Override
    @JProfiler(jKey = "DmsDeliveryCancelSendConsumer.consume", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.Heartbeat})
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn(MessageFormat.format("取消发货消息bd_dms_delivery_cancel_send-消息体非JSON格式，内容为【{0}】", message.getText()));
            return;
        }

        /**将mq消息体转换成SendDetail对象**/
        SendDetail sendDetailMQ = JsonHelper.jsonToObject(message.getText(), SendDetail.class);
        if (sendDetailMQ == null || StringHelper.isEmpty(sendDetailMQ.getPackageBarcode())) {
            logger.error("取消发货消息bd_dms_delivery_cancel_send-消息体[" + message.getText() + "]转换实体失败或没有合法的包裹号");
            return;
        }

        if(sendDetailMQ.getCreateSiteCode() == null || sendDetailMQ.getCreateSiteCode() < 1){
            logger.error("取消发货消息bd_dms_delivery_cancel_send-消息体[" + message.getText() + "]转换实体失败或没有始发分拣中心");
            return;
        }
        //从表里删除
        String barCode = sendDetailMQ.getPackageBarcode();
        if(BusinessHelper.isBoxcode(sendDetailMQ.getBoxCode())){
            barCode = sendDetailMQ.getBoxCode();
        }

        dmsOutWeightAndVolumeService.deleteByBarCodeAndDms(barCode,sendDetailMQ.getCreateSiteCode());

        // TODO: 2018/9/15  调es的接口，将取消发货的运单从es中删除

        //将运单维度的 托寄物品名是数据 写到es
        //缓存中有 表示不久前，已操作过同运单的包裹，不需要重复写入es了
        String key = Constants.GOODS_PRINT_WAYBILL_STATUS_0+sendDetailMQ.getSendCode() + Constants.SEPARATOR_HYPHEN + sendDetailMQ.getWaybillCode();
        if (!goodsPrintService.getWaybillFromEsOperator(key)) {
            //缓存中没有有2种情况 1：缓存过期 2：es就一直没存过
            //查es 是否有该运单
            GoodsPrintDto goodsPrintDto= goodsPrintEsManager.findGoodsPrintBySendCodeAndWaybillCode(sendDetailMQ.getSendCode(),sendDetailMQ.getWaybillCode());
            //es中查不到，就是真没有了， insert该运单
            if (goodsPrintDto!=null){
                //取消发货，清空箱号
                goodsPrintDto.setBoxCode("");
                //改为发货状态
                goodsPrintDto.setSendStatus(Constants.GOODS_PRINT_WAYBILL_STATUS_0);
                if (goodsPrintEsManager.insertOrUpdate(goodsPrintDto)){
                    goodsPrintService.setWaybillFromEsOperator(key);
                }
            }
        }
    }
}
