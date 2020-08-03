package com.jd.bluedragon.distribution.test.print;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import com.jd.bluedragon.distribution.print.waybill.handler.PromiseWaybillHandler;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.testCore.base.EntityUtil;
import com.jd.bluedragon.utils.DateHelper;

//@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
public class PromiseWaybillHandlerTestCase {
	
	public static void main(String[] args) throws Exception{

	}
    @Test
    public void testDealSopJZD() throws Exception{
		PromiseWaybillHandler promiseWaybillHandler = EntityUtil.getInstance(PromiseWaybillHandler.class);
		WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
		Date[][] dates1 = {
				{null,null},
				{null,DateHelper.parseAllFormatDateTime("2019-09-10 09:10:56.456")},
				{DateHelper.parseAllFormatDateTime("2019-09-10 23:10:56.456"),null},
				{DateHelper.parseAllFormatDateTime("2019-09-10 09:10:56.456"),DateHelper.parseAllFormatDateTime("2019-09-10 23:10:56.456")},
				{DateHelper.parseAllFormatDateTime("2019-09-10 09:10:56.456"),DateHelper.parseAllFormatDateTime("2019-09-11 23:10:56.456")},
				};
		String[] results = {
				null,
				"2019-09-10 09:10",
				"2019-09-10 23:10",
				"2019-09-10 09:10-23:10",
				"2019-09-10 09:10-2019-09-11 23:10"};
		int i = 0 ;
		for(Date[] dates :dates1){
			context.getBigWaybillDto().getWaybill().setRequireStartTime(dates[0]);
			context.getBigWaybillDto().getWaybill().setRequireTime(dates[1]);
			context.getBasePrintWaybill().setPromiseText(null);
			promiseWaybillHandler.dealJZD("","000000000000000000000000000000600000000000000000000000000000000",
					context.getBigWaybillDto().getWaybill(),
					context.getBasePrintWaybill());
			if(results[i]!=null){
				Assert.assertEquals(results[i], context.getBasePrintWaybill().getPromiseText());
			}else{
				Assert.assertNull(context.getBasePrintWaybill().getPromiseText());
			}
			System.err.println();
			i++;
		}
    }
}
