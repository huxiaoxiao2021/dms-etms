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
import com.jd.bluedragon.dms.utils.WaybillUtil;

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
				true,
				false,
				false,};
		for(int i=0 ; i < sendPays.length; i++ ){
				System.err.println(sendPays[i]);
				context.setBasePrintWaybill(context.getResponse());
				context.getOldBigWaybillDto().getWaybill().setSendPay(sendPays[i]);
				context.getBasePrintWaybill().setBcSign(null);
				
				//预期验证结果
				boolean checkResult = sendPayChecks[i];
				
				//工具类业务方法校验
				boolean utilsCheck = BusinessUtil.isPreSell(sendPays[i]);
				Assert.assertEquals(utilsCheck,checkResult);
				
				dealReversePrintInfoHandler.handle(context);
				//打标‘预’验证
				boolean hasFlag = (context.getBasePrintWaybill().getBcSign() != null 
						&& context.getBasePrintWaybill().getBcSign().equals("预"));
				Assert.assertEquals(hasFlag,checkResult);
		}
    }
	/**
	 * 重量显示运单重量：一单一件、未称重，包裹无重力显示运单重量
	 * @throws Exception
	 */
    @Test
    public void testSetWayillWeight() throws Exception{
    	WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
    	Double waybillAgainWeight = 1d;
    	Double goodWeight = 2d;
    	String[] waybillCodes = {
				null,
				"JD000000000001",
				"JDWA00000010307",
				"JDY000405106225"};
		boolean[] sendPayChecks ={
				false,
				false,
				true,
				true,
				};
		for(int i=0 ; i < waybillCodes.length; i++ ){
	    	context.getRequest().getWeightOperFlow().setWeight(0);
	    	context.getBigWaybillDto().setPackageList(context.getBigWaybillDto().getPackageList().subList(0, 1));
	    	context.getBasePrintWaybill().setWaybillCode(waybillCodes[i]);
	    	context.getResponse().getPackList().get(0).setWeightAndUnit(null, null);
	    	//取againWeight
	    	context.getBigWaybillDto().getWaybill().setAgainWeight(waybillAgainWeight);
	    	context.getBigWaybillDto().getWaybill().setGoodWeight(goodWeight);
	    	
	    	dealReversePrintInfoHandler.handle(context);
	    	Assert.assertEquals(sendPayChecks[i],waybillAgainWeight.equals(context.getResponse().getPackList().get(0).getWeight()));
	    	//取goodsWeight
	    	context.getResponse().getPackList().get(0).setWeightAndUnit(null, null);
	    	context.getBigWaybillDto().getWaybill().setAgainWeight(null);
	    	context.getBigWaybillDto().getWaybill().setGoodWeight(goodWeight);
	    	dealReversePrintInfoHandler.handle(context);
	    	Assert.assertEquals(sendPayChecks[i],goodWeight.equals(context.getResponse().getPackList().get(0).getWeight()));
		}
    }
}
