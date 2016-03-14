package com.jd.bluedragon.common.rpc.mock;

import org.junit.Before;
import org.junit.Test;

import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

public class RpcTestServiceTest {
	
	private RpcTestService rpcTestService;
	
	@Before
	public void setup(){
		rpcTestService = new RpcTestService();
	}

	@Test
	public void test(){
		RpcMockProxy.invokeRpc(BaseStaffSiteOrgDto.class, "JsfSortingResourceService.check","check");
	}
	
	@Test
	public void radomSleeptime(){
		RpcMockConfig config = new RpcMockConfig();
		for(int i=0; i<100; i++){
			long t = (RpcMockProxy.radomSleeptime(config));
			if(t==1000){
				System.out.println(t);
			}
		}
	}
}
