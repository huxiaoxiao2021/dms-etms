package com.jd.bluedragon.common.rpc.mock;

import java.util.Map;

/**
 * 远程方法模拟调用配置。
 * @author yangwubing
 *
 */
public class RpcMockConfig {
	
	/**
	 * 接口TP99值，单位毫秒。
	 */
	private long tp99 = 50;
	
	/**
	 * 接口MAX值，单位毫秒。
	 */
	private long max = 1000;
	
	/**
	 * 返回值的配置信息。
	 */
	private Map<String, String> returnValueConfig;

	public long getTp99() {
		return tp99;
	}

	public void setTp99(long tp99) {
		this.tp99 = tp99;
	}

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		this.max = max;
	}

	public Map<String, String> getReturnValueConfig() {
		return returnValueConfig;
	}

	public void setReturnValueConfig(Map<String, String> returnValueConfig) {
		this.returnValueConfig = returnValueConfig;
	}
	

}
