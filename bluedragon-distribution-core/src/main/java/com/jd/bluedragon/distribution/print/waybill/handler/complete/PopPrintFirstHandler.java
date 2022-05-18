package com.jd.bluedragon.distribution.print.waybill.handler.complete;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrintSmsMsg;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;


/**
 * 首次打印发送
 * @author lvyuan21
 */
@Service("popPrintFirstHandler")
public class PopPrintFirstHandler implements Handler<WaybillPrintCompleteContext, JdResult<Boolean>> {

    private static final Logger logger = LoggerFactory.getLogger(PopPrintFirstHandler.class);

    @Autowired
    @Qualifier("popPrintFirstProducer")
    private DefaultJMQProducer popPrintFirstProducer;

    /**
     * 执行处理，返回处理结果
     */
    @Override
    public JdResult<Boolean> handle(WaybillPrintCompleteContext context) {
        if (CollectionUtils.isNotEmpty(context.getFirstPrintPackages())) {
            for (String packageCode : context.getFirstPrintPackages()) {
                // 发送首打消息
                PopPrintSmsMsg msg = createPopPrintSmsMsg(context.getRequest(), packageCode);
                popPrintFirstProducer.sendOnFailPersistent(msg.getPackageBarcode(), JsonHelper.toJson(msg));
            }
        }
        return context.getResult();
    }

    private PopPrintSmsMsg createPopPrintSmsMsg(PrintCompleteRequest request, String packageCode) {
        PopPrintSmsMsg msg = new PopPrintSmsMsg();
        msg.setWaybillCode(WaybillUtil.getWaybillCode(packageCode));
        msg.setPackageBarcode(packageCode);
        msg.setCreateSiteCode(request.getOperateSiteCode());
        if (StringUtils.isNotBlank(request.getOperateTime())) {
            msg.setPrintPackTime(DateHelper.parseDate(request.getOperateTime(), Constants.DATE_TIME_FORMAT));
        } else {
            msg.setPrintPackTime(new Date());
        }
        msg.setPrintPackCode(request.getOperatorCode());
        msg.setPrintPackUser(request.getOperatorName());
        return msg;
    }


}
