package com.jd.bluedragon.distribution.test.print;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import com.jd.bluedragon.common.service.impl.WaybillCommonServiceImpl;
import com.jd.bluedragon.distribution.print.waybill.handler.CustomerAndConsignerInfoHandler;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.testCore.base.EntityUtil;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.DateHelper;

/**
 * 
 * @ClassName: CustomerAndConsignerInfoHandlerTestCase
 * @Description: 打印业务-收、寄件人信息处理测试类
 * @author: wuyoude
 * @date: 2020年2月14日 下午4:47:58
 *
 */
public class NoTouchPrintTestCase {
	
	public static void main(String[] args) throws Exception{

	}
	/**
	 * 无接触面单测试
	 * @throws Exception
	 */
    @Test
    public void testNoTouch() throws Exception{
    	//CustomerAndConsignerInfoHandler customerAndConsignerInfoHandler = EntityUtil.getInstance(CustomerAndConsignerInfoHandler.class);
    	WaybillCommonServiceImpl waybillCommonServiceImpl = new WaybillCommonServiceImpl();
    	WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
		String[] sendPays = {null,"","000",};
		boolean[] sendPayChecks ={false,false,false,};
		String[] waybillSigns = {null,"","000",};
		boolean[] waybillSignChecks ={false,false,false,};
		
		for(int i=0 ; i < sendPays.length; i++ ){
			for(int j=0 ; j < waybillSigns.length; j++ ){
				context.getBigWaybillDto().getWaybill().setSendPay(sendPays[i]);
				context.getBigWaybillDto().getWaybill().setWaybillSign(waybillSigns[j]);
				context.getBasePrintWaybill().setWaybillSignText(null);
				//customerAndConsignerInfoHandler.handle(context);
				waybillCommonServiceImpl.setBasePrintInfoByWaybill(context.getBasePrintWaybill(), context.getBigWaybillDto().getWaybill());
				boolean isNoTouch = BusinessUtil.isNoTouchService(sendPays[i], waybillSigns[j]);
				Assert.assertEquals((sendPayChecks[i]||waybillSignChecks[j]),isNoTouch);
				if(isNoTouch){
					Assert.assertEquals("代", context.getBasePrintWaybill().getWaybillSignText());
				}else{
					Assert.assertNull(context.getBasePrintWaybill().getWaybillSignText());
				}
			}
		}
	}
}
