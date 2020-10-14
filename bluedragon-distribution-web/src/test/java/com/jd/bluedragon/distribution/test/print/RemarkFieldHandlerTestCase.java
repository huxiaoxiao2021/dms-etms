package com.jd.bluedragon.distribution.test.print;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.print.waybill.handler.RemarkFieldHandler;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.test.utils.UtilsForTestCase;
import com.jd.bluedragon.distribution.testCore.base.EntityUtil;
import com.jd.bluedragon.dms.utils.BusinessUtil;

@RunWith(MockitoJUnitRunner.class)
public class RemarkFieldHandlerTestCase {
	@InjectMocks
	RemarkFieldHandler remarkHandeler;
	@Mock
    private WaybillQueryManager waybillQueryManager;
	/**
	 * 合约机打印需求
	 * @throws Exception
	 */
    @Test
    public void testIsContractPhone() throws Exception{
    	WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
		String[] sendPays = {
				null,
				"",
				"000",
				UtilsForTestCase.getSignString(500,292,'1'),
				UtilsForTestCase.getSignString(500,292,'2'),};
		boolean[] sendPayChecks ={
				false,
				false,
				false,
				true,
				false,};
		for(int i=0 ; i < sendPays.length; i++ ){
				System.err.println(sendPays[i]);
				context.setBasePrintWaybill(context.getResponse());
				context.getBigWaybillDto().getWaybill().setSendPay(sendPays[i]);
				context.getBasePrintWaybill().setRemark(null);
				
				//预期验证结果
				boolean checkResult = sendPayChecks[i];
				
				//工具类业务方法校验
				boolean utilsCheck = BusinessUtil.isContractPhone(sendPays[i]);
				Assert.assertEquals(utilsCheck,checkResult);
				
				remarkHandeler.handle(context);
				//打标‘合约机 需激活’验证
				boolean hasFlag = (context.getBasePrintWaybill().getRemark() != null 
						&& context.getBasePrintWaybill().getRemark().contains("合约机 需激活"));
				Assert.assertEquals(hasFlag,checkResult);
		}
    }
}
