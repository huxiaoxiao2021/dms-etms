package com.jd.bluedragon.common.rpc.mock;

import org.junit.Before;
import org.junit.Test;

public class RpcTestServiceTest {
	
	private RpcTestService rpcTestService;
	
	@Before
	public void setup(){
		rpcTestService = new RpcTestService();
	}

	@Test
	public void test(){
		for(int i=0; i<=5; i++){
			long s = System.currentTimeMillis();
			System.out.println(rpcTestService.test("hello"));
			System.out.println("执行耗时："+(System.currentTimeMillis()-s));
		}	
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
