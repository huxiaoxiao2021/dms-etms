package com.jd.bluedragon.distribution.print.waybill.handler.complete;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.ChangeOrderPrintMq;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.exception.JMQException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 *  处理器
 *
 * @author: chenyaguo
 * @date: 2019/12/6 18:20
 */
@Service("changeWaybillPrintSendMsgHandler")
public class ChangeWaybillPrintSendMsgHandler implements InterceptHandler<WaybillPrintCompleteContext, String> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("changeWaybillPrintFirstProducer")
    private DefaultJMQProducer changeWaybillPrintFirstProducer;


    @Override
    public InterceptResult<String> handle(WaybillPrintCompleteContext context) {
        InterceptResult<String> interceptResult = new InterceptResult<String>();
        interceptResult.toSuccess();
        PrintCompleteRequest request = context.getRequest();
        ChangeOrderPrintMq changeOrderPrintMq = getChangeOrderPrintMq(request, context);
        logger.info("changeOrderPrintSendProducer-{}",JsonHelper.toJson(changeOrderPrintMq));
        try {
            changeWaybillPrintFirstProducer.send(changeOrderPrintMq.getWaybillCode(), JsonHelper.toJson(changeOrderPrintMq));
        } catch (JMQException e) {
            e.printStackTrace();
        }
        return interceptResult;
    }

    /**
     * 组装换单打印MQ信息
     *
     * @param request
     * @param context
     * @return
     */
    private ChangeOrderPrintMq getChangeOrderPrintMq(PrintCompleteRequest request, WaybillPrintCompleteContext context) {
        ChangeOrderPrintMq mq = new ChangeOrderPrintMq();
        mq.setOperateType(WaybillPrintOperateTypeEnum.SWITCH_BILL_PRINT.getType());
        mq.setWaybillCode(request.getWaybillCode());
        mq.setUserCode(request.getOperatorCode());
        mq.setUserName(request.getOperatorName());
        mq.setUserErp(request.getOperatorErp());
        mq.setSiteCode(request.getOperateSiteCode());
        mq.setSiteName(request.getOperateSiteName());
        mq.setOperateTime(new Date());
        return mq;
    }

}
