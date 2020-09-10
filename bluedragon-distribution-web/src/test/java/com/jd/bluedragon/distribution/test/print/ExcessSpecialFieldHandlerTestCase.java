package com.jd.bluedragon.distribution.test.print;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.distribution.print.waybill.handler.ExcessSpecialFieldHandler;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.test.utils.UtilsForTestCase;
import com.jd.bluedragon.distribution.testCore.base.EntityUtil;

@RunWith(MockitoJUnitRunner.class)
public class ExcessSpecialFieldHandlerTestCase {
	@InjectMocks
	ExcessSpecialFieldHandler excessSpecialFieldHandler;
	/**
	 * 打印显示 【退】
	 * @throws Exception
	 */
    @Test
    public void testSetReverseInfo() throws Exception{
    	WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
		String[] waybillSigns = {
				null,
				"0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
				UtilsForTestCase.markSignChar(UtilsForTestCase.markSignChar("", 61, '1'),4,'0'),
				UtilsForTestCase.markSignChar(UtilsForTestCase.markSignChar("", 15, '1'),4,'0'),
				UtilsForTestCase.markSignChar(UtilsForTestCase.markSignChar("", 61, '1'),4,'1'),
				UtilsForTestCase.markSignChar(UtilsForTestCase.markSignChar("", 61, '1'),4,'9')};
		boolean[] checkResults ={
				false,
				false,
				true,
				true,
				false,
				false};
		for(int i=0 ; i < waybillSigns.length; i++ ){
				System.err.println(waybillSigns[i]);
				context.setBasePrintWaybill(context.getResponse());
				context.getBigWaybillDto().getWaybill().setWaybillSign(waybillSigns[i]);
				context.getBasePrintWaybill().setSpecialMarkNew(null);
				
				//预期验证结果
				boolean checkResult = checkResults[i];
				
				excessSpecialFieldHandler.handle(context);
				//打标‘退’验证
				boolean hasFlag = (context.getBasePrintWaybill().getSpecialMarkNew() != null 
						&& context.getBasePrintWaybill().getSpecialMarkNew().contains("退"));
				Assert.assertEquals(hasFlag,checkResult);
		}
    }
}
