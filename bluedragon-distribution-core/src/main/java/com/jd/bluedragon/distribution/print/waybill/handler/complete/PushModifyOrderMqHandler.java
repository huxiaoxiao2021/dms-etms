package com.jd.bluedragon.distribution.print.waybill.handler.complete;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.ModifyOrderInfo;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author wyh
 * @className PushModifyOrderMqHandler
 * @description
 * @date 2021/12/4 14:37
 **/
@Service("pushModifyOrderMqHandler")
public class PushModifyOrderMqHandler implements Handler<WaybillPrintCompleteContext, JdResult<Boolean>> {

    @Autowired
    @Qualifier("dmsModifyOrderInfoMQ")
    private DefaultJMQProducer dmsModifyOrderInfoMq;

    /**
     * 执行处理，返回处理结果
     *
     * @param context
     * @return
     */
    @Override
    public JdResult<Boolean> handle(WaybillPrintCompleteContext context) {

        sendModifyOrderMq(context.getRequest());

        return context.getResult();
    }

    /**
     *
     * @param request
     */
    private void sendModifyOrderMq(PrintCompleteRequest request) {
        String waybillSign = request.getWaybillSign();
        if (StringHelper.isNotEmpty(request.getWaybillSign()) && waybillSign.length() > 8 &&
                (BusinessUtil.isSignChar(waybillSign,8,'1' ) || BusinessUtil.isSignChar(waybillSign,8,'2' )
                        || BusinessUtil.isSignChar(waybillSign,8,'3' ))) {
            char sign = waybillSign.charAt(7);

            ModifyOrderInfo modifyOrderInfo = new ModifyOrderInfo();
            modifyOrderInfo.setOrderId(request.getWaybillCode());
            modifyOrderInfo.setDmsId(request.getOperateSiteCode());
            modifyOrderInfo.setDmsName(request.getOperateSiteName());
            Integer resultCode = null;
            if (sign == '1') {
                resultCode = 5;
            }
            else if (sign == '2'){
                resultCode = 6;
            }
            else if (sign == '3') {
                resultCode = 7;
            }
            modifyOrderInfo.setResultCode(resultCode);
            modifyOrderInfo.setOperateTime(request.getOperateTime());
            String json = JsonHelper.toJson(modifyOrderInfo);
            dmsModifyOrderInfoMq.sendOnFailPersistent(modifyOrderInfo.getOrderId(), json);
        }
    }
}
