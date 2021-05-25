package com.jd.bluedragon.core.base;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.distribution.test.utils.UtilsForTestCase;
import com.jd.etms.waybill.domain.Waybill;

import junit.framework.Assert;

/**
 * 
 * @ClassName: WaybillQueryManagerImplTestCase
 * @Description: 测试类
 * @author: wuyoude
 * @date: 2021年5月25日 下午1:47:58
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class WaybillQueryManagerImplTestCase {
	@InjectMocks
	WaybillQueryManagerImpl waybillQueryManagerImpl;
	
	public static void main(String[] args) throws Exception{

	}
	/**
	 * 海运标识测试
	 * @throws Exception
	 */
    @Test
    public void testPrint31H() throws Exception{
    	Waybill waybill = new Waybill();
		String[] waybillSigns = {
				null,
				"",
				"000",
				UtilsForTestCase.getSignString(500,31,'0'),
				UtilsForTestCase.getSignString(500,31,'H'),};
		boolean[] sendPayChecks ={
				false,
				false,
				false,
				false,
				true,
				};
		for(int i=0 ; i < waybillSigns.length; i++ ){
				System.err.println(waybillSigns[i]);
				waybill.setWaybillSign(waybillSigns[i]);
				
				String getTransportMode = waybillQueryManagerImpl.getTransportMode(waybill);
				//‘特快包裹’验证
				boolean hasFlag = ("特快包裹".equals(getTransportMode));
				Assert.assertEquals(sendPayChecks[i],hasFlag);
			}
		}
}
