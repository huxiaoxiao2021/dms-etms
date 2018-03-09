package com.jd.bluedragon.distribution.print.waybill.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.service.WaybillPrintService;
/**
 * 
 * @ClassName: SignTextWaybillHandler
 * @Description: 包裹标签打印-打标，主要处理waybillSign和sendPay
 * @author: wuyoude
 * @date: 2018年2月27日 下午4:55:11
 *
 */
@Service
public class SignTextWaybillHandler implements Handler<WaybillPrintContext,JdResult<String>>{
	private static final Log logger= LogFactory.getLog(SignTextWaybillHandler.class);
	
    @Autowired
    private WaybillPrintService waybillPrintService;
    
	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		logger.info("包裹标签打印-waybillSign及sendPay打标处理");
		String waybillSign = context.getBigWaybillDto().getWaybill().getWaybillSign();
		String sendPay = context.getBigWaybillDto().getWaybill().getSendPay();
		BasePrintWaybill target = context.getResponse();
		waybillPrintService.dealSignTexts(waybillSign, target, Constants.DIC_NAME_WAYBILL_SIGN_CONFIG);
		waybillPrintService.dealSignTexts(sendPay, target, Constants.DIC_NAME_SEND_PAY_CONFIG);
		return context.getResult();
	}
}
