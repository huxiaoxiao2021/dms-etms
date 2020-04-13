package com.jd.bluedragon.distribution.print.waybill.handler.reverse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.TextConstants;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.dms.utils.BusinessUtil;

/**
 * 换单打印面单数据处理
 * @author wuyoude
 *
 */
@Service("dealReversePrintInfoHandler")
public class DealReversePrintInfoHandler implements InterceptHandler<WaybillPrintContext,String> {

    private static final Logger logger = LoggerFactory.getLogger(DealReversePrintInfoHandler.class);

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
    	logger.info("换单打印面单数据处理");
        InterceptResult<String> result = context.getResult();
        String oldSendPay = null;
        if (context.getOldBigWaybillDto() != null && context.getOldBigWaybillDto().getWaybill() != null) {
        	oldSendPay = context.getOldBigWaybillDto().getWaybill().getSendPay();
        }
        //原运单号 SendPay第276位等于Y时，为预售未付全款， 逆向换单后的新单面单打印【预】字
        if(BusinessUtil.isPreSellWithNoPay(oldSendPay)){
        	context.getBasePrintWaybill().setBcSign(TextConstants.PRE_SELL_FLAG);
        }
        return result;
    }
}
