package com.jd.bluedragon.distribution.test.jmq;

import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.core.base.LDOPManager;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.testCore.base.EntityUtil;
import com.jd.bluedragon.dms.utils.BarCodeType;
/**
 * 
 * @ClassName: CustomerAndConsignerInfoHandlerTestCase
 * @Description: 打印业务-收、寄件人信息处理测试类
 * @author: wuyoude
 * @date: 2020年2月14日 下午4:47:58
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class BdWaybillScheduleMqListenerTestCase {
	@InjectMocks
	BdWaybillScheduleMqListener bdWaybillScheduleMqListener;
    @Mock
    private LDOPManager ldopManager;

	
	public static void main(String[] args) throws Exception{

	}
	/**
	 * 无接触面单测试
	 * @throws Exception
	 */
    @Test
    public void testQueryWaybillByBusiCodeHandler() throws Exception{
    	WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
    	context.getRequest().setBarCodeType(BarCodeType.BUSINESS_ORDER_CODE.getCode());
    	String newWaybillCode = "JD000000000001";
    	when(ldopManager.queryWaybillCodeByOrderIdAndCustomerCode(context.getRequest().getBusinessId(), context.getRequest().getBarCode())).thenReturn(newWaybillCode);
    	queryWaybillByBusiCodeHandler.handle(context);
    	Assert.assertEquals(newWaybillCode,context.getRequest().getBarCode());
	}
}
