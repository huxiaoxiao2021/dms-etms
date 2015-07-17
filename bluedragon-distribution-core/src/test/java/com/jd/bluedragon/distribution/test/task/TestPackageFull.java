package com.jd.bluedragon.distribution.test.task;

import junit.framework.Assert;

import org.junit.Test;

import com.jd.bluedragon.distribution.reverse.domain.ReverseSendWms;
import com.jd.bluedragon.distribution.reverse.domain.WmsSite;

/**
 * 异地退库ReverseSendWms赋值测试
 * @author huangliang
 *
 */
public class TestPackageFull {

	@Test
	public void testEqual() {

		WmsSite site = new WmsSite();
		site.setCky2(3);
		site.setOrgId(3);
		site.setStoreId(80);
		//2.先从运单取得明细，及出库仓储号cky2\orgId\storeId
		ReverseSendWms send = new ReverseSendWms();
		send.setCky2(3);
		send.setOrgId(3);
		send.setStoreId(80);
		
		//3.判断是否是异地退货，也就是他仓退;默认非本仓
		boolean isOtherStore = true;
		if(send==null){
			isOtherStore = false;//运单获得不了数据，那么从仓储接口试试
		}else if (site.getCky2().equals(send.getCky2())
				&& site.getOrgId().equals(send.getOrgId())
				&& site.getStoreId().equals(send.getStoreId())){
			isOtherStore = false;//表示是生产仓与退货仓是一个仓
		}
		
		
		Assert.assertEquals(false, isOtherStore);
	}

	@Test
	public void testNotEqual() {

		WmsSite site = new WmsSite();
		site.setCky2(3);
		site.setOrgId(3);
		site.setStoreId(80);
		//2.先从运单取得明细，及出库仓储号cky2\orgId\storeId
		ReverseSendWms send = new ReverseSendWms();
		send.setCky2(3);
		send.setOrgId(3);
		send.setStoreId(60);
		
		//3.判断是否是异地退货，也就是他仓退;默认非本仓
		boolean isOtherStore = true;
		if(send==null){
			isOtherStore = false;//运单获得不了数据，那么从仓储接口试试
		}else if (site.getCky2().equals(send.getCky2())
				&& site.getOrgId().equals(send.getOrgId())
				&& site.getStoreId().equals(send.getStoreId())){
			isOtherStore = false;//表示是生产仓与退货仓是一个仓
		}
		
		
		Assert.assertEquals(true, isOtherStore);
	}
}
