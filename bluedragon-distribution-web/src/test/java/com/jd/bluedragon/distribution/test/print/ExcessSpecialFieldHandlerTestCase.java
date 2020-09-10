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
import com.jd.bluedragon.dms.utils.BusinessUtil;

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
		String[] waybillCodes = {
				null,
				"",
				"000",
				"JDF000000015384",
				"JDT000000030722"};
		boolean[] checkResults ={
				false,
				false,
				false,
				true,
				true};
		for(int i=0 ; i < waybillCodes.length; i++ ){
				System.err.println(waybillCodes[i]);
				context.setBasePrintWaybill(context.getResponse());
				context.getBasePrintWaybill().setWaybillCode(waybillCodes[i]);
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
