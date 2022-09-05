package com.jd.bluedragon.distribution.base;

/**
 * 结果处理
 * @author wuyoude
 *
 */
public interface ResultHandler {
	/**
	 * 成功处理
	 */
	void success();
	/**
	 * 失败处理
	 */
	void fail();
	/**
	 * 异常处理
	 */
	void error(Exception e);	
}
