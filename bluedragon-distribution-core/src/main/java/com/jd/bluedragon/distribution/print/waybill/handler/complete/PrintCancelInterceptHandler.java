package com.jd.bluedragon.distribution.print.waybill.handler.complete;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.domain.KyAddressModifyPrintCancelInterceptMQ;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 打印取消拦截处理器
 *
 * @author hujiping
 * @date 2022/12/14 3:06 PM
 */
@Service("printCancelInterceptHandler")
public class PrintCancelInterceptHandler implements Handler<WaybillPrintCompleteContext, JdResult<Boolean>> {

    private static final Logger logger = LoggerFactory.getLogger(PrintCancelInterceptHandler.class);

    @Autowired
    @Qualifier("printCancelKyAddressModifyInterceptProducer")
    private DefaultJMQProducer printCancelKyAddressModifyInterceptProducer;

    @Override
    public JdResult<Boolean> handle(WaybillPrintCompleteContext context) {

        PrintCompleteRequest request = context.getRequest();
        if(BusinessUtil.isKyAddressModifyWaybill(request.getWaybillSign())){
            // 快运改址，发打印消息通知ver，ver消费来解除拦截状态
            KyAddressModifyPrintCancelInterceptMQ kyAddressModifyPrintCancelInterceptMQ = new KyAddressModifyPrintCancelInterceptMQ();
            kyAddressModifyPrintCancelInterceptMQ.setWaybillCode(request.getWaybillCode());
            if(WaybillUtil.isPackageCode(request.getPackageBarcode())){
                kyAddressModifyPrintCancelInterceptMQ.setPackageCode(request.getPackageBarcode());
            }
            Date operateTime = DateHelper.parseDate(request.getOperateTime(), Constants.DATE_TIME_FORMAT);
            kyAddressModifyPrintCancelInterceptMQ.setOperateTime(operateTime == null ? System.currentTimeMillis() : operateTime.getTime());
            kyAddressModifyPrintCancelInterceptMQ.setPrintType(context.getOperateType());
            kyAddressModifyPrintCancelInterceptMQ.setUserCode(request.getOperatorCode());
            kyAddressModifyPrintCancelInterceptMQ.setUserName(request.getOperatorName());
            kyAddressModifyPrintCancelInterceptMQ.setUserErp(request.getOperatorErp());
            kyAddressModifyPrintCancelInterceptMQ.setSiteCode(request.getOperateSiteCode());
            kyAddressModifyPrintCancelInterceptMQ.setSiteName(request.getOperateSiteName());
            printCancelKyAddressModifyInterceptProducer.sendOnFailPersistent(kyAddressModifyPrintCancelInterceptMQ.getWaybillCode(), JsonHelper.toJson(kyAddressModifyPrintCancelInterceptMQ));
            logger.info("单号:{}操作打印后取消快运改址拦截!", StringUtils.isEmpty(request.getPackageBarcode())
                    ? request.getWaybillCode() : request.getPackageBarcode());
        }
        return context.getResult();
    }
}
