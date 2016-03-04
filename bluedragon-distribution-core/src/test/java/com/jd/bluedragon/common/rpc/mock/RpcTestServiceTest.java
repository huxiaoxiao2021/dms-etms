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
		System.out.println(rpcTestService.test("hello"));
	}
}
