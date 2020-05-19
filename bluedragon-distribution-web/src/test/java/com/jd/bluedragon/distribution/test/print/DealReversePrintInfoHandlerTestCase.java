package com.jd.bluedragon.distribution.test.print;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.print.waybill.handler.reverse.DealReversePrintInfoHandler;
import com.jd.bluedragon.distribution.test.utils.UtilsForTestCase;
import com.jd.bluedragon.distribution.testCore.base.EntityUtil;
import com.jd.bluedragon.dms.utils.BusinessUtil;

@RunWith(MockitoJUnitRunner.class)
public class DealReversePrintInfoHandlerTestCase {
	@InjectMocks
	DealReversePrintInfoHandler dealReversePrintInfoHandler;
	/**
	 * 打印显示预
	 * @throws Exception
	 */
    @Test
    public void testPreSell() throws Exception{
    	WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
		String[] sendPays = {
				null,
				"",
				"000",
				UtilsForTestCase.getSignString(500,297,'1'),
				UtilsForTestCase.getSignString(500,297,'2'),
				UtilsForTestCase.getSignString(500,297,'3'),
				UtilsForTestCase.getSignString(500,297,'4'),};
		boolean[] sendPayChecks ={
				false,
				false,
				false,
				true,
				false,
				false,
				false,};
		for(int i=0 ; i < sendPays.length; i++ ){
				System.err.println(sendPays[i]);
				context.setBasePrintWaybill(context.getResponse());
				context.getBigWaybillDto().getWaybill().setSendPay(sendPays[i]);
				context.getBasePrintWaybill().setBcSign(null);
				
				//预期验证结果
				boolean checkResult = sendPayChecks[i];
				
				//工具类业务方法校验
				boolean utilsCheck = BusinessUtil.isPreSellWithNoPay(sendPays[i]);
				Assert.assertEquals(utilsCheck,checkResult);
				
				dealReversePrintInfoHandler.handle(context);
				//打标‘代’验证
				boolean hasFlag = (context.getBasePrintWaybill().getBcSign() != null 
						&& context.getBasePrintWaybill().getBcSign().equals("预"));
				Assert.assertEquals(hasFlag,checkResult);
		}
    }
}
