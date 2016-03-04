package com.jd.bluedragon.common.rpc.mock;

/**
 * 测试远程调用服务。
 * @author yangwubing
 *
 */
public class RpcTestService {
	
	public TestBean test(String username){
		
		//原始调用。
		// /////////////////////////////////////////
		
		//替换为模拟调用。
		return RpcMockProxy.invokeRpc(TestBean.class, "RpcTestSerivce.test", username);
		
	}

}
