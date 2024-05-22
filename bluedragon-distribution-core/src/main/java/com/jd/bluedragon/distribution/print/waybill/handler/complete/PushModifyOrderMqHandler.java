package com.jd.bluedragon.distribution.print.waybill.handler.complete;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.ModifyOrderInfo;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import com.jd.bluedragon.distribution.handler.AbstractHandler;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
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
public class PushModifyOrderMqHandler extends AbstractHandler<WaybillPrintCompleteContext, JdResult<Boolean>> {

    @Autowired
    @Qualifier("dmsModifyOrderInfoMQ")
    private DefaultJMQProducer dmsModifyOrderInfoMq;

    @Autowired
    private WaybillService waybillService;

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
        ModifyOrderInfo modifyOrderInfo = new ModifyOrderInfo();
        modifyOrderInfo.setOrderId(request.getWaybillCode());
        modifyOrderInfo.setDmsId(request.getOperateSiteCode());
        modifyOrderInfo.setDmsName(request.getOperateSiteName());
        modifyOrderInfo.setOperateTime(request.getOperateTime());
        // 校验是否满足标位修改条件并给运单发送MQ
        waybillService.checkAndSendModifyWaybillSignJmq(modifyOrderInfo, request.getWaybillSign());
    }
}
