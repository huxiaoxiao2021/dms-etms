package com.jd.bluedragon.distribution.test.print;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.waybill.handler.SitePlateInterceptHandler;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.test.utils.UtilsForTestCase;
import com.jd.bluedragon.distribution.testCore.base.EntityUtil;

@RunWith(MockitoJUnitRunner.class)
public class SitePlateInterceptHandlerTestCase {
	@InjectMocks
	SitePlateInterceptHandler sitePlateInterceptHandler;
	/**
	 * b2c和c2c拦截
	 * @throws Exception
	 */
    @Test
    public void test() throws Exception{
    	WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
		String[] waybillSigns = {
				UtilsForTestCase.markSignChar(UtilsForTestCase.markSignChar("", 25, '1'),29,'8'),
				UtilsForTestCase.markSignChar(UtilsForTestCase.markSignChar("", 25, '1'),29,'1'),};
		
		context.getRequest().setOperateType(100102);
		boolean[] checkResults ={
				true,
				true,};
		for(int i=0 ; i < waybillSigns.length; i++ ){
				System.err.println(waybillSigns[i]);
				context.setBasePrintWaybill(context.getResponse());
				context.getBigWaybillDto().getWaybill().setWaybillSign(waybillSigns[i]);
				context.getResult().toSuccess();
				
				//预期验证结果
				boolean checkResult = checkResults[i];
				
				InterceptResult<String> result = sitePlateInterceptHandler.handle(context);
				
				boolean hasPass = (result.isFailed());
				Assert.assertEquals(hasPass,checkResult);
		}
    }
}
